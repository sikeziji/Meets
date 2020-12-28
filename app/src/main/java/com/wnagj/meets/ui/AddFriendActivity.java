package com.wnagj.meets.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wnagj.framework.adapter.CommonAdapter;
import com.wnagj.framework.adapter.CommonViewHolder;
import com.wnagj.framework.base.BaseBackActivity;
import com.wnagj.framework.bmob.BmobManager;
import com.wnagj.framework.bmob.IMUser;
import com.wnagj.framework.manager.KeyWordManager;
import com.wnagj.framework.util.CommonUtils;
import com.wnagj.framework.util.LogUtils;
import com.wnagj.meets.R;
import com.wnagj.meets.model.AddFriendModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AddFriendActivity extends BaseBackActivity {


    /**
     * 1. 模拟用户数据
     * 2. 根据条件查询
     * 3. 推荐好友
     */

    //标题
    public static final int TYPE_TITLE = 0;
    //内容
    public static final int TYPE_CONTENT = 1;

    @BindView(R.id.ll_to_contact)
    LinearLayout llToContact;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.mSearchResultView)
    RecyclerView mSearchResultView;


    @BindView(R.id.include_empty_view)
    View include_empty_view;


    private CommonAdapter mAddFriendAdapter;
    private List<AddFriendModel> mList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        //列表的实现
        mSearchResultView.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mAddFriendAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnMoreBindDataListener<AddFriendModel>() {
            @Override
            public int getItemType(int position) {
                return mList.get(position).getType();
            }

            @Override
            public void onBindViewHolder(AddFriendModel model, CommonViewHolder viewHolder, int type, int position) {
                if (type == TYPE_TITLE) {
                    viewHolder.setText(R.id.tv_title, model.getTitle());
                } else if (type == TYPE_CONTENT) {
                    //设置头像
                    viewHolder.setImageUrl(AddFriendActivity.this, R.id.iv_photo, model.getPhoto());
                    //设置性别
                    viewHolder.setImageResource(R.id.iv_sex,
                            model.isSex() ? R.drawable.img_boy_icon : R.drawable.img_girl_icon);
                    //设置昵称
                    viewHolder.setText(R.id.tv_nickname, model.getNickName());
                    //年龄
                    viewHolder.setText(R.id.tv_ages, model.getAge() + getString(R.string.text_search_age));
                    //设置描述
                    viewHolder.setText(R.id.tv_desc, model.getDesc());

                    //点击事件
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserInfoActivity.startActivity(AddFriendActivity.this,
                                    model.getUserId());
                        }
                    });
                }
            }

            @Override
            public int getLayoutId(int type) {
                if (type == TYPE_TITLE) {
                    return  R.layout.layout_search_title_item;
                }else if (type == TYPE_CONTENT){
                    return  R.layout.layout_search_user_item;
                }
                return 0;
            }
        });

        mSearchResultView.setAdapter(mAddFriendAdapter);


    }


    private void queryPhoneUser() {
        //1.获取电话号码
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.text_login_phone_null),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        //2.过滤自己
        String phoneNumber = BmobManager.getInstance().getUser().getMobilePhoneNumber();
        LogUtils.i("phoneNumber:" + phoneNumber);
        if (phone.equals(phoneNumber)) {
            Toast.makeText(this, getString(R.string.text_add_friend_no_me), Toast.LENGTH_SHORT).show();
            return;
        }

        //2.查询
        BmobManager.getInstance().queryPhoneUser(phone, new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                KeyWordManager.getInstance().hideKeyWord(AddFriendActivity.this);
                if (e != null) {
                    return;
                }
                if (CommonUtils.isEmpty(list)) {
                    IMUser imUser = list.get(0);
                    include_empty_view.setVisibility(View.GONE);
                    mSearchResultView.setVisibility(View.VISIBLE);

                    //每次查询有数据则清空
                    mList.clear();

                    addTitle(getString(R.string.text_add_friend_title));
                    addContent(imUser);
                    mAddFriendAdapter.notifyDataSetChanged();

                    //推荐
                    pushUser(phone);
                } else {
                    //显示空数据
                    include_empty_view.setVisibility(View.VISIBLE);
                    mSearchResultView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void addContent(IMUser imUser) {
        AddFriendModel model = new AddFriendModel();
        model.setType(TYPE_CONTENT);
        model.setUserId(imUser.getObjectId());
        model.setPhoto(imUser.getPhoto());
        model.setSex(imUser.isSex());
        model.setAge(imUser.getAge());
        model.setNickName(imUser.getNickName());
        model.setDesc(imUser.getDesc());
        mList.add(model);
    }

    private void addTitle(String title) {
        AddFriendModel model = new AddFriendModel();
        model.setType(TYPE_TITLE);
        model.setTitle(title);
        mList.add(model);
    }

    /**
     * 推荐好友
     *
     * @param phone 过滤所查询的电话号码
     */
    private void pushUser(String phone) {
        //查询所有的好友 取100个
        BmobManager.getInstance().queryAllUser(new FindListener<IMUser>() {
            @Override
            public void done(List<IMUser> list, BmobException e) {
                if (e == null) {
                    if (CommonUtils.isEmpty(list)) {
                        addTitle(getString(R.string.text_add_friend_content));
                        int num = (list.size() <= 100) ? list.size() : 100;
                        for (int i = 0; i < num; i++) {
                            //也不能自己推荐给自己
                            String phoneNumber = BmobManager.getInstance().getUser().getMobilePhoneNumber();
                            if (list.get(i).getMobilePhoneNumber().equals(phoneNumber)) {
                                //跳过本次循环
                                continue;
                            }
                            //也不能查询到所查找的好友
                            if (list.get(i).getMobilePhoneNumber().equals(phone)) {
                                //跳过本次循环
                                continue;
                            }

                            addContent(list.get(i));
                        }
                        mAddFriendAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @OnClick({R.id.ll_to_contact,R.id.iv_search})
    void onClick(View view){
        switch (view.getId()) {
            //跳转到从通讯录导入
            case R.id.ll_to_contact:
                //处理权限
                if (checkPermissions(Manifest.permission.READ_CONTACTS)) {
                    startActivity(new Intent(this, ContactFirendActivity.class));
                } else {
                    requestPermission(new String[]{Manifest.permission.READ_CONTACTS});
                }
                break;
            case R.id.iv_search:
                queryPhoneUser();
                break;
        }
    }

}
