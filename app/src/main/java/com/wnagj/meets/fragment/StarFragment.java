package com.wnagj.meets.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxun.tagcloudlib.view.TagCloudView;
import com.wnagj.framework.base.BaseFragment;
import com.wnagj.framework.bmob.BmobManager;
import com.wnagj.framework.manager.DialogManager;
import com.wnagj.meets.R;
import com.wnagj.meets.adapter.CloudTagAdapter;
import com.wnagj.meets.model.StarModel;
import com.wnagj.meets.ui.AddFriendActivity;
import com.wnagj.meets.ui.QrCodeActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StarFragment extends BaseFragment {

    @BindView(R.id.tv_star_title)
    TextView tvStarTitle;
    @BindView(R.id.iv_camera)
    ImageView ivCamera;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.tv_connect_status)
    TextView tvConnectStatus;
    @BindView(R.id.mCloudView)
    TagCloudView mCloudView;
    @BindView(R.id.tv_random)
    TextView tvRandom;
    @BindView(R.id.ll_random)
    LinearLayout llRandom;
    @BindView(R.id.tv_soul)
    TextView tvSoul;
    @BindView(R.id.ll_soul)
    LinearLayout llSoul;
    @BindView(R.id.tv_fate)
    TextView tvFate;
    @BindView(R.id.ll_fate)
    LinearLayout llFate;
    @BindView(R.id.tv_love)
    TextView tvLove;
    @BindView(R.id.ll_love)
    LinearLayout llLove;

    private List<StarModel> mStarList;

    private CloudTagAdapter mCloudTagAdapter;


    //二维码结果
    private static final int REQUEST_CODE = 1235;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_star, null);
        ButterKnife.bind(this,view);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mStarList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            StarModel model = new StarModel();
            model.setNickName("测试 " + i);
            mStarList.add(model);
        }

        mCloudTagAdapter = new CloudTagAdapter(getActivity(), mStarList);
        mCloudView.setAdapter(mCloudTagAdapter);
    }


    @OnClick({R.id.iv_camera,R.id.iv_add,R.id.ll_random,
            R.id.ll_soul})
    void onClick(View view){
        switch (view.getId()) {
            case R.id.iv_camera:
                //扫描
                Intent intent = new Intent(getActivity(), QrCodeActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.iv_add:
                //添加好友
                startActivity(new Intent(getActivity(), AddFriendActivity.class));
                break;
            case R.id.ll_random:
                //随机匹配
//                pairUser(0);
                break;
            case R.id.ll_soul:
                //灵魂匹配
//                if(TextUtils.isEmpty(BmobManager.getInstance().getUser().getConstellation())){
//                    tv_null_text.setText(getString(R.string.text_star_par_tips_1));
//                    DialogManager.getInstance().show(mNullDialogView);
//                    return;
//                }
//
//                if(BmobManager.getInstance().getUser().getAge() == 0){
//                    tv_null_text.setText(getString(R.string.text_star_par_tips_2));
//                    DialogManager.getInstance().show(mNullDialogView);
//                    return;
//                }
//
//                if(TextUtils.isEmpty(BmobManager.getInstance().getUser().getHobby())){
//                    tv_null_text.setText(getString(R.string.text_star_par_tips_3));
//                    DialogManager.getInstance().show(mNullDialogView);
//                    return;
//                }
//
//                if(TextUtils.isEmpty(BmobManager.getInstance().getUser().getStatus())){
//                    tv_null_text.setText(getString(R.string.text_star_par_tips_4));
//                    DialogManager.getInstance().show(mNullDialogView);
//                    return;
//                }
//
//                //灵魂匹配
//                pairUser(1);
                break;
            case R.id.ll_fate:
                //缘分匹配
//                pairUser(2);
                break;
            case R.id.ll_love:
                //恋爱匹配
//                pairUser(3);
                break;
        }
    }
}
