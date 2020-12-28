package com.wnagj.meets.service;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.wnagj.framework.bmob.BmobManager;
import com.wnagj.framework.bmob.IMUser;
import com.wnagj.framework.cloud.CloudManager;
import com.wnagj.framework.db.LitePalHelper;
import com.wnagj.framework.db.NewFriend;
import com.wnagj.framework.entity.Constants;
import com.wnagj.framework.event.EventManager;
import com.wnagj.framework.event.MessageEvent;
import com.wnagj.framework.gson.TextBean;
import com.wnagj.framework.helper.GlideHelper;
import com.wnagj.framework.util.CommonUtils;
import com.wnagj.framework.util.LogUtils;
import com.wnagj.framework.util.SpUtils;
import com.wnagj.meets.MainActivity;
import com.wnagj.meets.R;
import com.wnagj.meets.ui.ChatActivity;
import com.wnagj.meets.ui.NewFriendActivity;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

public class CloudService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        linkCloudServer();
    }

    /**
     * 连接云服务
     */
    private void linkCloudServer() {
        //获取Token
        String token = SpUtils.getInstance().getString(Constants.SP_TOKEN, "");
        LogUtils.e("token:" + token);
        //连接服务
        CloudManager.getInstance().connect(token);
        //接收消息
        CloudManager.getInstance().setOnReceiveMessageListener((message, i) -> {
            parsingImMessage(message);
            return false;
        });
    }

    /**
     * 解析消息体
     * @param message
     */
    @SuppressLint("CheckResult")
    private void parsingImMessage(Message message) {
        LogUtils.i("message:" + message);
        String objectName = message.getObjectName();
        //文本消息
        if (objectName.equals(CloudManager.MSG_TEXT_NAME)) {

            //获取消息的主体
            TextMessage textMessage = (TextMessage) message.getContent();
            String content = textMessage.getContent();
            LogUtils.i("content = " + content);
            TextBean textBean = new Gson().fromJson(content, TextBean.class);


            if (textBean.getType().equals(CloudManager.TYPE_TEXT)) {
                //普通消息
                MessageEvent event = new MessageEvent(EventManager.FLAG_SEND_TEXT);
                event.setText(textBean.getMsg());
                event.setUserId(message.getSenderUserId());
                EventManager.post(event);
                pushSystem(message.getSenderUserId(), 1, 0, 0, textBean.getMsg());

            } else if (textBean.getType().equals(CloudManager.TYPE_ADD_FRIEND)) {
                //添加好友的消息
                //查询数据库如果有重复的则不添加
                LogUtils.i("添加好友消息");
                saveNewFriend(textBean.getMsg(), message.getSenderUserId());

            } else if (textBean.getType().equals(CloudManager.TYPE_ARGEED_FRIEND)) {
                //同意添加好友消息
                //1.添加到好友列表
                BmobManager.getInstance().addFriend(message.getSenderUserId(), new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            pushSystem(message.getSenderUserId(), 0, 1, 0, "");
                            //2.刷新好友列表
                            EventManager.post(EventManager.FLAG_UPDATE_FRIEND_LIST);
                        }
                    }
                });
            }

        }
    }
    /**
     * 保存新朋友
     *
     * @param msg
     * @param senderUserId
     */
    private void saveNewFriend(String msg, String senderUserId) {
        pushSystem(senderUserId, 0, 0, 0, msg);
        System.out.println("senderUserId : "+ senderUserId);
        LitePalHelper.getInstance().saveNewFriend(msg, senderUserId);
    }

    /**
     * @param id          发消息id
     * @param type        0：特殊消息 1：聊天消息
     * @param friendType  0: 添加好友请求 1：同意好友请求
     * @param messageType 0：文本  1：图片 2：位置
     */
    private void pushSystem(final String id, final int type, final int friendType, final int messageType, final String msgText) {
        LogUtils.i("pushSystem");
        BmobManager.getInstance().queryObjectIdUser(id, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if (e == null) {
                    if (CommonUtils.isEmpty(list)) {
                        IMUser imUser = list.get(0);
                        String text = "";
                        if (type == 0) {
                            switch (friendType) {
                                case 0:
                                    text = imUser.getNickName() + getString(R.string.text_server_noti_send_text);
                                    break;
                                case 1:
                                    text = imUser.getNickName() + getString(R.string.text_server_noti_receiver_text);
                                    break;
                            }
                        } else if (type == 1) {
                            switch (messageType) {
                                case 0:
                                    text = msgText;
                                    break;
                                case 1:
                                    text = getString(R.string.text_chat_record_img);
                                    break;
                                case 2:
                                    text = getString(R.string.text_chat_record_location);
                                    break;
                            }
                        }
                        pushBitmap(type, friendType, imUser, imUser.getNickName(), text, imUser.getPhoto());
                    }
                }
            }
        });
    }

    /**
     * 发送通知
     *
     * @param type       0：特殊消息 1：聊天消息
     * @param friendType 0: 添加好友请求 1：同意好友请求
     * @param imUser     用户对象
     * @param title      标题
     * @param text       内容
     * @param url        头像Url
     */
    private void pushBitmap(final int type, final int friendType, final IMUser imUser, final String title, final String text, String url) {
        LogUtils.i("pushBitmap");
        GlideHelper.loadUrlToBitmap(this, url, new GlideHelper.OnGlideBitmapResultListener() {
            @Override
            public void onResourceReady(Bitmap resource) {
                if (type == 0) {
                    if (friendType == 0) {
                        Intent intent = new Intent(CloudService.this, NewFriendActivity.class);
                        PendingIntent pi = PendingIntent.getActivities(CloudService.this, 0, new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);
//                        NotificationHelper.getInstance().pushAddFriendNotification(imUser.getObjectId(), title, text, resource, pi);
                    } else if (friendType == 1) {
                        Intent intent = new Intent(CloudService.this, MainActivity.class);
                        PendingIntent pi = PendingIntent.getActivities(CloudService.this, 0, new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);
//                        NotificationHelper.getInstance().pushArgeedFriendNotification(imUser.getObjectId(), title, text, resource, pi);
                    }
                } else if (type == 1) {
                    Intent intent = new Intent(CloudService.this, ChatActivity.class);
                    intent.putExtra(Constants.INTENT_USER_ID, imUser.getObjectId());
                    intent.putExtra(Constants.INTENT_USER_NAME, imUser.getNickName());
                    intent.putExtra(Constants.INTENT_USER_PHOTO, imUser.getPhoto());
                    PendingIntent pi = PendingIntent.getActivities(CloudService.this, 0, new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);
//                    NotificationHelper.getInstance().pushMessageNotification(imUser.getObjectId(), title, text, resource, pi);
                }
            }
        });
    }


}
