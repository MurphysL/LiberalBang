package com.example.lenovo.murphysl.bean;

import cn.bmob.v3.BmobObject;

/**
 * Friend
 * 好友表
 */

public class Friend extends BmobObject {

    private UserBean user;
    private UserBean friendUser;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public UserBean getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(UserBean friendUser) {
        this.friendUser = friendUser;
    }

}
