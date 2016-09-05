package com.example.lenovo.murphysl.bean;

import cn.bmob.v3.BmobObject;


public class Comment extends BmobObject{
	
	public static final String TAG = "Comment";

	private UserBean user;
	private String commentContent;
	public UserBean getUser() {
		return user;
	}
	public void setUser(UserBean user) {
		this.user = user;
	}
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

}
