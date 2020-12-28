package com.wnagj.meets.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wnagj.framework.base.BaseBackActivity;
import com.wnagj.framework.bmob.BmobManager;
import com.wnagj.framework.bmob.IMUser;
import com.wnagj.framework.entity.Constants;
import com.wnagj.framework.event.EventManager;
import com.wnagj.framework.helper.GlideHelper;
import com.wnagj.framework.manager.DialogManager;
import com.wnagj.framework.util.CommonUtils;
import com.wnagj.framework.view.DialogView;
import com.wnagj.meets.R;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatInfoActivity extends BaseBackActivity implements View.OnClickListener {


    private CircleImageView iv_photo;
    private TextView tv_name;
    private TextView tv_phone;
    private Button btn_delete;

    private String objectId;

    private DialogView mDeleteDialog;
    private TextView tvText;
    private TextView tv_confirm;
    private TextView tv_cancel;

    /**
     * 启动界面
     * @param mActivity
     * @param objectId
     * @param requestCode
     */
    public static void startChatInfo(Activity mActivity,String objectId,int requestCode){
        Intent intent = new Intent(mActivity, ChatInfoActivity.class);
        intent.putExtra(Constants.INTENT_USER_ID, objectId);
        mActivity.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);

        initView();
    }

    private void initView() {

        initDeleteDialog();
        iv_photo = (CircleImageView) findViewById(R.id.iv_photo);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        btn_delete = (Button) findViewById(R.id.btn_delete);

        btn_delete.setOnClickListener(this);
        tv_phone.setOnClickListener(this);

        objectId = getIntent().getStringExtra(Constants.INTENT_USER_ID);
        if (!TextUtils.isEmpty(objectId)) {
            BmobManager.getInstance().queryObjectIdUser(objectId, new FindListener<IMUser>() {
                @Override
                public void done(List<IMUser> list, BmobException e) {
                    if (e == null) {
                        if (CommonUtils.isEmpty(list)) {
                            IMUser imUser = list.get(0);
                            GlideHelper.loadUrl(ChatInfoActivity.this, imUser.getPhoto(), iv_photo);
                            tv_phone.setText(imUser.getMobilePhoneNumber());
                            tv_name.setText(imUser.getNickName());
                        }
                    }
                }
            });
        }

    }

    private void initDeleteDialog() {
        mDeleteDialog = DialogManager.getInstance().initView(this, R.layout.dialog_delete_friend);
        tvText = (TextView) mDeleteDialog.findViewById(R.id.tvText);
        tvText.setText(getString(R.string.text_chat_info_del_text));

        tv_confirm = (TextView) mDeleteDialog.findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(this);

        tv_cancel = (TextView) mDeleteDialog.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_phone:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + tv_phone.getText().toString().trim()));
                startActivity(intent);
                break;
            case R.id.btn_delete:
                DialogManager.getInstance().show(mDeleteDialog);
                break;
            case R.id.tv_confirm:
                DialogManager.getInstance().show(mDeleteDialog);
                BmobManager.getInstance().deleteFriend(objectId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            //刷新列表
                            EventManager.post(EventManager.FLAG_UPDATE_FRIEND_LIST);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(ChatInfoActivity.this, getString(R.string.text_chat_info_del_error) + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.tv_cancel:
                DialogManager.getInstance().hide(mDeleteDialog);
                break;
        }
    }

}
