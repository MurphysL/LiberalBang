package com.example.lenovo.murphysl;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.lenovo.murphysl.adapter.ChatAdapter;
import com.example.lenovo.murphysl.adapter.OnRecyclerViewListener;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.MoveLine;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.event.LocationEvent;
import com.example.lenovo.murphysl.map.Location;
import com.example.lenovo.murphysl.map.MyOrientationListener;
import com.example.lenovo.murphysl.model.UserModel;
import com.example.lenovo.murphysl.util.LocConfig;
import com.example.lenovo.murphysl.util.Util;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMVideoMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.BmobRecordManager;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.listener.OnRecordChangeListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * NewChatActivity
 *
 * @author: lenovo
 * @time: 2016/9/9 15:03
 */

public class NewChatActivity extends ParentWithNaviActivity implements ObseverListener, MessageListHandler {

    @Bind(R.id.mMapView)
    MapView mMapView;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout swRefresh;
    @Bind(R.id.iv_record)
    ImageView iv_record;
    @Bind(R.id.tv_voice_tips)
    TextView tv_voice_tips;
    @Bind(R.id.layout_record)
    RelativeLayout layout_record;
    @Bind(R.id.edit_msg)
    EditText edit_msg;
    @Bind(R.id.btn_speak)
    Button btn_speak;
    @Bind(R.id.btn_chat_voice)
    Button btn_chat_voice;
    @Bind(R.id.btn_chat_keyboard)
    Button btn_chat_keyboard;
    @Bind(R.id.btn_chat_send)
    Button btn_chat_send;
    @Bind(R.id.tv_picture)
    TextView tvPicture;
    @Bind(R.id.tv_camera)
    TextView tvCamera;
    @Bind(R.id.ll_chat)
    LinearLayout ll_chat;
    @Bind(R.id.rc_view)
    RecyclerView rc_view;
    @Bind(R.id.geo_dis)
    TextView geoDis;
    @Bind(R.id.geo_time)
    TextView geoTime;
    @Bind(R.id.map)
    LinearLayout map;

    private Drawable[] drawable_Anims;// 话筒动画
    private BmobRecordManager recordManager;

    private ChatAdapter adapter;
    protected LinearLayoutManager layoutManager;
    private BmobIMConversation c;

    private String address = Environment.getExternalStorageDirectory().getPath() + "/temp.png";
    private static final int CODE_IMAGE = 1;
    private static final int CODE_CAMERA = 2;
    private FileInputStream fis;

    private String aroundID;

    private static final int MSG_CHANGE_GEO = 0;
    private static final int MSG_CREATE_GEO = 1;
    private BaiduMap mBaiduMap = null;

    private LatLng pt;//所在经纬

    private Location location;
    private MyOrientationListener myOrientationListener;
    private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果
    private List<LatLng> historyList = new ArrayList<>();//记录轨迹
    private List<BmobGeoPoint> geoList = new ArrayList<>();//Bmob格式
    private boolean isFirstIn = true;

    private float orientationX;//旋转方向

    private String otherID = null;
    private String qiangYu = null;
    private UserBean user = UserModel.getInstance().getUser();

    private double speed = 0.0;// m/s
    private String geo;

    private boolean flag = true;

