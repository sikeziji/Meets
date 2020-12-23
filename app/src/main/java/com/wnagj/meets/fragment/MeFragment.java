package com.wnagj.meets.fragment;

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
import com.wnagj.framework.helper.GlideHelper;
import com.wnagj.meets.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

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

    }

    /**
     * 加载我的个人信息
     */
    private void loadMeInfo() {

        IMUser user = BmobManager.getInstance().getUser();
        GlideHelper.loadUrl(getActivity(),user.getPhoto(),ivMePhoto);
        tvNickname.setText(user.getNickName());


    }
}
