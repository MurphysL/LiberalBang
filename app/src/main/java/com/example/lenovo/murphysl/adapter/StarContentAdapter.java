package com.example.lenovo.murphysl.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.murphysl.MyApplication;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.adapter.base.BaseContentAdapter;
import com.example.lenovo.murphysl.bean.UserBean;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;


/**
 * StarContentAdapter
 *
 * @author: MurphySL
 * @time: 2016/9/22 23:58
 */

public class StarContentAdapter extends BaseContentAdapter<UserBean> {

    public StarContentAdapter( Context context, List<UserBean> list) {
        super(context, list);
        mContext = context;
    }


    @Override
    public View getConvertView(int position, View convertView, ViewGroup parent) {

        // TODO Auto-generated method stub
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_cell, null);
            viewHolder.userName = (TextView) convertView
                    .findViewById(R.id.name);
            viewHolder.userLogo = (ImageView) convertView
                    .findViewById(R.id.icon);
            viewHolder.contentText = (TextView) convertView
                    .findViewById(R.id.dec);
            viewHolder.contentRank = (TextView) convertView
                    .findViewById(R.id.rank);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final UserBean entity = dataList.get(position);
        Log.i("user", entity.toString());
        if (entity == null) {
            Log.i("user", "USER IS NULL");
        }
        if (entity.getAvatar() == null) {
            Log.i("user", "USER avatar IS NULL");
        }
        String avatarUrl = null;
        if (entity.getAvatar() != null) {
            avatarUrl = entity.getAvatar();
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

        viewHolder.userName.setText(entity.getUsername());
        viewHolder.contentText.setText(entity.getHobby());
        viewHolder.contentRank.setText("" + (position + 1));

        return convertView;
    }


    public static class ViewHolder {
        public ImageView userLogo;
        public TextView userName;
        public TextView contentText;
        public TextView contentRank;
    }
}