    /**
     * 封装定位结果和时间的实体类
     */
    class LocationEntity {
        BDLocation location;
        long time;
    }

    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null) {
                if (bdLocation.hasSpeed()) {
                    speed = bdLocation.getSpeed() * 3.6;
                    geo = bdLocation.getAddrStr();
                    geoDis.setText(geo);
                }
                Algorithm(bdLocation);
            }
        }
    };

    @Override
    protected String title() {
        return c.getConversationTitle();
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
                if(flag == true){
                    map.setVisibility(View.VISIBLE);
                    flag = false;
                }else{
                    map.setVisibility(View.GONE);
                }

            }
        };
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        c = BmobIMConversation.obtain(BmobIMClient.getInstance(), (BmobIMConversation) getBundle().getSerializable("c"));
        initNaviView();
        ButterKnife.bind(this);

        aroundID = c.getConversationId();
        log("around" + aroundID);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(this.getResources().getColor(R.color.green_theme));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        initLocation();
        initMyLine();
        initOtherLine();
        initSwipeLayout();
        initVoiceView();
        initBottomView();

        otherID = getIntent().getBundleExtra("com.example.lenovo.murphysl").getString("ID");
        log("otherID" + otherID);
        qiangYu = getIntent().getBundleExtra("com.example.lenovo.murphysl").getString("q");
        log("qiangYu:" + qiangYu);
    }

    private void initOtherLine() {
        MoveLine ml = new MoveLine();
        BmobQuery<MoveLine> q = new BmobQuery<>();
        q.addWhereEqualTo("user", otherID);
        log("otherID" + otherID);
        q.findObjects(NewChatActivity.this, new FindListener<MoveLine>() {
            @Override
            public void onSuccess(List<MoveLine> list) {
                if (list.size() == 0) {
                    toast("对方还未开启路径分享功能");
                } else {
                    log(list.size() + "");
                    otherID = list.get(0).getObjectId();
                }
            }

            @Override
            public void onError(int i, String s) {
                toast("初始化对方路线错误");
            }
        });
    }

    private void initMyLine() {
        handler.postDelayed(runnable, 5000);
        BmobQuery<MoveLine> q = new BmobQuery<>();
        q.addWhereEqualTo("user", user);
        q.findObjects(NewChatActivity.this, new FindListener<MoveLine>() {
            @Override
            public void onSuccess(List<MoveLine> list) {
                if (list.size() != 0) {
                    final MoveLine ml = list.get(0);
                    String mlID = ml.getObjectId();
                    log("MoveLine：" + mlID);
                    ml.delete(NewChatActivity.this, mlID, new DeleteListener() {
                        @Override
                        public void onSuccess() {
                            log("清理上次完毕");
                            /*BmobQuery<QiangYu> q = new BmobQuery<QiangYu>();
                            q.getObject(NewChatActivity.this, qiangYu, new GetListener<QiangYu>() {
                                @Override
                                public void onSuccess(QiangYu qiangYu) {
                                    qiangYu.setAskLine(ml);
                                    qiangYu.save(NewChatActivity.this, new SaveListener() {
                                        @Override
                                        public void onSuccess() {
                                            log("路线与Moment链接成功");
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            log("路线与Moment链接失败" + s);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });*/
                            Message msg = new Message();
                            msg.what = MSG_CREATE_GEO;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            log("清理上次失败");
                        }
                    });
                } else {
                    Message msg = new Message();
                    msg.what = MSG_CREATE_GEO;
                    handler.sendMessage(msg);
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void initLocation() {
        location = MyApplication.getINSTANCE().location;
        location.registerLocListener(mListener);

        mBaiduMap = mMapView.getMap();
        //设置初始视距
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setMyLocationEnabled(true);

        myOrientationListener = new MyOrientationListener(this);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                orientationX = x;
            }
        });
    }

    /**
     * 平滑策略代码实现方法，主要通过对新定位和历史定位结果进行速度评分，
     * 来判断新定位结果的抖动幅度，如果超过经验值，则判定为过大抖动，进行平滑处理,若速度过快，
     * 则推测有可能是由于运动速度本身造成的，则不进行低速平滑处理
     *
     * @param bdLocation
     */
    private void Algorithm(BDLocation bdLocation) {

        if (locationList.isEmpty() || locationList.size() < 2) {
            LocationEntity temp = new LocationEntity();
            temp.location = bdLocation;
            temp.time = System.currentTimeMillis();
            locationList.add(temp);
        } else {
            if (locationList.size() > 5)
                locationList.removeFirst();
            double score = 0;
            for (int i = 0; i < locationList.size(); ++i) {
                LatLng lastPoint = new LatLng(locationList.get(i).location.getLatitude(), locationList.get(i).location.getLongitude());
                LatLng curPoint = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                double distance = DistanceUtil.getDistance(lastPoint, curPoint);
                double curSpeed = distance / (System.currentTimeMillis() - locationList.get(i).time) / 1000;
                score += curSpeed * LocConfig.EARTH_WEIGHT[i];
            }
            if (score > 0.00000999 && score < 0.00005) { // 经验值
                bdLocation.setLongitude(
                        (locationList.get(locationList.size() - 1).location.getLongitude() + bdLocation.getLongitude())
                                / 2);
                bdLocation.setLatitude(
                        (locationList.get(locationList.size() - 1).location.getLatitude() + bdLocation.getLatitude())
                                / 2);
            }
            LocationEntity newLocation = new LocationEntity();
            newLocation.location = bdLocation;
            newLocation.time = System.currentTimeMillis();
            locationList.add(newLocation);
        }
        EventBus.getDefault().post(new LocationEvent(bdLocation));
    }

    /**
     * 当前位置
     */
    private void myLoction(LatLng latLng) {
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);//设置动画
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventLoc(LocationEvent event) {
        BDLocation location = event.getLocData();
        //存储位置
        LocationEntity temp = new LocationEntity();
        temp.location = location;
        temp.time = System.currentTimeMillis();
        historyList.add(new LatLng(location.getLatitude(), location.getLongitude()));

        if (location != null) {
            MyLocationData data = new MyLocationData.Builder()
                    .direction(orientationX)//定位数据方向
                    .accuracy(location.getRadius())//定位数据精度信息
                    .latitude(location.getLatitude())//定位数据的纬度
                    .longitude(location.getLongitude())//定位数据的经度
                    .build();//生成定位数据对象
            mBaiduMap.setMyLocationData(data);

            //BitmapDescriptor mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
            //MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mIconLocation);
            //mBaiduMap.setMyLocationConfigeration(config);

            //更新经纬度
            pt = new LatLng(location.getLatitude(), location.getLongitude());

            if (isFirstIn) {
                myLoction(pt);
                isFirstIn = false;
            }
        }
    }

    private String lineID;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CHANGE_GEO:
                    MoveLine ml = new MoveLine();
                    ml.setList(geoList);
                    ml.update(NewChatActivity.this, lineID, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            log("上传成功");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            log("上传失败" + s);
                        }
                    });
                    break;
                case MSG_CREATE_GEO:
                    final MoveLine m = new MoveLine();
                    m.setUser(UserModel.getInstance().getUser());
                    m.setList(geoList);
                    m.save(NewChatActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            lineID = m.getObjectId();
                            log("上传成功:" + lineID);
                           /* BmobQuery<QiangYu> q = new BmobQuery<QiangYu>();
                            q.getObject(NewChatActivity.this, qiangYu, new GetListener<QiangYu>() {
                                @Override
                                public void onSuccess(QiangYu qiangYu) {
                                    qiangYu.setAskLine(m);
                                    qiangYu.save(NewChatActivity.this, new SaveListener() {
                                        @Override
                                        public void onSuccess() {
                                            log("路线与Moment链接成功");
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            log("路线与Moment链接失败" + s);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });*/
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            log("上传失败" + s);
                        }
                    });
                    break;
            }
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            drawLine(historyList);
            handler.postDelayed(runnable, 5000);
        }
    };

    private void drawLine(List<LatLng> list) {
        Iterator<LatLng> i = list.iterator();
        while (i.hasNext()) {
            LatLng latLng = i.next();
            Double latitude = latLng.latitude;
            Double longitude = latLng.longitude;
            BmobGeoPoint geo = new BmobGeoPoint(longitude, latitude);
            geoList.add(geo);
            log("latitude:" + latitude + "\nlongitude:" + longitude);
        }

       /* LatLng pt1 = new LatLng(39.93923, 116.357428);
        LatLng pt2 = new LatLng(39.91923, 116.327428);
        LatLng pt3 = new LatLng(39.89923, 116.347428);
        LatLng pt4 = new LatLng(39.89923, 116.367428);
        LatLng pt5 = new LatLng(39.91923, 116.387428);
        List<LatLng> list = new ArrayList<>();
        list.add(pt1);
        list.add(pt2);
        list.add(pt3);
        list.add(pt4);
        list.add(pt5);

        LatLng pt6 = new LatLng(40.93923, 116.357428);
        LatLng pt7 = new LatLng(40.91923, 116.327428);
        LatLng pt8 = new LatLng(40.89923, 116.347428);
        LatLng pt9 = new LatLng(40.89923, 116.367428);
        LatLng pt0 = new LatLng(40.91923, 116.387428);
        List<LatLng> list2 = new ArrayList<>();
        list2.add(pt7);
        list2.add(pt8);
        list2.add(pt9);
        list2.add(pt6);
        list2.add(pt0);*/


        /*Message msg = new Message();
        msg.what = MSG_CHANGE_GEO;
        handler.sendMessage(msg);*/

        //另一个人的路径
            /*log("otherID" + otherID);
            BmobQuery<List> q = new BmobQuery<>();//?List<BmobGeoPoint>
            q.getObject(NewChatActivity.this, otherID, new GetListener<List>() {
                @Override
                public void onSuccess(List bmobGeoPoints) {
                    Iterator i = bmobGeoPoints.iterator();
                    List<LatLng> ll = new ArrayList<LatLng>();
                    while (i.hasNext()) {
                        BmobGeoPoint b = (BmobGeoPoint) i.next();
                        Double la = b.getLatitude();
                        Double lo = b.getLongitude();
                        LatLng l = new LatLng(la, lo);
                        ll.add(l);
                    }
                    log("draw Other");
                    if (ll != null && historyList != null) {
                        Double dis = DistanceUtil.getDistance(ll.get(ll.size() - 1)
                                , historyList.get(historyList.size() - 1));
                        geoDis.setText("相距" + dis);
                        if (dis < 10) {
                            toast("已到达附近");
                        }
                    }
                    BitmapDescriptor custom1 = BitmapDescriptorFactory
                            .fromResource(R.drawable.low_poly_texture);

                    OverlayOptions polygonOption = new PolylineOptions()
                            .points(ll)
                            .width(50)
                            .color(0xAAFF0000)
                            .customTexture(custom1);
                    mBaiduMap.addOverlay(polygonOption);
                }

                @Override
                public void onFailure(int i, String s) {
                    log("failure" + s );
                }
            });*/

        log("draw");

        if (list.size() > 2) {
            OverlayOptions polygonOption = new PolylineOptions()
                    .points(list)
                    .width(15)
                    .color(Color.GREEN);
            mBaiduMap.addOverlay(polygonOption);
        }

    }


    private void initSwipeLayout() {
        swRefresh.setEnabled(true);
        layoutManager = new LinearLayoutManager(this);
        rc_view.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(this, c);
        rc_view.setAdapter(adapter);
        ll_chat.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll_chat.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                swRefresh.setRefreshing(true);
                //自动刷新
                queryMessages(null);
            }
        });
        //下拉加载
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BmobIMMessage msg = adapter.getFirstMessage();
                queryMessages(msg);
            }
        });
        //设置RecyclerView的点击事件
        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                Logger.i("" + position);
            }

            @Override
            public boolean onItemLongClick(int position) {
                c.deleteMessage(adapter.getItem(position));
                adapter.remove(position);
                return true;
            }
        });
    }

    private void initBottomView() {
        edit_msg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
                    scrollToBottom();
                }
                return false;
            }
        });
        edit_msg.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                scrollToBottom();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btn_chat_send.setVisibility(View.VISIBLE);
                    btn_chat_keyboard.setVisibility(View.GONE);
                    btn_chat_voice.setVisibility(View.GONE);
                } else {
                    if (btn_chat_voice.getVisibility() != View.VISIBLE) {
                        btn_chat_voice.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);
                        btn_chat_keyboard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 初始化语音布局
     *
     * @param
     * @return void
     */
    private void initVoiceView() {
        btn_speak.setOnTouchListener(new VoiceTouchListener());
        initVoiceAnimRes();
        initRecordManager();
    }

    /**
     * 初始化语音动画资源
     *
     * @param
     * @return void
     * @Title: initVoiceAnimRes
     */
    private void initVoiceAnimRes() {
        drawable_Anims = new Drawable[]{
                getResources().getDrawable(R.mipmap.chat_icon_voice2),
                getResources().getDrawable(R.mipmap.chat_icon_voice3),
                getResources().getDrawable(R.mipmap.chat_icon_voice4),
                getResources().getDrawable(R.mipmap.chat_icon_voice5),
                getResources().getDrawable(R.mipmap.chat_icon_voice6)};
    }

    private void initRecordManager() {
        // 语音相关管理器
        recordManager = BmobRecordManager.getInstance(this);
        // 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
        recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

            @Override
            public void onVolumnChanged(int value) {
                iv_record.setImageDrawable(drawable_Anims[value]);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                Logger.i("voice", "已录音长度:" + recordTime);
                if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
                    // 需要重置按钮
                    btn_speak.setPressed(false);
                    btn_speak.setClickable(false);
                    // 取消录音框
                    layout_record.setVisibility(View.INVISIBLE);
                    // 发送语音消息
                    sendVoiceMessage(localPath, recordTime);
                    //是为了防止过了录音时间后，会多发一条语音出去的情况。
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            btn_speak.setClickable(true);
                        }
                    }, 1000);
                }
            }
        });
    }

    /**
     * 长按说话
     *
     * @author smile
     * @date 2014-7-1 下午6:10:16
     */
    class VoiceTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!Util.checkSdCard()) {
                        toast("发送语音需要sdcard支持！");
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        layout_record.setVisibility(View.VISIBLE);
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        // 开始录音
                        recordManager.startRecording(c.getConversationId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        tv_voice_tips.setTextColor(Color.RED);
                    } else {
                        tv_voice_tips.setText(getString(R.string.voice_up_tips));
                        tv_voice_tips.setTextColor(Color.WHITE);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    layout_record.setVisibility(View.INVISIBLE);
                    try {
                        if (event.getY() < 0) {// 放弃录音
                            recordManager.cancelRecording();
                            Logger.i("voice", "放弃发送语音");
                        } else {
                            int recordTime = recordManager.stopRecording();
                            if (recordTime > 1) {
                                // 发送语音文件
                                sendVoiceMessage(recordManager.getRecordFilePath(c.getConversationId()), recordTime);
                            } else {// 录音时间过短，则提示录音过短的提示
                                layout_record.setVisibility(View.GONE);
                                showShortToast().show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    Toast toast;

    /**
     * 显示录音时间过短的Toast
     *
     * @return void
     * @Title: showShortToast
     */
    private Toast showShortToast() {
        if (toast == null) {
            toast = new Toast(this);
        }
        View view = LayoutInflater.from(this).inflate(
                R.layout.include_chat_voice_short, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        return toast;
    }

    @OnClick({R.id.iv_record, R.id.tv_voice_tips, R.id.layout_record, R.id.edit_msg, R.id.btn_speak, R.id.btn_chat_voice, R.id.btn_chat_keyboard, R.id.btn_chat_send, R.id.tv_picture, R.id.tv_camera, R.id.ll_chat})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_record:
                break;
            case R.id.tv_voice_tips:
                break;
            case R.id.layout_record:
                break;
            case R.id.edit_msg:
                break;
            case R.id.btn_speak:
                break;
            case R.id.btn_chat_voice:
                edit_msg.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.GONE);
                btn_chat_keyboard.setVisibility(View.VISIBLE);
                btn_speak.setVisibility(View.VISIBLE);
                hideSoftInputView();
                break;
            case R.id.btn_chat_keyboard:
                showEditState(false);
                break;
            case R.id.btn_chat_send:
                sendMessage();
                break;
            case R.id.tv_picture:
                startActivityForResult(new Intent(this, ImageActivity.class), CODE_IMAGE);
                break;
            case R.id.tv_camera:
                sendImageMessage();
                break;
            case R.id.ll_chat:
                break;
        }
    }


    /**
     * 根据是否点击笑脸来显示文本输入框的状态
     *
     * @param isEmo 用于区分文字和表情
     * @return void
     */
    private void showEditState(boolean isEmo) {
        edit_msg.setVisibility(View.VISIBLE);
        btn_chat_keyboard.setVisibility(View.GONE);
        btn_chat_voice.setVisibility(View.VISIBLE);
        btn_speak.setVisibility(View.GONE);
        edit_msg.requestFocus();
        if (isEmo) {
            hideSoftInputView();
        } else {
            showSoftInputView();
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(edit_msg, 0);
        }
    }

    /**
     * 发送文本消息
     */
    private void sendMessage() {
        String text = edit_msg.getText().toString();
        if (TextUtils.isEmpty(text.trim())) {
            toast("请输入内容");
            return;
        }
        BmobIMTextMessage msg = new BmobIMTextMessage();
        msg.setContent(text);
        //可设置额外信息
        Map<String, Object> map = new HashMap<>();
        map.put("level", "1");//随意增加信息
        msg.setExtraMap(map);
        c.sendMessage(msg, listener);
    }

    /**
     * 发送图片
     */
    public void sendImageMessage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = Uri.fromFile(new File(address));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CODE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CODE_CAMERA) {
                BmobIMImageMessage image = new BmobIMImageMessage(address);
                c.sendMessage(image, listener);
            } else if (requestCode == CODE_IMAGE) {
                List<String> list = data.getStringArrayListExtra("imgs");
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    BmobIMImageMessage image = new BmobIMImageMessage((String) iterator.next());
                    c.sendMessage(image, listener);
                }
            }
        }
    }


    /**
     * 发送语音消息
     *
     * @param local
     * @param length
     * @return void
     * @Title: sendVoiceMessage
     */
    private void sendVoiceMessage(String local, int length) {
        BmobIMAudioMessage audio = new BmobIMAudioMessage(local);
        //可设置额外信息-开发者设置的额外信息，需要开发者自己从extra中取出来
        Map<String, Object> map = new HashMap<>();
        map.put("from", "优酷");
        audio.setExtraMap(map);
        //设置语音文件时长：可选
//        audio.setDuration(length);
        c.sendMessage(audio, listener);
    }

    /**
     * 发送视频文件
     */
    private void sendVideoMessage() {
        BmobIMVideoMessage video = new BmobIMVideoMessage("/storage/sdcard0/bimagechooser/11.png");
        c.sendMessage(video, listener);
    }

    /**
     * 消息发送监听器
     */
    public MessageSendListener listener = new MessageSendListener() {

        @Override
        public void onProgress(int value) {
            super.onProgress(value);
            //文件类型的消息才有进度值
            Logger.i("onProgress：" + value);
        }

        @Override
        public void onStart(BmobIMMessage msg) {
            super.onStart(msg);
            adapter.addMessage(msg);
            edit_msg.setText("");
            scrollToBottom();
        }

        @Override
        public void done(BmobIMMessage msg, BmobException e) {
            adapter.notifyDataSetChanged();
            edit_msg.setText("");
            scrollToBottom();
            if (e != null) {
                toast(e.getMessage());
            }
        }
    };

    /**
     * 首次加载，可设置msg为null，下拉刷新的时候，默认取消息表的第一个msg作为刷新的起始时间点，默认按照消息时间的降序排列
     *
     * @param msg
     */
    public void queryMessages(BmobIMMessage msg) {
        c.queryMessages(msg, 10, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                swRefresh.setRefreshing(false);
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        adapter.addMessages(list);
                        layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                    }
                } else {
                    toast(e.getMessage() + "(" + e.getErrorCode() + ")");
                }
            }
        });
    }

    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        //Logger.i("聊天页面接收到消息：" + list.size());
        //当注册页面消息监听时候，有消息（包含离线消息）到来时会回调该方法
        for (int i = 0; i < list.size(); i++) {
            addMessage2Chat(list.get(i));
        }
    }

