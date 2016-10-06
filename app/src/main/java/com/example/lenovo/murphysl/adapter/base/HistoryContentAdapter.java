package com.example.lenovo.murphysl.adapter.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.murphysl.MyApplication;
import com.example.lenovo.murphysl.NewChatActivity;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.bean.QiangYu;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.model.QiangYuModel;
import com.example.lenovo.murphysl.model.UserModel;
import com.example.lenovo.murphysl.moment.CommentActivity;
import com.example.lenovo.murphysl.util.ActivityUtil;
import com.example.lenovo.murphysl.util.StringUtils;
import com.example.lenovo.murphysl.view.UserInfoActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.listener.UpdateListener;

/**
 * HistoryContentAdapter
 *
 * 历史求助
 *
 * @author: MurphySL
 * @time: 2016/9/20 17:56
 */

public class HistoryContentAdapter extends BaseContentAdapter<QiangYu> {

    private Context mContext;

    public HistoryContentAdapter( Context context, List<QiangYu> list) {
        super(context, list);
        mContext = context;
    }


    @Override
    public View getConvertView(int position, View convertView, ViewGroup parent) {
        String[] keyWords = {
                mContext.getResources().getString(R.string.ac_eat_chinese),
                mContext.getResources().getString(R.string.ac_eat_buffet_dinner),
                mContext.getResources().getString(R.string.ac_eat_fast),
                mContext.getResources().getString(R.string.ac_eat_snack),
                mContext.getResources().getString(R.string.ac_eat_west),

                mContext.getResources().getString(R.string.ac_bar),
                mContext.getResources().getString(R.string.ac_entertainment),
                mContext.getResources().getString(R.string.ac_gym),
                mContext.getResources().getString(R.string.ac_internet),
                mContext.getResources().getString(R.string.ac_movie),

                mContext. getResources().getString(R.string.ac_tour_amusement),
                mContext.getResources().getString(R.string.ac_tour_museum),
                mContext.getResources().getString(R.string.ac_tour_park),
                mContext.getResources().getString(R.string.ac_tour_zoo),
                mContext.getResources().getString(R.string.ac_tour_view),

                mContext.getResources().getString(R.string.ac_play_badminton),
                mContext.getResources().getString(R.string.ac_play_ball),
                mContext.getResources().getString(R.string.ac_play_basketball),
                mContext.getResources().getString(R.string.ac_play_football),
                mContext.getResources().getString(R.string.ac_play_volleyball)};

        // TODO Auto-generated method stub
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.history_item, null);
            viewHolder.userName = (TextView) convertView
                    .findViewById(R.id.user_name);
            viewHolder.userLogo = (ImageView) convertView
                    .findViewById(R.id.user_logo);
            viewHolder.contentText = (TextView) convertView
                    .findViewById(R.id.content_text);
            viewHolder.contentMoney = (TextView) convertView
                    .findViewById(R.id.content_money);
            viewHolder.contentImage = (ImageView) convertView
                    .findViewById(R.id.content_image);
            viewHolder.love = (TextView) convertView
                    .findViewById(R.id.item_action_love);
            viewHolder.confrim = (Button) convertView
                    .findViewById(R.id.confrim);
            viewHolder.comment = (TextView) convertView
                    .findViewById(R.id.item_action_comment);
            viewHolder.geo = (TextView) convertView.findViewById(R.id.content_geo);
            viewHolder.cancel = (Button) convertView.findViewById(R.id.cancel);
            viewHolder.state = (TextView) convertView.findViewById(R.id.state);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final QiangYu entity = dataList.get(position);
        Log.i("user", entity.toString());
        final UserBean user = entity.getAuthor();
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

        viewHolder.userLogo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                QiangYuModel.getInstance().setCurrentQiangYu(entity);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("u" , user);
                intent.putExtra("com.example.lenovo.murphysl" , bundle);
                intent.setClass(MyApplication.getINSTANCE().getTopActivity(), UserInfoActivity.class);
                mContext.startActivity(intent);
            }
        });

       if(entity.getState() == 1){
            viewHolder.state.setText("进行中");
            viewHolder.confrim.setVisibility(View.VISIBLE);
            viewHolder.cancel.setText("取消");
            /*viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(entity.getState() == 0){
                       viewHolder.cancel.setBackgroundResource(R.color.color_99);
                       final QiangYu qiangYu = new QiangYu();
                       qiangYu.setState(1);
                       qiangYu.setHelper(UserModel.getInstance().getUser());
                       qiangYu.update(MyApplication.getINSTANCE().getTopActivity(), entity.getObjectId(),
                               new UpdateListener() {
                                   @Override
                                   public void onSuccess() {
                                       //开始对话
                                       BmobIMUserInfo info = new BmobIMUserInfo(
                                               entity.getAuthor().getObjectId(),entity.getAuthor().getUsername(),entity.getAuthor().getAvatar());
                                       BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info,false,null);
                                       Intent intent = new Intent();
                                       Bundle bundle = new Bundle();
                                       bundle.putSerializable("c", c);
                                       bundle.putString("q" , qiangYu.getObjectId());
                                       intent.putExtra("com.example.lenovo.murphysl" , bundle);
                                       intent.setClass(MyApplication.getINSTANCE().getTopActivity(), NewChatActivity.class);
                                       mContext.startActivity(intent);
                                   }

                                   @Override
                                   public void onFailure(int i, String s) {
                                   }
                               });
                   }else{
                       Toast.makeText(MyApplication.getINSTANCE().getTopActivity()
                               , "已经有人在帮助他了" , Toast.LENGTH_SHORT).show();
                   }

               }
           });*/
        }else if(entity.getState() == 2){
            viewHolder.state.setText("已完成");
            viewHolder.cancel.setText("删除");
        }
        viewHolder.userName.setText(entity.getAuthor().getUsername());
        viewHolder.contentText.setText(StringUtils.highlight(entity.getContent() , keyWords));
        viewHolder.contentMoney.setText(entity.getMoney() + "");
        viewHolder.geo.setText(entity.getGeo());
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
        viewHolder.love.setOnClickListener(new View.OnClickListener() {

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

        viewHolder.comment.setOnClickListener(new View.OnClickListener() {

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
        public TextView contentMoney;
        public ImageView contentImage;
        public TextView state;
        public Button cancel;
        public Button confrim;

        public TextView love;
        public TextView comment;
        public TextView geo;
    }

}
