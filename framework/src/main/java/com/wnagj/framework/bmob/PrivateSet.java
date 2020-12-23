package com.wnagj.framework.bmob;

import cn.bmob.v3.BmobObject;

public class PrivateSet extends BmobObject {

    //用户ID
    private String userId;
    //用户电话号码
    private String phone;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
