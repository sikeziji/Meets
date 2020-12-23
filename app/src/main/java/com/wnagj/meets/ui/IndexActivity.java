package com.wnagj.meets.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wnagj.framework.bmob.BmobManager;
import com.wnagj.framework.entity.Constants;
import com.wnagj.framework.util.SpUtils;
import com.wnagj.meets.MainActivity;
import com.wnagj.meets.R;


/**
 * 启动页
 */
public class IndexActivity extends AppCompatActivity {


    /**
     * 1. 把启动页全屏
     * 2. 延迟进入主页
     * 3. 根据具体逻辑是进入主页还是引导页或者登陆页
     * 4. 适配刘海屏
     *
     * @param savedInstanceState
     */

    private static final int SKIP_MAIN = 1000;


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case SKIP_MAIN:
                    startMain();
                    break;
                default:
                    break;
            }
            return  false;
        }
    });

    /**
     * 进入主界面
     */
    private void startMain() {
        //判断App是否第一次启动成功， install - first run
        boolean isFirstApp = SpUtils.getInstance().getBoolean(Constants.SP_IS_FIRST_APP, true);
        Intent intent = new Intent();
        if (isFirstApp) {
            //跳转到引导页
            intent.setClass(this, GuideActivity.class);
            //设置为非第一次启动
            SpUtils.getInstance().putBoolean(Constants.SP_IS_FIRST_APP, false);
        } else {
            // 如果是非第一次启动，判断是否登陆过
            String token = SpUtils.getInstance().getString(Constants.SP_TOKEN, "");
            if (TextUtils.isEmpty(token)) {
//                判断bomo是否登陆
                if (BmobManager.getInstance().isLogin()) {
                    //跳转到主页
                    intent.setClass(this, MainActivity.class);
                } else {
                    //跳转到登录页
                    intent.setClass(this, LoginActivity.class);
                }
            } else {
                //跳转到主页
                intent.setClass(this, MainActivity.class);
            }
        }
        startActivity(intent);
        finish();

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_index);

        mHandler.sendEmptyMessageDelayed(SKIP_MAIN, 2 * 1000);
    }
}
