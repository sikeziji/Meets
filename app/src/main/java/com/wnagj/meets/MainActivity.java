package com.wnagj.meets;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.wnagj.framework.base.BaseUIActicity;
import com.wnagj.framework.bmob.BmobManager;
import com.wnagj.framework.entity.Constants;
import com.wnagj.framework.event.EventManager;
import com.wnagj.framework.event.MessageEvent;
import com.wnagj.framework.gson.TokenBean;
import com.wnagj.framework.java.SimulationData;
import com.wnagj.framework.manager.DialogManager;
import com.wnagj.framework.util.LogUtils;
import com.wnagj.framework.util.SpUtils;
import com.wnagj.framework.view.DialogView;
import com.wnagj.meets.fragment.ChatFragment;
import com.wnagj.meets.fragment.MeFragment;
import com.wnagj.meets.fragment.SquareFragment;
import com.wnagj.meets.fragment.StarFragment;
import com.wnagj.meets.service.CloudService;
import com.wnagj.meets.ui.FirstUploadActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

/**
 * 主界面
 */
public class MainActivity extends BaseUIActicity {


    /**
     * 1. 初始化Fragment
     * 2. 显示Fragment
     * 3. 隐藏所有的Fragment
     * 4. 恢复Fragment
     * 5. 对一些问题进行优化
     */

    private Disposable disposable;

    @BindView(R.id.mMainLayout)
    FrameLayout mMainLayout;

    //星球
    @BindView(R.id.iv_star)
    ImageView iv_star;
    @BindView(R.id.tv_star)
    TextView tv_star;
    @BindView(R.id.ll_star)
    LinearLayout ll_star;
    private StarFragment mStarFragment = null;
    private FragmentTransaction mStarTransaction = null;

    //广场
    @BindView(R.id.iv_square)
    ImageView iv_square;
    @BindView(R.id.tv_square)
    TextView tv_square;
    @BindView(R.id.ll_square)
    LinearLayout ll_square;
    private SquareFragment mSquareFragment = null;
    private FragmentTransaction mSquareTransaction = null;

    //聊天
    @BindView(R.id.iv_chat)
    ImageView iv_chat;
    @BindView(R.id.tv_chat)
    TextView tv_chat;
    @BindView(R.id.ll_chat)
    LinearLayout ll_chat;
    private ChatFragment mChatFragment = null;
    private FragmentTransaction mChatTransaction = null;

    //我的
    @BindView(R.id.iv_me)
    ImageView iv_me;
    @BindView(R.id.tv_me)
    TextView tv_me;
    @BindView(R.id.ll_me)
    LinearLayout ll_me;
    private MeFragment mMeFragment = null;
    private FragmentTransaction mMeTransaction = null;



    private DialogView mUploadView;

    @Override
    protected void initView() {
        requestPermiss();

        //设置文本
        tv_star.setText("星球");
        tv_square.setText("广场");
        tv_chat.setText("聊天");
        tv_me.setText("我的");

        initFragment();

        //默认从主页开始
        checkMainTab(MainTab.Start);

        checkToken();

        //模拟数据
//        SimulationData.testData();

    }

    private void checkToken() {
        LogUtils.i("checkToken");
        if (mUploadView != null) {
            DialogManager.getInstance().hide(mUploadView);
        }
        //获取Token 需要三个参数 1. 用户ID 2. 头像地址 3.昵称
        String token = SpUtils.getInstance().getString(Constants.SP_TOKEN, "");
        if (!TextUtils.isEmpty(token)) {
            //启动云服务连接融云服务
            startService(new Intent(this, CloudService.class));
        } else {
            //1. 有三个参数
            String tokenPhoto = BmobManager.getInstance().getUser().getTokenPhoto();
            String tokenNickName = BmobManager.getInstance().getUser().getTokenNickName();
            if (!TextUtils.isEmpty(tokenPhoto) && !TextUtils.isEmpty(tokenNickName)) {
                //创建token
                createToken();
            } else {
                //创建上传提示框
                createUploadDialog();
            }
        }

    }

