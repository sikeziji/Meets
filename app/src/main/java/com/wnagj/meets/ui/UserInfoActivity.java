package com.wnagj.meets.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wnagj.framework.adapter.CommonAdapter;
import com.wnagj.framework.adapter.CommonViewHolder;
import com.wnagj.framework.base.BaseUIActicity;
import com.wnagj.framework.bmob.BmobManager;
import com.wnagj.framework.bmob.Friend;
import com.wnagj.framework.bmob.IMUser;
import com.wnagj.framework.cloud.CloudManager;
import com.wnagj.framework.entity.Constants;
import com.wnagj.framework.helper.GlideHelper;
import com.wnagj.framework.manager.DialogManager;
import com.wnagj.framework.util.CommonUtils;
import com.wnagj.framework.util.LogUtils;
import com.wnagj.framework.view.DialogView;
import com.wnagj.meets.R;
import com.wnagj.meets.model.UserinfoModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends BaseUIActicity {

    /**
     * 1. 根据传递过来的ID 查询用户信息并且显示
     * - 普通信息
     * - 构建一个RecycleView 宫格
     * 2. 建立好友关系模型
     * 3. 实现添加好友的提示框
     * 4. 发送添加好友的消息
     * 5. 接受好友的消息
     */

    @BindView(R.id.ll_back)
    RelativeLayout llBack;
    @BindView(R.id.iv_user_photo)
    CircleImageView ivUserPhoto;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.mUserInfoView)
    RecyclerView mUserInfoView;
    @BindView(R.id.btn_add_friend)
    Button btnAddFriend;
    @BindView(R.id.btn_chat)
    Button btnChat;
    @BindView(R.id.btn_audio_chat)
    Button btnAudioChat;
    @BindView(R.id.btn_video_chat)
    Button btnVideoChat;
    @BindView(R.id.ll_is_friend)
    LinearLayout llIsFriend;
