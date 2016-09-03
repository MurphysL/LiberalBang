package com.example.lenovo.murphysl.bean;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import cn.bmob.newim.bean.BmobIMExtraMessage;
import cn.bmob.newim.bean.BmobIMMessage;

/**
 * 同意添加好友请求-仅仅只用于发送同意添加好友的消息
 * 接收到对方发送的同意添加自己为好友的请求时，需要做两个事情：
 * 1、在本地数据库中新建一个会话，因此需要设置isTransient为false,
 * 2、添加对方到自己的好友表中
 */
public class AgreeAddFriendMessage extends BmobIMExtraMessage{

    //以下均是从extra里面抽离出来的字段，方便获取
    private String uid;//最初的发送方
    private Long time;
    private String msg;//用于通知栏显示的内容

    @Override
    public String getMsgType() {
        return "agree";
    }

    @Override
    public boolean isTransient() {
        //设置为true,表明为暂态消息，那么这条消息并不会保存到本地db中，SDK只负责发送出去
        //设置为false,则会保存到指定会话的数据库中
        return true;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public AgreeAddFriendMessage(){}

    /**
     * 继承BmobIMMessage的属性
     * @param msg
     */
    private AgreeAddFriendMessage(BmobIMMessage msg){
        super.parse(msg);
    }

    /**将BmobIMMessage转成AgreeAddFriendMessage
     * @param msg 消息
     * @return
     */
    public static AgreeAddFriendMessage convert(BmobIMMessage msg){
        AgreeAddFriendMessage agree =new AgreeAddFriendMessage(msg);
        try {
            String extra = msg.getExtra();
            if(!TextUtils.isEmpty(extra)){
                JSONObject json =new JSONObject(extra);
                Long time = json.getLong("time");
                String uid =json.getString("uid");
                String m =json.getString("msg");
                agree.setMsg(m);
                agree.setUid(uid);
                agree.setTime(time);
            }else{
                Logger.i("AgreeAddFriendMessage的extra为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return agree;
    }
}
