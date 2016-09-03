package com.example.lenovo.murphysl.bean;

import com.example.lenovo.murphysl.db.NewFriend;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;


/**
 * UserBean
 *
 * 用户数据
 *
 * @author: lenovo
 * @time: 2016/7/13 21:02
 */
public class UserBean extends BmobUser {

    private String avatar;
    private BmobFile pic;
    private String hobby;
    private String sort;

    public UserBean(){}

    public UserBean(NewFriend friend){
        setObjectId(friend.getUid());
        setUsername(friend.getName());
        setAvatar(friend.getAvatar());
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }
}
