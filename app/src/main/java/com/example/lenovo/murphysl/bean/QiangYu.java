package com.example.lenovo.murphysl.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

public class QiangYu extends BmobObject implements Serializable{

	private static final long serialVersionUID = -6280656428527540320L;
	
	private UserBean author;
	private String content;
	private BmobFile Contentfigureurl;
	private int love;
	private int comment;
	private boolean isPass;
	private boolean myLove;//赞
	private BmobRelation relation;
	private String geo;
	private BmobGeoPoint loc;

	public BmobRelation getRelation() {
		return relation;
	}
	public void setRelation(BmobRelation relation) {
		this.relation = relation;
	}
	public UserBean getAuthor() {
		return author;
	}
	public void setAuthor(UserBean author) {
		this.author = author;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public BmobFile getContentfigureurl() {
		return Contentfigureurl;
	}
	public void setContentfigureurl(BmobFile contentfigureurl) {
		Contentfigureurl = contentfigureurl;
	}
	public int getLove() {
		return love;
	}
	public void setLove(int love) {
		this.love = love;
	}
	public int getComment() {
		return comment;
	}
	public void setComment(int comment) {
		this.comment = comment;
	}
	public boolean isPass() {
		return isPass;
	}
	public void setPass(boolean isPass) {
		this.isPass = isPass;
	}
	public boolean getMyLove() {
		return myLove;
	}
	public void setMyLove(boolean myLove) {
		this.myLove = myLove;
	}
	public void setGeo(String geo) {
		this.geo = geo;
	}
	public String getGeo() {
		return geo;
	}
	public BmobGeoPoint getLoc() {
		return loc;
	}
	public void setLoc(BmobGeoPoint loc) {
		this.loc = loc;
	}

	@Override
	public String toString() {
		return "QiangYu [author=" + author + ", content=" + content
				+ ", Contentfigureurl=" + Contentfigureurl + ", love=" + love
				+ ", comment=" + comment + ", isPass=" + isPass
				+  ", myLove=" + myLove + ", relation=" + relation + "]";
	}
	
}
