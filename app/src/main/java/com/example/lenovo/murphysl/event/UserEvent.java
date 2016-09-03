package com.example.lenovo.murphysl.event;

import com.example.lenovo.murphysl.bean.UserBean;

/**
 * UserEvent
 *
 * @author: lenovo
 * @time: 2016/8/26 17:18
 */

public class UserEvent {

    private String userID;
    private String userName;
    private String hobby;
    private String sort;
    private Double distance;

    private UserBean userBean;

    public UserEvent(String userID , String userName , String hobby , String sort , Double distance){
        this.userID = userID;
        this.userName = userName;
        this.hobby = hobby;
        this.sort = sort;
        this.distance = distance;
    }

    public UserEvent(UserBean userBean){
        this.userBean = userBean;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
}
