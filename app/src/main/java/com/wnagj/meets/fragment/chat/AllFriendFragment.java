package com.wnagj.meets.fragment.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wnagj.framework.adapter.CommonAdapter;
import com.wnagj.framework.adapter.CommonViewHolder;
import com.wnagj.framework.base.BaseFragment;
import com.wnagj.framework.bmob.BmobManager;
import com.wnagj.framework.bmob.Friend;
import com.wnagj.framework.bmob.IMUser;
import com.wnagj.framework.event.EventManager;
import com.wnagj.framework.event.MessageEvent;
import com.wnagj.framework.util.CommonUtils;
import com.wnagj.framework.util.LogUtils;
import com.wnagj.meets.R;
import com.wnagj.meets.model.AllFriendModel;
import com.wnagj.meets.ui.UserInfoActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AllFriendFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.mAllFriendView)
    RecyclerView mAllFriendView;
    @BindView(R.id.item_empty_view)
    View item_empty_view;
    @BindView(R.id.mAllFriendRefreshLayout)
    SwipeRefreshLayout mAllFriendRefreshLayout;

    private Unbinder mBind;

    CommonAdapter<AllFriendModel> mAllFriendAdapter;
    private List<AllFriendModel> mList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_record, null);
        mBind = ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    /**
     * 初始化视图
     *
     * @param view
     */
    private void initView(View view) {

        mAllFriendRefreshLayout.setOnRefreshListener(this);

        mAllFriendView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAllFriendView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        mAllFriendAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnBindDataListener<AllFriendModel>() {
            @Override
            public void onBindViewHolder(AllFriendModel model, CommonViewHolder viewHolder, int type, int position) {
                viewHolder.setImageUrl(getActivity(), R.id.iv_photo, model.getUrl());
                viewHolder.setText(R.id.tv_nickname, model.getNickName());
                viewHolder.setImageResource(R.id.iv_sex, model.isSex()
                        ? R.drawable.img_boy_icon : R.drawable.img_girl_icon);
                viewHolder.setText(R.id.tv_desc, model.getDesc());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfoActivity.startActivity(getActivity(), model.getUserId());
                    }
                });
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_all_friend_item;
            }
        });
        mAllFriendView.setAdapter(mAllFriendAdapter);

        queryMyFriends();

    }

    /**
     * 查询所有的好友
     */
    private void queryMyFriends() {
        //设置正在刷新中
        mAllFriendRefreshLayout.setRefreshing(true);
        BmobManager.getInstance().queryMyFriends(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                mAllFriendRefreshLayout.setRefreshing(false);
                if (e == null) {
                    if (CommonUtils.isEmpty(list)) {
                        item_empty_view.setVisibility(View.GONE);
                        mAllFriendView.setVisibility(View.VISIBLE);
                        if (mList.size()>0) {
                            mList.clear();
                        }
                        LogUtils.i("list:" + list.size());

                        for (int i = 0; i < list.size(); i++) {
                            Friend friend = list.get(i);
                            String id = friend.getFriendUser().getObjectId();
                            BmobManager.getInstance().queryObjectIdUser(id, new FindListener<IMUser>() {
                                @Override
                                public void done(List<IMUser> list, BmobException e) {
                                    if (e == null) {
                                        if (CommonUtils.isEmpty(list)) {
                                            IMUser imUser = list.get(0);
                                            AllFriendModel model = new AllFriendModel();
                                            model.setUserId(imUser.getObjectId());
                                            model.setUrl(imUser.getPhoto());
                                            model.setNickName(imUser.getNickName());
                                            model.setSex(imUser.isSex());
                                            model.setDesc(getString(R.string.text_all_friend_desc) + imUser.getDesc());
                                            mList.add(model);
                                            mAllFriendAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                        }

                    }else{
                        item_empty_view.setVisibility(View.VISIBLE);
                        mAllFriendView.setVisibility(View.GONE);
                    }
                }
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBind != null) {
            mBind.unbind();
        }
    }

    @Override
    public void onRefresh() {
        if (mAllFriendRefreshLayout.isRefreshing()) {
            queryMyFriends();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getType()) {
            case EventManager.FLAG_UPDATE_FRIEND_LIST:
                if (!mAllFriendRefreshLayout.isRefreshing()) {
                    queryMyFriends();
                }
                break;
        }
    }
}
