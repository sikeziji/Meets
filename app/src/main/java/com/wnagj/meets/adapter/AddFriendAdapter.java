package com.wnagj.meets.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wnagj.framework.helper.GlideHelper;
import com.wnagj.meets.R;
import com.wnagj.meets.model.AddFriendModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //标题
    private static final int TYPE_TITLE = 0;
    //内容
    private static final int TYPE_CONTENT = 1;

    private Context mContext;
    private List<AddFriendModel> mList;
    private LayoutInflater inLayoutInflater;

    private OnClickListener mOnClickListener;


    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public AddFriendAdapter(Context context, List<AddFriendModel> list) {
        mContext = context;
        mList = list;
        inLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_TITLE) {
            return new TitleViewHolder(inLayoutInflater.inflate(R.layout.layout_search_title_item, null));
        } else if (viewType == TYPE_CONTENT) {
            return new ContentViewHolder(inLayoutInflater.inflate(R.layout.layout_search_user_item, null));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        AddFriendModel model = mList.get(position);
        if (model.getType() == TYPE_TITLE) {
            ((TitleViewHolder) holder).tv_title.setText(model.getTitle());
        } else if (model.getType() == TYPE_CONTENT) {
            //设置头像
            GlideHelper.loadUrl(mContext, model.getPhoto(), ((ContentViewHolder) holder).iv_photo);
            //设置性别
            ((ContentViewHolder) holder).iv_sex.setImageResource(model.isSex() ? R.drawable.img_boy_icon :
                    R.drawable.img_girl_icon);
            //设置年龄
            ((ContentViewHolder) holder).tv_ages.setText(model.getAge()+"");
            //设置描述
            ((ContentViewHolder) holder).tv_desc.setText(model.getDesc());
            //设置昵称
            ((ContentViewHolder) holder).tv_nickname.setText(model.getNickName());
        }
        //点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null) {
                    mOnClickListener.Onclick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title;

        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView iv_photo;
        private ImageView iv_sex;
        private TextView tv_nickname;
        private TextView tv_ages;
        private TextView tv_desc;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_photo = itemView.findViewById(R.id.iv_photo);
            iv_sex = itemView.findViewById(R.id.iv_sex);
            tv_nickname = itemView.findViewById(R.id.tv_nickname);
            tv_ages = itemView.findViewById(R.id.tv_ages);
            tv_desc = itemView.findViewById(R.id.tv_desc);
        }
    }

    public  interface  OnClickListener{
        void  Onclick(int position);

    }

}