    /**
     * 解析Token
     *
     * @param s
     */
    private void parsingCloudToken(String s) {
//        try {
//            LogUtils.i("parsingCloudToken:" + s);
//            TokenBean tokenBean = new Gson().fromJson(s, TokenBean.class);
//            if (tokenBean.getCode() == 200) {
//                if (!TextUtils.isEmpty(tokenBean.getToken())) {
//                    //保存Token
//                    SpUtils.getInstance().putString(Constants.SP_TOKEN, tokenBean.getToken());
//                    startCloudService();
//                }
//            } else if (tokenBean.getCode() == 2007) {
//                Toast.makeText(this, "注册人数已达上限，请替换成自己的Key", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            LogUtils.i("parsingCloudToken:" + e.toString());
//        }
    }

    /**
     * 创建Token
     */
    private void createToken() {
        /**
         * 1.去融云后台获取Token
         * 2.连接融云
         */
//        final HashMap<String, String> map = new HashMap<>();
//        map.put("userId", BmobManager.getInstance().getUser().getObjectId());
//        map.put("name", BmobManager.getInstance().getUser().getTokenNickName());
//        map.put("portraitUri", BmobManager.getInstance().getUser().getTokenPhoto());
//
//        //通过OkHttp请求Token
//        disposable = Observable.create(new ObservableOnSubscribe<String>() {
//            @Override
//            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
//                //执行请求过程
//                String json = HttpManager.getInstance().postCloudToken(map);
//                LogUtils.i("json:" + json);
//                emitter.onNext(json);
//                emitter.onComplete();
//            }
//            //线程调度
//        }).subscribeOn(Schedulers.newThread())
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//                        parsingCloudToken(s);
//                    }
//                });

    }