//    @BindView(R.id.et_msg)
//    EditText etMsg;

    //用户ID
    private String userId;
    private CommonAdapter<UserinfoModel> mUserInfoAdapter;
    private List<UserinfoModel> mUserInfoList = new ArrayList<>();
    //个人信息颜色
    private int[] mColor = {0x881E90FF, 0x8800FF7F, 0x88FFD700, 0x88FF6347, 0x88F08080, 0x8840E0D0};

    private DialogView mAddFriendDialogView;
    private EditText et_msg;
    private TextView tv_cancel;
    private TextView tv_add_friend;
    private IMUser mImUser;

    /**
     * 跳转Activity
     *
     * @param mContext
     * @param userId
     */
    public static void startActivity(Context mContext, String userId) {
        Intent intent = new Intent(mContext, UserInfoActivity.class);
        intent.putExtra(Constants.INTENT_USER_ID, userId);
        mContext.startActivity(intent);
    }

    @Override
    protected void initView() {

        initAddFriendDialog();

        userId = getIntent().getStringExtra(Constants.INTENT_USER_ID);

        mUserInfoAdapter = new CommonAdapter<UserinfoModel>(mUserInfoList, new CommonAdapter.OnBindDataListener<UserinfoModel>() {
            @Override
            public void onBindViewHolder(UserinfoModel model, CommonViewHolder viewHolder, int type, int position) {
                viewHolder.getView(R.id.ll_bg).setBackgroundColor(model.getBgColor());
                viewHolder.setText(R.id.tv_type, model.getTitle());
                viewHolder.setText(R.id.tv_content, model.getContent());

            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_user_info_item;
            }
        });
        mUserInfoView.setLayoutManager(new GridLayoutManager(this, 3));
        mUserInfoView.setAdapter(mUserInfoAdapter);

        queryUserInfo();


    }

    /**
     * 添加好友的提示框
     */
    private void initAddFriendDialog() {
        mAddFriendDialogView = DialogManager.getInstance().initView(this, R.layout.dialog_send_friend);
        et_msg = (EditText) mAddFriendDialogView.findViewById(R.id.et_msg);
        tv_cancel = (TextView) mAddFriendDialogView.findViewById(R.id.tv_cancel);
        tv_add_friend = (TextView) mAddFriendDialogView.findViewById(R.id.tv_add_friend);

        et_msg.setText(getString(R.string.text_me_info_tips) + BmobManager.getInstance().getUser().getNickName());
        tv_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加好友
                String msg = et_msg.getText().toString().trim();
                if (TextUtils.isEmpty(msg)) {
                    msg = getString(R.string.text_user_info_add_friend);
                    return;
                }

                CloudManager.getInstance().sendTextMessage(msg,
                        CloudManager.TYPE_ADD_FRIEND, userId);

                DialogManager.getInstance().hide(mAddFriendDialogView);

                Toast.makeText(UserInfoActivity.this, getString(R.string.text_user_resuest_succeed), Toast.LENGTH_SHORT).show();

            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏添加好友的dialog
                DialogManager.getInstance().hide(mAddFriendDialogView);
            }
        });

    }

    private void queryUserInfo() {
        LogUtils.i("userId = "+ userId);

        if (TextUtils.isEmpty(userId)) {
            return;
        }
        BmobManager.getInstance().queryObjectIdUser(userId, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if (e == null) {
                    if (CommonUtils.isEmpty(list)) {
                        mImUser = list.get(0);
                        updateUserInfo(mImUser);
                    }
                }
            }
        });

        //判断好友关系
        BmobManager.getInstance().queryMyFriends(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                if (e == null) {
                    if (CommonUtils.isEmpty(list)) {
                        //判断这个对象中的id是否跟我目前的userId相同
                        for (int i = 0; i < list.size(); i++) {
                            Friend friend = list.get(i);
                            //判断这个对象中的id是否跟我目前的userId相同
                            if (friend.getFriendUser().getObjectId().equals(userId)) {
                                //你们是好友关系
                                btnAddFriend.setVisibility(View.GONE);
                                llIsFriend.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                }
            }
        });


    }

    /**
     * 更新用户信息
     *
     * @param imUser
     */
    private void updateUserInfo(IMUser imUser) {
        //设置基本属性
        GlideHelper.loadUrl(UserInfoActivity.this,
                imUser.getPhoto(), ivUserPhoto);
        tvNickname.setText(imUser.getNickName());
        tvDesc.setText(imUser.getDesc());

        //性别 年龄 生日 星座 爱好 单身状态
        addUserInfoModel(mColor[0], getString(R.string.text_me_info_sex), imUser.isSex() ? getString(R.string.text_me_info_boy) : getString(R.string.text_me_info_girl));
        addUserInfoModel(mColor[1], getString(R.string.text_me_info_age), imUser.getAge() + getString(R.string.text_search_age));
        addUserInfoModel(mColor[2], getString(R.string.text_me_info_birthday), imUser.getBirthday());
        addUserInfoModel(mColor[3], getString(R.string.text_me_info_constellation), imUser.getConstellation());
        addUserInfoModel(mColor[4], getString(R.string.text_me_info_hobby), imUser.getHobby());
        addUserInfoModel(mColor[5], getString(R.string.text_me_info_status), imUser.getStatus());
        //刷新数据
        mUserInfoAdapter.notifyDataSetChanged();
    }


    /**
     * 添加数据
     *
     * @param color
     * @param title
     * @param content
     */
    private void addUserInfoModel(int color, String title, String content) {
        UserinfoModel model = new UserinfoModel();
        model.setBgColor(color);
        model.setTitle(title);
        model.setContent(content);
        mUserInfoList.add(model);
    }


    @Override
    protected int getResLayout() {
        return R.layout.activity_user_info;
    }

    @OnClick({R.id.ll_back, R.id.btn_add_friend, R.id.btn_chat,
            R.id.btn_audio_chat, R.id.btn_video_chat,
          R.id.iv_user_photo})
    void onClick(View view) {
        switch (view.getId()) {

            case R.id.ll_back:
                finish();
                break;
//            case R.id.iv_user_photo:
//                ImagePreviewActivity.startActivity(this, true, imUser.getPhoto());
//                break;
            case R.id.btn_add_friend:
                DialogManager.getInstance().show(mAddFriendDialogView);
                break;
            case R.id.btn_chat:
                ChatActivity.startActivity(UserInfoActivity.this,
                        userId,mImUser.getNickName(),mImUser.getPhoto());
                break;
            case R.id.btn_audio_chat:
                finish();
                break;
            case R.id.btn_video_chat:
                finish();
                break;
        }

    }

}
