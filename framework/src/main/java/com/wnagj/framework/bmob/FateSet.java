package com.wnagj.framework.bmob;

import cn.bmob.v3.BmobObject;

public class FateSet extends BmobObject {

    //用户ID
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}