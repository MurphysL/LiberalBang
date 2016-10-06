package com.example.lenovo.murphysl.bean;

import com.baidu.mapapi.model.LatLng;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * MoveLine
 *
 * @author: lenovo
 * @time: 2016/9/8 17:04
 */

public class MoveLine extends BmobObject {

    UserBean user;
    List<BmobGeoPoint> list;
    UserBean friend;

    public UserBean getFriend() {
        return friend;
    }

    public void setFriend(UserBean friend) {
        this.friend = friend;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public List<BmobGeoPoint> getList() {
        return list;
    }

    public void setList(List<BmobGeoPoint> list) {
        this.list = list;
    }
}
