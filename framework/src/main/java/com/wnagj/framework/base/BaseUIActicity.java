package com.wnagj.framework.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wnagj.framework.util.SystemUI;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseUIActicity extends BaseActivity {

    protected Unbinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUI.fixSystemUI(this);
        setContentView(getResLayout());
        initView();
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        mBinder = ButterKnife.bind(this);
    }

    protected abstract void initView();

    protected abstract int getResLayout();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBinder != null) {
            mBinder.unbind();
        }
    }
}
