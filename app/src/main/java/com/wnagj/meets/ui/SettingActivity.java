package com.wnagj.meets.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.wnagj.framework.base.BaseBackActivity;
import com.wnagj.framework.cloud.CloudManager;
import com.wnagj.framework.entity.Constants;
import com.wnagj.framework.helper.ActivityHelper;
import com.wnagj.framework.helper.UpdateHelper;
import com.wnagj.framework.manager.DialogManager;
import com.wnagj.framework.util.LanguaueUtils;
import com.wnagj.framework.util.SpUtils;
import com.wnagj.framework.view.DialogView;
import com.wnagj.meets.R;
import com.wnagj.meets.service.CloudService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;

public class SettingActivity extends BaseBackActivity {

    @BindView(R.id.sw_app_tips)
    Switch swAppTips;
    @BindView(R.id.rl_app_tips)
    RelativeLayout rlAppTips;
    @BindView(R.id.rl_chat_theme)
    RelativeLayout rlChatTheme;
    @BindView(R.id.tv_cache_size)
    TextView tvCacheSize;
    @BindView(R.id.rl_clear_cache)
    RelativeLayout rlClearCache;
    @BindView(R.id.tv_current_languaue)
    TextView tvCurrentLanguaue;
    @BindView(R.id.rl_update_languaue)
    RelativeLayout rlUpdateLanguaue;
    @BindView(R.id.rl_check_permissions)
    RelativeLayout rlCheckPermissions;
    @BindView(R.id.rl_app_show)
    RelativeLayout rlAppShow;
    @BindView(R.id.tv_new_version)
    TextView tvNewVersion;
    @BindView(R.id.tv_app_version)
    TextView tvAppVersion;
    @BindView(R.id.rl_check_version)
    RelativeLayout rlCheckVersion;
    @BindView(R.id.btn_logout)
    Button btnLogout;


    private Unbinder mBind;
    private boolean isTips;
    private UpdateHelper mUpdateHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mBind = ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        try {
            tvAppVersion.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        swAppTips.setChecked(isTips);

        tvCacheSize.setText("0.0 MB");

        int languaue = SpUtils.getInstance().getInt(Constants.SP_LANGUAUE, 0);
        tvCurrentLanguaue.setText(languaue == 1 ? getString(R.string.text_setting_en) : getString(R.string.text_setting_zh));

        initLanguaueDialog();

        mUpdateHelper = new UpdateHelper(this);
        updateApp();

    }

    /**
     * 更新app
     */
    private void updateApp() {

        mUpdateHelper.updateApp(new UpdateHelper.OnUpdateAppListener() {
            @Override
            public void OnUpdate(boolean isUpdate) {
                tvNewVersion.setVisibility(isUpdate ? View.VISIBLE : View.GONE);
            }
        });
    }

    private DialogView mLanguaueDialog;
    private TextView tv_zh;
    private TextView tv_en;
    private TextView tv_cancel;


    private void initLanguaueDialog() {
        mLanguaueDialog = DialogManager.getInstance().initView(this, R.layout.dialog_select_photo, Gravity.BOTTOM);
        tv_zh = (TextView) mLanguaueDialog.findViewById(R.id.tv_camera);
        tv_en = (TextView) mLanguaueDialog.findViewById(R.id.tv_ablum);
        tv_cancel = (TextView) mLanguaueDialog.findViewById(R.id.tv_cancel);

        tv_zh.setText(getString(R.string.text_setting_zh));
        tv_en.setText(getString(R.string.text_setting_en));

        tv_zh.setOnClickListener(view -> {
            selectLanguaue(0);
            DialogManager.getInstance().hide(mLanguaueDialog);
        });

        tv_en.setOnClickListener(view -> {
            selectLanguaue(1);
            DialogManager.getInstance().hide(mLanguaueDialog);
        });

        tv_cancel.setOnClickListener(view -> DialogManager.getInstance().hide(mLanguaueDialog));

    }

    /**
     * @param index
     */
    private void selectLanguaue(int index) {
        if (LanguaueUtils.SYS_LANGUAGE == index) {
            return;
        }
        SpUtils.getInstance().putInt(Constants.SP_LANGUAUE, index);
        //EventManager.post(EventManager.EVENT_RUPDATE_LANGUAUE);
        Toast.makeText(this, "Test Model , Reboot App ", Toast.LENGTH_SHORT).show();
        //暂时先重启处理
        finishAffinity();
        System.exit(0);
    }

    /**
     * 退出登录
     */
    private void logout() {

        //删除Token
        SpUtils.getInstance().deleteKey(Constants.SP_TOKEN);
        //Bmob退出登录
        BmobUser.logOut();
        //融云
        CloudManager.getInstance().disconnect();
        CloudManager.getInstance().logout();

        ActivityHelper.getInstance().exit();

        stopService(new Intent(this, CloudService.class));

        //跳转到登录页
        Intent intent_login = new Intent();
        intent_login.setClass(SettingActivity.this, LoginActivity.class);
        intent_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent_login);
        finish();
    }

    private void openWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                            , Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "已授予窗口权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "已授予窗口权限", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBind != null) {
            mBind.unbind();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.rl_app_tips,R.id.rl_chat_theme,R.id.rl_clear_cache,R.id.rl_update_languaue,
            R.id.rl_check_permissions,R.id.rl_check_version,R.id.btn_logout,R.id.rl_app_show})
     void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_app_tips:
                isTips = !isTips;
                swAppTips.setChecked(isTips);
                SpUtils.getInstance().putBoolean("isTips", isTips);
                break;
            case R.id.rl_chat_theme:
                startActivity(new Intent(this,ChatThemeActivity.class));
                break;
            case R.id.rl_clear_cache:

                break;
            case R.id.rl_update_languaue:
                DialogManager.getInstance().show(mLanguaueDialog);
                break;
            case R.id.rl_check_permissions:
                openWindow();
                break;
            case R.id.rl_check_version:
                updateApp();
                break;
            case R.id.btn_logout:
                /**
                 * 退出登录的逻辑
                 * 1.通过一个管理类管理好所有的Activity
                 * 2.清空Token
                 * 3.清空Bmob
                 * 4.断开服务连接
                 * 5.跳转至登录页
                 * 7.停止云服务
                 */
                logout();
                break;
            case R.id.rl_app_show:
                Uri uri = Uri.parse("https://www.pgyer.com/imoocmeet");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
    }
}
