package com.wnagj.meets.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.wnagj.framework.base.BaseUIActicity;
import com.wnagj.framework.entity.Constants;
import com.wnagj.framework.helper.FileHelper;
import com.wnagj.framework.helper.GlideHelper;
import com.wnagj.meets.R;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

public class ImagePreviewActivity extends BaseUIActicity {


    @BindView(R.id.photo_view)
    PhotoView photoView;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_download)
    TextView tvDownload;
    private String mUrl;


    public static void startActivity(Context mContext, boolean isUrl, String url) {
        Intent intent = new Intent(mContext, ImagePreviewActivity.class);
        intent.putExtra(Constants.INTENT_IMAGE_TYPE, isUrl);
        intent.putExtra(Constants.INTENT_IMAGE_URL, url);
        mContext.startActivity(intent);
    }

    @Override
    protected void initView() {

        Intent intent = getIntent();
        boolean isUrl = intent.getBooleanExtra(Constants.INTENT_IMAGE_TYPE, false);
        mUrl = intent.getStringExtra(Constants.INTENT_IMAGE_URL);
        //图片地址菜下载，File代表本次已经存在
        tvDownload.setVisibility(isUrl? View.VISIBLE:View.GONE);

        if (isUrl) {
            GlideHelper.loadUrl(this, mUrl, photoView);
        }else{
            GlideHelper.loadFile(this, new File(mUrl), photoView);
        }


    }

    @OnClick({R.id.iv_back,R.id.tv_download})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_download:
                Toast.makeText(this, getString(R.string.text_iv_pre_downloading), Toast.LENGTH_SHORT).show();
                GlideHelper.loadUrlToBitmap(this, mUrl, new GlideHelper.OnGlideBitmapResultListener() {
                    @Override
                    public void onResourceReady(Bitmap resource) {
                        if(resource != null){
                            FileHelper.getInstance().saveBitmapToAlbum(ImagePreviewActivity.this,resource);
                        }else{
                            Toast.makeText(ImagePreviewActivity.this, getString(R.string.text_iv_pre_save_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected int getResLayout() {
        return R.layout.activity_image_preview;
    }

}
