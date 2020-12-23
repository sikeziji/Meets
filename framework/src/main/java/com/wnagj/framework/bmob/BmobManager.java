package com.wnagj.framework.bmob;

import android.content.Context;

import com.wnagj.framework.util.LogUtils;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class BmobManager {


    private static final String BMOB_SDK_ID = "eceead525670b7d1623e8def15abc900";
    private static final String BMOB_NEW_DOMAIN = "http://sdk.cilc.cloud/8/";

    private volatile static BmobManager mInstance = null;

    private BmobManager() {

    }

    public static BmobManager getInstance() {
        if (mInstance == null) {
            synchronized (BmobManager.class) {
                if (mInstance == null) {
                    mInstance = new BmobManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化Bmob
     *
     * @param mContext
     */
    public void initBmob(Context mContext) {
        //如果Bmob绑定独立域名，则需要在初始化之前重置
        Bmob.resetDomain(BMOB_NEW_DOMAIN);
        Bmob.initialize(mContext, BMOB_SDK_ID);
    }

    /**
     * 是否登录
     *
     * @return
     */
    public boolean isLogin() {
        return BmobUser.isLogin();
    }

    /**
     * 获取本地对象
     *
     * @return
     */
    public IMUser getUser() {
        IMUser currentUser = BmobUser.getCurrentUser(IMUser.class);
        LogUtils.i("currentUser = " + currentUser.toString());
        return currentUser;
    }

    /**
     * 同步控制台信息至本地缓存
     */
    public void fetchUserInfo(FetchUserInfoListener<BmobUser> listener) {
        BmobUser.fetchUserInfo(listener);
    }

    /**
     * 发送短信验证码
     *
     * @param phone    手机号码
     * @param listener 回调
     */
    public void requestSMS(String phone, QueryListener<Integer> listener) {
        BmobSMS.requestSMSCode(phone, "", listener);
    }

    /**
     * 通过手机号码注册或者登陆
     *
     * @param phone    手机号码
     * @param code     短信验证码
     * @param listener 回调
     */
    public void signOrLoginByMobilePhone(String phone, String code, LogInListener<IMUser> listener) {
        BmobUser.signOrLoginByMobilePhone(phone, code, listener);
    }

    /**
     * 账号密码登录
     *
     * @param userName
     * @param pw
     * @param listener
     */
    public void loginByAccount(String userName, String pw, SaveListener<IMUser> listener) {
        IMUser imUser = new IMUser();
        imUser.setUsername(userName);
        imUser.setPassword(pw);
        imUser.login(listener);
    }
    /**
     * 上传头像
     *
     * @param nickName
     * @param file
     * @param listener
     */
    public void uploadFirstPhoto(final String nickName, File file, final OnUploadPhotoListener listener) {
        /**
         * 1.上传文件拿到地址
         * 2.更新用户信息
         */
        final IMUser imUser = getUser();
        //独立域名 设置 - 应用配置 - 独立域名 一年/100
        //免费的办法： 使用我的Key
        //你如果怕数据冲突
        //解决办法：和我的类名不一样即可
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //上传成功
                    imUser.setNickName(nickName);
                    imUser.setPhoto(bmobFile.getFileUrl());

                    imUser.setTokenNickName(nickName);
                    imUser.setTokenPhoto(bmobFile.getFileUrl());

                    //更新用户信息
                    imUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                listener.OnUpdateDone();
                            } else {
                                listener.OnUpdateFail(e);
                            }
                        }
                    });
                } else {
                    listener.OnUpdateFail(e);
                }
            }
        });
    }




    public interface OnUploadPhotoListener {

        void OnUpdateDone();

        void OnUpdateFail(BmobException e);
    }


    /**
     * 根据电话号码查询
     * @param phone
     * @param listener
     */
    public void queryPhoneUser(String phone, FindListener<IMUser> listener){
        baseQuery("mobilePhoneNumber",phone,listener);
    }

    /**
     * 查询base的方法
     * @param key
     * @param values
     * @param listener
     */
    public void baseQuery(String key,String values,FindListener<IMUser> listener){
        BmobQuery<IMUser>  query = new BmobQuery<>();
        BmobQuery<IMUser> query1 = query.addWhereEqualTo(key, values);
        LogUtils.i("query1 = " + query1.toString() );
        query.findObjects(listener);
    }

    /**
     * 查询所有用户
     * @param listener
     */
    public void queryAllUser(FindListener<IMUser> listener) {
        BmobQuery<IMUser>  query = new BmobQuery<>();
        query.findObjects(listener);
    }

    /**
     * 查询私有库
     *
     * @param listener
     */
    public void queryPrivateSet(FindListener<PrivateSet> listener) {
        BmobQuery<PrivateSet> query = new BmobQuery<>();
        query.findObjects(listener);
    }

}
