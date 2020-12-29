package com.wnagj.meets.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.wnagj.framework.base.BaseUIActicity;
import com.wnagj.framework.helper.FileHelper;
import com.wnagj.meets.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QrCodeActivity extends BaseUIActicity {

    private static final int REQUEST_IMAGE = 1234;


    @BindView(R.id.fl_my_container)
    FrameLayout flMyContainer;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_to_ablum)
    TextView ivToAblum;
    @BindView(R.id.iv_flashlight)
    ImageView ivFlashlight;

    /**
     * callback 回调
     */
    private CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);
            finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    };

    //是否打开闪光灯
    private boolean isOpenLight = false;

    @Override
    protected void initView() {
        initQrCode();


    }

    /**
     * 初始化二维码
     */
    private void initQrCode() {
        CaptureFragment captureFragment = new CaptureFragment();
        CodeUtils.setFragmentArgs(captureFragment, R.layout.layout_qrcode);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_my_container, captureFragment).commit();
    }

    @Override
    protected int getResLayout() {
        return R.layout.activity_qrcode;
    }


    @OnClick({R.id.iv_back, R.id.iv_to_ablum, R.id.iv_flashlight})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                //关闭
                finish();
                break;
            case R.id.iv_to_ablum:
                openAblum();
                break;
            case R.id.iv_flashlight:
                try {
                    isOpenLight = !isOpenLight;
                    CodeUtils.isLightEnable(isOpenLight);
                    ivFlashlight.setImageResource(isOpenLight ? R.drawable.img_flashlight_p : R.drawable.img_flashlight);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 打开相册
     */
    private void openAblum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                String path = FileHelper.getInstance()
                        .getRealPathFromURI(QrCodeActivity.this, uri);
                try {
                    CodeUtils.analyzeBitmap(path, new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            analyzeCallback.onAnalyzeSuccess(mBitmap, result);
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            analyzeCallback.onAnalyzeFailed();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
