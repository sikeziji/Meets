package com.wnagj.meets.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wnagj.framework.base.BaseFragment;
import com.wnagj.framework.bmob.BmobManager;
import com.wnagj.framework.bmob.IMUser;
import com.wnagj.framework.cloud.CloudManager;
import com.wnagj.framework.event.EventManager;
import com.wnagj.framework.event.MessageEvent;
import com.wnagj.framework.helper.GlideHelper;
import com.wnagj.framework.util.LogUtils;
import com.wnagj.meets.R;
import com.wnagj.meets.ui.NewFriendActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imlib.RongIMClient;

public class MeFragment extends BaseFragment {

    @BindView(R.id.iv_me_photo)
    CircleImageView ivMePhoto;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_server_status)
    TextView tvServerStatus;
    @BindView(R.id.ll_me_info)
    LinearLayout llMeInfo;
    @BindView(R.id.ll_new_friend)
    LinearLayout llNewFriend;
    @BindView(R.id.ll_private_set)
    LinearLayout llPrivateSet;
    @BindView(R.id.ll_share)
    LinearLayout llShare;
    @BindView(R.id.ll_notice)
    LinearLayout llNotice;
    @BindView(R.id.ll_setting)
    LinearLayout llSetting;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        ButterKnife.bind(this,view);
        initView(view);
        return view;
    }

    private void initView(View view) {

        loadMeInfo();

        //监听连接状态
        CloudManager.getInstance().setConnectionStatusListener(connectionStatus -> {
            if(isAdded()){
                if (null != connectionStatus) {
                    LogUtils.i("connectionStatus:" + connectionStatus);
                    if (connectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
                        //连接成功
                        tvServerStatus.setText(getString(R.string.text_server_status_text_1));
                    } else if (connectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING) {
                        //连接中
                        tvServerStatus.setText(getString(R.string.text_server_status_text_2));
                    } else if (connectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.UNCONNECTED) {
                        //断开连接
                        tvServerStatus.setText(getString(R.string.text_server_status_text_3));
                    } else if (connectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
                        //用户在其他地方登陆
                    } else if (connectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE) {
                        //网络不可用
                        tvServerStatus.setText(getString(R.string.text_server_status_text_4));
                    } else if (connectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONN_USER_BLOCKED) {
                        //服务器异常
                        tvServerStatus.setText(getString(R.string.text_server_status_text_5));
                    } else if (connectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.TOKEN_INCORRECT) {
                        //Token不正确
                        tvServerStatus.setText(getString(R.string.text_server_status_text_6));
                    }
                }
            }
        });

    }

    /**
     * 加载我的个人信息
     */
    private void loadMeInfo() {

        IMUser user = BmobManager.getInstance().getUser();
        GlideHelper.loadUrl(getActivity(),user.getPhoto(),ivMePhoto);
        tvNickname.setText(user.getNickName());


    }

    @OnClick({R.id.ll_me_info,R.id.ll_new_friend,R.id.ll_private_set,R.id.ll_share,
            R.id.ll_notice,R.id.ll_setting})
    void onClick(View view){
        switch (view.getId()) {
            case R.id.ll_me_info:
                //个人信息
//                startActivity(new Intent(getActivity(), MeInfoActivity.class));
                break;
            case R.id.ll_new_friend:
                //新朋友
                startActivity(new Intent(getActivity(), NewFriendActivity.class));
                break;
            case R.id.ll_private_set:
                //隐私设置
//                startActivity(new Intent(getActivity(), PrivateSetActivity.class));
                break;
            case R.id.ll_share:
                //分享
//                startActivity(new Intent(getActivity(), ShareImgActivity.class));
                break;
            case R.id.ll_notice:
                //通知
//                startActivity(new Intent(getActivity(), NoticeActivity.class));
                break;
            case R.id.ll_setting:
                //设置
//                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getType()) {
            case EventManager.EVENT_REFRE_ME_INFO:
                loadMeInfo();
                break;
        }
    }
}