    /**
     * 创建上传提示框
     */
    private void createUploadDialog() {
        mUploadView = DialogManager.getInstance().initView(this, R.layout.dialog_first_upload);
        mUploadView.setCancelable(false);
        View iv_go_upload = mUploadView.findViewById(R.id.iv_go_upload);
        iv_go_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirstUploadActivity.startActivity(MainActivity.this);
            }
        });
        DialogManager.getInstance().show(mUploadView);
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {

        //星球
        if (mStarFragment == null) {
            mStarFragment = new StarFragment();
            mStarTransaction = getSupportFragmentManager().beginTransaction();
            mStarTransaction.add(R.id.mMainLayout, mStarFragment);
            mStarTransaction.commit();
        }

        //广场
        if (mSquareFragment == null) {
            mSquareFragment = new SquareFragment();
            mSquareTransaction = getSupportFragmentManager().beginTransaction();
            mSquareTransaction.add(R.id.mMainLayout, mSquareFragment);
            mSquareTransaction.commit();
        }

        //聊天
        if (mChatFragment == null) {
            mChatFragment = new ChatFragment();
            mChatTransaction = getSupportFragmentManager().beginTransaction();
            mChatTransaction.add(R.id.mMainLayout, mChatFragment);
            mChatTransaction.commit();
        }

        //我的
        if (mMeFragment == null) {
            mMeFragment = new MeFragment();
            mMeTransaction = getSupportFragmentManager().beginTransaction();
            mMeTransaction.add(R.id.mMainLayout, mMeFragment);
            mMeTransaction.commit();
        }

    }

    /**
     * 显示fragment
     *
     * @param fragment
     */
    private void showFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            hideALl(transaction);
            transaction.show(fragment);
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 隐藏所有的Fragment
     *
     * @param transaction
     */
    private void hideALl(FragmentTransaction transaction) {
        if (mStarFragment != null) {
            transaction.hide(mStarFragment);
        }
        if (mSquareFragment != null) {
            transaction.hide(mSquareFragment);
        }
        if (mChatFragment != null) {
            transaction.hide(mChatFragment);
        }
        if (mMeFragment != null) {
            transaction.hide(mMeFragment);
        }
    }

    /**
     * 切换主页选项卡
     */
    private void checkMainTab(MainTab tab) {
        switch (tab) {
            case Start:
                showFragment(mStarFragment);

                iv_star.setImageResource(R.drawable.img_star_p);
                iv_square.setImageResource(R.drawable.img_square);
                iv_chat.setImageResource(R.drawable.img_chat);
                iv_me.setImageResource(R.drawable.img_me);

                //字体文字颜色
                tv_star.setTextColor(getResources().getColor(R.color.colorAccent));
                tv_square.setTextColor(Color.BLACK);
                tv_chat.setTextColor(Color.BLACK);
                tv_me.setTextColor(Color.BLACK);

                break;
            case Squeare:
                showFragment(mSquareFragment);


                iv_star.setImageResource(R.drawable.img_star);
                iv_square.setImageResource(R.drawable.img_square_p);
                iv_chat.setImageResource(R.drawable.img_chat);
                iv_me.setImageResource(R.drawable.img_me);


                //字体文字颜色
                tv_star.setTextColor(Color.BLACK);
                tv_square.setTextColor(getResources().getColor(R.color.colorAccent));
                tv_chat.setTextColor(Color.BLACK);
                tv_me.setTextColor(Color.BLACK);

                break;
            case Chat:
                showFragment(mChatFragment);


                iv_star.setImageResource(R.drawable.img_star);
                iv_square.setImageResource(R.drawable.img_square);
                iv_chat.setImageResource(R.drawable.img_chat_p);
                iv_me.setImageResource(R.drawable.img_me);


                //字体文字颜色
                tv_chat.setTextColor(getResources().getColor(R.color.colorAccent));
                tv_star.setTextColor(Color.BLACK);
                tv_square.setTextColor(Color.BLACK);
                tv_me.setTextColor(Color.BLACK);

                break;
            case Me:
                showFragment(mMeFragment);


                iv_star.setImageResource(R.drawable.img_star);
                iv_square.setImageResource(R.drawable.img_square);
                iv_chat.setImageResource(R.drawable.img_chat);
                iv_me.setImageResource(R.drawable.img_me_p);


                //字体文字颜色
                tv_me.setTextColor(getResources().getColor(R.color.colorAccent));
                tv_star.setTextColor(Color.BLACK);
                tv_square.setTextColor(Color.BLACK);
                tv_chat.setTextColor(Color.BLACK);

                break;
        }


    }

    /**
     * 防止重叠
     * 当应用内存紧张的时候，系统会回收掉Fragment对象
     * 再一次进入的时候会重新创建Fragment
     * 非原来对象，我们无法控制，导致重叠
     *
     * @param fragment
     */
    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if (mStarFragment != null && fragment instanceof StarFragment) {
            mStarFragment = (StarFragment) fragment;
        }
        if (mSquareFragment != null && fragment instanceof SquareFragment) {
            mSquareFragment = (SquareFragment) fragment;
        }
        if (mChatFragment != null && fragment instanceof ChatFragment) {
            mChatFragment = (ChatFragment) fragment;
        }
        if (mMeFragment != null && fragment instanceof MeFragment) {
            mMeFragment = (MeFragment) fragment;
        }

    }

    enum MainTab {
        Start,
        Squeare,
        Chat,
        Me
    }

    /**
     * 请求权限
     */
    private void requestPermiss() {
        request(new OnPermissionsResult() {
            @Override
            public void OnSuccess() {

            }

            @Override
            public void OnFail(List<String> noPermissions) {
                LogUtils.i("noPermissions:" + noPermissions.toString());
            }
        });

    }

    @Override
    protected int getResLayout() {
        return R.layout.activity_main;
    }


    @OnClick({R.id.ll_star, R.id.ll_square, R.id.ll_chat, R.id.ll_me})
    void onclick(View view) {
        switch (view.getId()) {
            case R.id.ll_star:
                checkMainTab(MainTab.Start);
                break;
            case R.id.ll_square:
                checkMainTab(MainTab.Squeare);
                break;
            case R.id.ll_chat:
                checkMainTab(MainTab.Chat);
                break;
            case R.id.ll_me:
                checkMainTab(MainTab.Me);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getType()) {
            case EventManager.EVENT_REFRE_TOKEN_STATUS:
                checkToken();
                break;
        }
    }
}