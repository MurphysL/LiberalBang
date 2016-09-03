package com.example.lenovo.murphysl.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * VoiceBean
 *
 * @author: lenovo
 * @time: 2016/9/1 20:30
 */

public class VoiceBean extends BmobObject {

    private BmobFile file;
    private UserBean user;
    private BmobGeoPoint geo;

    public BmobFile getFile() {
        return file;
    }

    public void setFile(BmobFile file) {
        this.file = file;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public BmobGeoPoint getGeo() {
        return geo;
    }

    public void setGeo(BmobGeoPoint geo) {
        this.geo = geo;
    }
}
