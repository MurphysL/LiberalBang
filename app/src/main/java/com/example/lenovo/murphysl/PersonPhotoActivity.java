package com.example.lenovo.murphysl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.MyDate;
import com.example.lenovo.murphysl.bean.UserBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;

/**
 * PersonPhotoActivity
 *
 * @author: lenovo
 * @time: 2016/9/8 9:22
 */

public class PersonPhotoActivity extends ParentWithNaviActivity {

    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout swRefresh;
    @Bind(R.id.viewPager)
    ViewPager viewPager;

    private MyPagerAdapter adapter;

    private static final int INIT_POPUPVIEW = 1;

    private List<String> friendList = new ArrayList<String>();
    private List<Bitmap> photo = new ArrayList<>();
    private List<ImageView> imageViews = new ArrayList<>();

    @Override
    protected String title() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_f);
        ButterKnife.bind(this);
        initNaviView();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(this.getResources().getColor(R.color.green_theme));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        log("onStart");
        initSwRefresh();
        updateDate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        photo.clear();
        ButterKnife.unbind(this);
    }

    private void initSwRefresh() {
        swRefresh.setEnabled(true);
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateDate();
            }
        });
    }

    /**
     * 准备Date历史数据
     */
    private void updateDate() {

        if (friendList != null) {
            friendList.clear();
        }

        BmobQuery<MyDate> query = new BmobQuery<MyDate>();
        query.addWhereEqualTo("user", BmobUser.getCurrentUser(PersonPhotoActivity.this, UserBean.class));
        query.order("-friend");
        query.include("friend , user");
        query.findObjects(PersonPhotoActivity.this, new FindListener<MyDate>() {
            @Override
            public void onSuccess(final List<MyDate> list) {
                Iterator<MyDate> i = list.iterator();
                while (i.hasNext()) {
                    final MyDate date = i.next();
                    BmobFile bf = date.getPhoto();
                    final String url = date.getPhoto().getFileUrl(PersonPhotoActivity.this);
                    final String objectID = date.getObjectId();
                    UserBean friend = date.getFriend();
                    final String s = friend.getObjectId();
                    log("friend" + s);

                    bf.download(PersonPhotoActivity.this, new DownloadFileListener() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            log("开始下载");
                        }

                        @Override
                        public void onSuccess(String s) {
                            log("下载图片成功" + s);
                            //if (photo.get(name) != null) {
                            photo.add(BitmapFactory.decodeFile(s));

                            Message message = new Message();
                            message.what = INIT_POPUPVIEW;
                            handler.sendMessage(message);
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            log("加载失败");
                        }
                    });

                    /*Message msg = new Message();
                    msg.what = INIT_Q;
                    Bundle b = new Bundle();
                    b.putString("s", s);
                    b.getString("url", url);
                    b.putString("objectID", objectID);
                    msg.setData(b);
                    handler.sendMessage(msg);*/
                }
            }

            @Override
            public void onError(int i, String s) {
                toast("初始化出错");
            }
        });

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == INIT_POPUPVIEW) {
                adapter = new MyPagerAdapter(photo);
                viewPager.setAdapter(adapter);
            } else {
                Bundle bundle = msg.getData();
                if (bundle != null) {
                    String url = bundle.getString("url");
                    String name = bundle.getString("name");
                    String objectID = bundle.getString("objectID");

                    File file = new File(Environment.getExternalStorageDirectory().getPath() +
                            "/" + name + "/" + objectID + ".png");
                    //if(!file.exists()){
                    //downLoadPhotoByUrl(url, name, objectID);
                    // }
                }

            }

        }
    };

    class MyPagerAdapter extends PagerAdapter {

        List<Bitmap> list;

        public MyPagerAdapter(List<Bitmap> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = null;
            log("position" + position);
            log("imageView" + imageViews.size());
            imageView = new ImageView(PersonPhotoActivity.this);
            log("list size" + list.size());
            imageView.setImageBitmap(list.get(position));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViews.add(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews.get(position));
        }
    }


    @Override
    public Object right() {
        return R.drawable.base_action_bar_add_bg_selector;
    }

    @Override
    public ToolBarListener setToolBarListener() {
        return new ToolBarListener() {
            @Override
            public void clickLeft() {
                finish();
            }

            @Override
            public void clickRight() {

            }
        };
    }
}
