package com.wnagj.meets.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wnagj.framework.adapter.CommonAdapter;
import com.wnagj.framework.adapter.CommonViewHolder;
import com.wnagj.framework.base.BaseBackActivity;
import com.wnagj.framework.entity.Constants;
import com.wnagj.framework.util.SpUtils;
import com.wnagj.meets.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChatThemeActivity extends BaseBackActivity {


    @BindView(R.id.mThemeView)
    RecyclerView mThemeView;
    private Unbinder mBind;
    private List<Integer> mThemeList = new ArrayList<>();
    private CommonAdapter<Integer> mThemeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_theme);
        mBind = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mThemeList.add(R.drawable.img_chat_bg_1);
        mThemeList.add(R.drawable.img_chat_bg_2);
        mThemeList.add(R.drawable.img_chat_bg_3);
        mThemeList.add(R.drawable.img_chat_bg_4);
        mThemeList.add(R.drawable.img_chat_bg_5);
        mThemeList.add(R.drawable.img_chat_bg_6);
        mThemeList.add(R.drawable.img_chat_bg_7);
        mThemeList.add(R.drawable.img_chat_bg_8);
        mThemeList.add(R.drawable.img_chat_bg_9);

        mThemeView.setLayoutManager(new GridLayoutManager(this, 3));
        mThemeAdapter = new CommonAdapter<>(mThemeList, new CommonAdapter.OnBindDataListener<Integer>() {
            @Override
            public void onBindViewHolder(Integer model, CommonViewHolder viewHolder, int type, int position) {
                viewHolder.setImageResource(R.id.iv_theme, model);

                viewHolder.itemView.setOnClickListener(v -> {
                    SpUtils.getInstance().putInt(Constants.SP_CHAT_THEME, (position + 1));
                    Toast.makeText(ChatThemeActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_theme_item;
            }
        });
        mThemeView.setAdapter(mThemeAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBind != null) {
            mBind.unbind();
        }
    }
}
