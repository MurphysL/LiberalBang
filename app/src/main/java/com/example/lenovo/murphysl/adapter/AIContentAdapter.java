package com.example.lenovo.murphysl.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.bmob.v3.listener.UpdateListener;

import com.example.lenovo.murphysl.MyApplication;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.UserInfoActivity;
import com.example.lenovo.murphysl.adapter.base.BaseContentAdapter;
import com.example.lenovo.murphysl.bean.QiangYu;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.model.QiangYuModel;
import com.example.lenovo.murphysl.moment.CommentActivity;
import com.example.lenovo.murphysl.util.ActivityUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * AIContentAdapter
 *
 * 朋友圈ListView Adapter
 *
 * @author: lenovo
 * @time: 2016/9/5 21:24
 */

public class AIContentAdapter extends BaseContentAdapter<QiangYu> {

	private Context mContext;

	public AIContentAdapter( Context context, List<QiangYu> list) {
		super(context, list);
		mContext = context;
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.ai_item, null);
			viewHolder.userName = (TextView) convertView
					.findViewById(R.id.user_name);
			viewHolder.userLogo = (ImageView) convertView
					.findViewById(R.id.user_logo);
			viewHolder.contentText = (TextView) convertView
					.findViewById(R.id.content_text);
			viewHolder.contentImage = (ImageView) convertView
					.findViewById(R.id.content_image);
			viewHolder.love = (TextView) convertView
					.findViewById(R.id.item_action_love);
			viewHolder.comment = (TextView) convertView
					.findViewById(R.id.item_action_comment);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final QiangYu entity = dataList.get(position);
		Log.i("user", entity.toString());
		UserBean user = entity.getAuthor();
		if (user == null) {
			Log.i("user", "USER IS NULL");
		}
		if (user.getAvatar() == null) {
			Log.i("user", "USER avatar IS NULL");
		}
		String avatarUrl = null;
		if (user.getAvatar() != null) {
			avatarUrl = user.getAvatar();
		}
		ImageLoader.getInstance().displayImage(
				avatarUrl,
				viewHolder.userLogo,
				MyApplication.getINSTANCE().getOptions(
						R.drawable.user_icon_default_main),
				new SimpleImageLoadingListener() {

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						super.onLoadingComplete(imageUri, view, loadedImage);
					}

				});
		viewHolder.userLogo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				QiangYuModel.getInstance().setCurrentQiangYu(entity);
				Intent intent = new Intent();
				intent.setClass(MyApplication.getINSTANCE().getTopActivity(),
						UserInfoActivity.class);
				mContext.startActivity(intent);
			}
		});
		viewHolder.userName.setText(entity.getAuthor().getUsername());
		viewHolder.contentText.setText(entity.getContent());
		if (null == entity.getContentfigureurl()) {
			viewHolder.contentImage.setVisibility(View.GONE);
		} else {
			viewHolder.contentImage.setVisibility(View.VISIBLE);
			ImageLoader
					.getInstance()
					.displayImage(
							entity.getContentfigureurl().getFileUrl(mContext) == null ? ""
									: entity.getContentfigureurl().getFileUrl(
											mContext),
							viewHolder.contentImage,
							MyApplication.getINSTANCE().getOptions(
									R.drawable.bg_pic_loading),
							new SimpleImageLoadingListener() {

								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									// TODO Auto-generated method stub
									super.onLoadingComplete(imageUri, view,
											loadedImage);
									float[] cons = ActivityUtil
											.getBitmapConfiguration((Activity) mContext,
													loadedImage,
													viewHolder.contentImage,
													1.0f);
									RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
											(int) cons[0], (int) cons[1]);

									layoutParams.addRule(RelativeLayout.BELOW,
											R.id.content_text);
									viewHolder.contentImage
											.setLayoutParams(layoutParams);
								}

							});
		}
		viewHolder.love.setText(entity.getLove() + "");
		Log.i("love", entity.getMyLove() + "..");
		if (entity.getMyLove()) {
			viewHolder.love.setTextColor(Color.parseColor("#D95555"));
		} else {
			viewHolder.love.setTextColor(Color.parseColor("#000000"));
		}
		viewHolder.love.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (entity.getMyLove()) {
					//ActivityUtil.show(mContext, "您已赞过啦");
					return;
				}

				/*if (DatabaseUtil.getInstance(mContext).isLoved(entity)) {
					//ActivityUtil.show(mContext, "您已赞过啦");
					entity.setMyLove(true);
					entity.setLove(entity.getLove() + 1);
					viewHolder.love.setTextColor(Color.parseColor("#D95555"));
					viewHolder.love.setText(entity.getLove() + "");
					return;
				}*/

				entity.setLove(entity.getLove() + 1);
				viewHolder.love.setTextColor(Color.parseColor("#D95555"));
				viewHolder.love.setText(entity.getLove() + "");

				entity.increment("love", 1);
				entity.update(mContext, new UpdateListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						entity.setMyLove(true);
						/*DatabaseUtil.getInstance(mContext).insertFav(entity);*/
						// DatabaseUtil.getInstance(mContext).queryFav();
						//LogUtils.i(TAG, "点赞成功~");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						entity.setMyLove(true);
					}
				});
			}
		});

		viewHolder.comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 评论
				// MyApplication.getInstance().setCurrentQiangYu(entity);

				Intent intent = new Intent();
				intent.setClass(MyApplication.getINSTANCE().getTopActivity(),
						CommentActivity.class);
				intent.putExtra("data", entity);
				mContext.startActivity(intent);
			}
		});

		return convertView;
	}


	public static class ViewHolder {
		public ImageView userLogo;
		public TextView userName;
		public TextView contentText;
		public ImageView contentImage;

		public TextView love;
		public TextView comment;
	}
}