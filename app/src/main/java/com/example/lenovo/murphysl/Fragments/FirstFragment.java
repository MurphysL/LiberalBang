package com.example.lenovo.murphysl.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.murphysl.MapActivity;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.base.ParentWithNaviFragment;
import com.example.lenovo.murphysl.bean.MyDate;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.moment.EditActivity;
import com.example.lenovo.murphysl.ui.PopupListView;
import com.example.lenovo.murphysl.ui.PopupView;
import com.example.lenovo.murphysl.util.DepthPageTransformer;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

/**
 * FirstFragment
 * <p/>
 * 问题：
 * 1、环形进度条
 * 2、UserModel user 线程
 *
 * @author: lenovo
 * @time: 2016/8/4 18:49
 */

public class FirstFragment extends ParentWithNaviFragment {

    @Bind(R.id.popupListView)
    PopupListView popupListView;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout swRefresh;

    private ViewPager viewPager;
    private MyPagerAdapter adapter;

    private static final int INIT_POPUPVIEW = 1;

    private ArrayList<PopupView> popupViews;

    private List<String> friendList = new ArrayList<String>();
    private Map<String , List<Bitmap>> photo = new HashMap<>();
    private List<ImageView> imageViews = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_f, container, false);
        initNaviView();
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        log("onStart");
        mBackHandledInterface.setSelectedFragment(this); //告诉FragmentActivity，当前Fragment在栈顶
        initSwRefresh();
        updateDate();
    }

    @Override
    public void onDestroyView() {
        photo.clear();
        ButterKnife.unbind(this);
        super.onDestroyView();
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
        //swRefresh.setRefreshing(true);

        if(friendList != null){
            friendList.clear();
        }
        if(popupViews != null){
            popupViews.clear();
        }

        BmobQuery<MyDate> query = new BmobQuery<MyDate>();
        query.addWhereEqualTo("user", BmobUser.getCurrentUser(getActivity(), UserBean.class));
        query.order("-friend");
        query.include("friend , user");
        query.findObjects(getActivity(), new FindListener<MyDate>() {
            @Override
            public void onSuccess(final List<MyDate> list) {
                Iterator<MyDate> i = list.iterator();
                while (i.hasNext()) {
                    final MyDate date = i.next();
                    final String url = date.getPhoto().getFileUrl(getActivity());
                    final String objectID = date.getObjectId();
                    UserBean friend = date.getFriend();
                    final String s = friend.getObjectId();

                    BmobQuery<UserBean> q = new BmobQuery<UserBean>();
                    q.getObject(getActivity(), s, new GetListener<UserBean>() {
                        @Override
                        public void onSuccess(UserBean userBean) {
                            String name = userBean.getUsername();
                            Message msg = new Message();
                            Bundle bundle = new Bundle();

                            compareFriendList(name);

                            bundle.putString("url" , url);
                            bundle.putString("name" , name);
                            bundle.putString("objectID" , objectID);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            //swRefresh.setRefreshing(false);
                        }


                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {
                toast("初始化出错");
            }
        });

    }

    private synchronized void compareFriendList(String name){
        boolean flag = false;
        if(friendList.size() == 0){
            friendList.add(name);
            photo.put(name , new ArrayList<Bitmap>());
            log("name" + name);
        }else{
            Iterator<String> it = friendList.iterator();
            log("friendList" + friendList);
            while(it.hasNext()){
                if(name.equals(it.next())){
                    flag = true;
                }
            }
            if(!flag){
                log("name" + name);
                friendList.add(name);
                photo.put(name , new ArrayList<Bitmap>());
            }
        }
    }


    private void downLoadPhotoByUrl(String url , final String name , String objectID) {

        final String address = Environment.getExternalStorageDirectory().getPath() +
                "/" + name + "/" + objectID + ".png";

        BmobFile bf = new BmobFile(address , "" , url);
        bf.download(getActivity(), new DownloadFileListener() {
            @Override
            public void onStart() {
                super.onStart();
                log("开始下载");
            }

            @Override
            public void onSuccess(String s) {
                log("下载图片成功" + s);
                log("finl" + name);
                if(photo.get(name) != null){
                    photo.get(name).add(BitmapFactory.decodeFile(s));
                }else{
                    photo.put(name , new ArrayList<Bitmap>());
                    photo.get(name).add(BitmapFactory.decodeFile(s));
                }
                Message message = new Message();
                message.what = INIT_POPUPVIEW;
                handler.sendMessage(message);
                if(adapter != null){
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                log("加载失败");
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == INIT_POPUPVIEW) {
                initPopupView();
            }else{
                Bundle bundle = msg.getData();
                if(bundle != null){
                    String url = bundle.getString("url");
                    String name = bundle.getString("name");
                    String objectID = bundle.getString("objectID");

                    File file = new File(Environment.getExternalStorageDirectory().getPath() +
                            "/" + name + "/" + objectID + ".png");
                    //if(!file.exists()){
                    downLoadPhotoByUrl(url , name , objectID);
                    // }
                }


            }

        }
    };

    private void initPopupView() {
        if(popupListView != null){
            popupListView.removeAllViews();
        }
        popupViews = new ArrayList<PopupView>();
        final Iterator<String> i = friendList.iterator();
        while (i.hasNext()) {
            final String s = i.next();
            PopupView popupView = new PopupView(getActivity(), R.layout.popup_view_item) {
                @Override
                public void setViewsElements(View view) {
                    TextView textView = (TextView) view.findViewById(R.id.friend);
                    textView.setText("朋友：" + s);
                }

                @Override
                public View setExtendView(View view) {
                    View extendView;
                    //if (view == null) {
                        log("View");
                        extendView = LayoutInflater.from(getActivity().getApplicationContext())
                                .inflate(R.layout.extend_view, null);
                        viewPager = (ViewPager) extendView.findViewById(R.id.id_viewpager);
                        //viewPager.setPageTransformer(true, new DepthPageTransformer());
                        //log("1" + photo.get("1").size() + "3" + photo.get("3").size());
                        adapter = new MyPagerAdapter(photo.get(s));
                        viewPager.setAdapter(adapter);
                    /*} else {
                        extendView = view;
                    }*/
                    return extendView;
                }
            };
            popupViews.add(popupView);
        }
        popupListView.init(null);
        popupListView.setItemViews(popupViews);

    }

    class MyPagerAdapter extends PagerAdapter{

        List<Bitmap> list;

        public MyPagerAdapter(List<Bitmap> list){
            this.list = list;
        }

        @Override
        public int getCount() {
            if(list == null){
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
            log("imageView"+ imageViews.size());
            imageView = new ImageView(getActivity());
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
    protected String title() {
        return "组局";
    }

    @Override
    public Object right() {
        return R.drawable.base_action_bar_add_bg_selector;
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {

            }

            @Override
            public void clickRight() {
                //startActivity(MapActivity.class, null);
                startActivity(EditActivity.class , null);
            }
        };
    }

    @Override
    public boolean onBackPressed() {
        if (popupListView.isItemZoomIn()) {
            popupListView.zoomOut();
            return true;
        } else {
            return false;
        }
    }
}
