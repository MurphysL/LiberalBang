package com.example.lenovo.murphysl.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * MyDate
 *
 * @author: lenovo
 * @time: 2016/8/26 13:41
 */

public class MyDate extends BmobObject{

    private UserBean user;
    private UserBean friend;
    private BmobFile photo;
    private Integer star;
    private QiangYu qiangYu;

    public MyDate(){

    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public UserBean getFriend() {
        return friend;
    }

    public void setFriend(UserBean friend) {
        this.friend = friend;
    }

    public BmobFile getPhoto() {
        return photo;
    }

    public void setPhoto(BmobFile photo) {
        this.photo = photo;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public QiangYu getQiangYu() {
        return qiangYu;
    }

    public void setQiangYu(QiangYu qiangYu) {
        this.qiangYu = qiangYu;
    }
}
