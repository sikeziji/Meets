package com.wnagj.framework;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.tencent.bugly.crashreport.CrashReport;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.wnagj.framework.bmob.BmobManager;
import com.wnagj.framework.cloud.CloudManager;
import com.wnagj.framework.helper.NotificationHelper;
import com.wnagj.framework.helper.WindowHelper;
import com.wnagj.framework.manager.MapManager;
import com.wnagj.framework.util.LogUtils;
import com.wnagj.framework.util.SpUtils;

import org.litepal.LitePal;

import java.util.function.Consumer;

import io.reactivex.plugins.RxJavaPlugins;

/**
 * framework 入口
 */
public class Framework {

    private volatile static Framework mFramework;

    private String BUGLY_KEY = "3fa947674f";


    private Framework() {
    }


    public static Framework getFramework() {
        if (mFramework == null) {
            synchronized (Framework.class) {
                if (mFramework == null) {
                    mFramework = new Framework();
                }
            }
        }
        return  mFramework;
    }

    /**
     * 初始化框架 Model
     *
     * @param mContext
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initFramework(Context mContext) {
        LogUtils.i("initFramework");
        SpUtils.getInstance().initSp(mContext);
        BmobManager.getInstance().initBmob(mContext);
        CloudManager.getInstance().initCloud(mContext);
        LitePal.initialize(mContext);
        MapManager.getInstance().initMap(mContext);
        WindowHelper.getInstance().initWindow(mContext);
        CrashReport.initCrashReport(mContext, BUGLY_KEY, false);
        ZXingLibrary.initDisplayOpinion(mContext);
        NotificationHelper.getInstance().createChannel(mContext);
//        KeyWordManager.getInstance().initManager(mContext);
//
//        //全局捕获RxJava异常
        RxJavaPlugins.setErrorHandler(new io.reactivex.functions.Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.e("RxJava：" + throwable.toString());
            }
        });
    }



}