//    /**接收到聊天消息
//     * @param event
//     */
//    @Subscribe
//    public void onEventMainThread(MessageEvent event){
//        addMessage2Chat(event);
//    }
//
//    @Subscribe
//    public void onEventMainThread(OfflineMessageEvent event){
//        Map<String,List<MessageEvent>> map =event.getEventMap();
//        if(map!=null&&map.size()>0){
//            //只获取当前聊天对象的离线消息
//            List<MessageEvent> list = map.get(c.getConversationId());
//            if(list!=null && list.size()>0){
//                for (int i=0;i<list.size();i++){
//                    addMessage2Chat(list.get(i));
//                }
//            }
//        }
//    }

    /**
     * 添加消息到聊天界面中
     *
     * @param event
     */
    private void addMessage2Chat(MessageEvent event) {
        BmobIMMessage msg = event.getMessage();
        if (c != null && event != null && c.getConversationId().equals(event.getConversation().getConversationId()) //如果是当前会话的消息
                && !msg.isTransient()) {//并且不为暂态消息
            if (adapter.findPosition(msg) < 0) {//如果未添加到界面中
                adapter.addMessage(msg);
                //更新该会话下面的已读状态
                c.updateReceiveStatus(msg);
            }
            scrollToBottom();
        } else {
            Logger.i("不是与当前聊天对象的消息");
        }
    }


    @Override
    protected void onResume() {
        //锁屏期间的收到的未读消息需要添加到聊天界面中
        addUnReadMessage();
        //添加页面消息监听器
        BmobIM.getInstance().addMessageListHandler(this);
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知
        BmobNotificationManager.getInstance(this).cancelNotification();
        super.onResume();
    }

    /**
     * 添加未读的通知栏消息到聊天界面
     */
    private void addUnReadMessage() {
        List<MessageEvent> cache = BmobNotificationManager.getInstance(this).getNotificationCacheList();
        if (cache.size() > 0) {
            int size = cache.size();
            for (int i = 0; i < size; i++) {
                MessageEvent event = cache.get(i);
                addMessage2Chat(event);
            }
        }
        scrollToBottom();
    }

    @Override
    protected void onStart() {
        super.onStart();
        location.start();
        myOrientationListener.start();
    }

    @Override
    protected void onResumeFragments() {
        mMapView.onResume();
        super.onResumeFragments();
    }

    @Override
    protected void onPause() {
        //移除页面消息监听器
        BmobIM.getInstance().removeMessageListHandler(this);
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        myOrientationListener.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        location.unregisterLocListener(mListener);
        location.stop();

        //清理资源
        if (recordManager != null) {
            recordManager.clear();
        }
        //更新此会话的所有消息为已读状态
        if (c != null) {
            c.updateLocalCache();
        }
        hideSoftInputView();
        super.onDestroy();
    }

}

