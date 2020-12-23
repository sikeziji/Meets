package com.wnagj.meets.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxun.tagcloudlib.view.TagsAdapter;
import com.wnagj.framework.helper.GlideHelper;
import com.wnagj.meets.R;
import com.wnagj.meets.model.StarModel;

import java.security.PublicKey;
import java.util.List;

/**
 * 3D星球实现
 */
public class CloudTagAdapter extends TagsAdapter {
    private Context mContext;
    private List<StarModel> mList;
    private LayoutInflater mLayoutInflater;

    public CloudTagAdapter(Context context, List<StarModel> list) {
        mContext = context;
        mList = list;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        StarModel model = mList.get(position);
        View mView = null;
        ViewHolder mViewHolder;
        if (mView == null) {
            mViewHolder = new ViewHolder();
            //初始化View
            mView = mLayoutInflater.inflate(R.layout.layout_star_view_item, null);
            //初始化控件
            mViewHolder.iv_star_icon = mView.findViewById(R.id.iv_star_icon);
            mViewHolder.tv_star_name = mView.findViewById(R.id.tv_star_name);
            mView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) mView.getTag();
        }
        //判断数据中的手机号是否为空
        if (!TextUtils.isEmpty(model.getPhotoUrl())) {
            //加载图片
            GlideHelper.loadSmollUrl(mContext, model.getPhotoUrl(), 30, 30, mViewHolder.iv_star_icon);
        } else {
            //设置为默认
            mViewHolder.iv_star_icon.setImageResource(R.drawable.img_star_icon_3);
        }
        //获取昵称
        mViewHolder.tv_star_name.setText(model.getNickName());
        return mView;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getPopularity(int position) {
        return 7;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor) {

    }

    class ViewHolder {
        private ImageView iv_star_icon;
        private TextView tv_star_name;
    }
}
