package com.example.lenovo.murphysl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.MoveLine;
import com.example.lenovo.murphysl.event.LocationEvent;
import com.example.lenovo.murphysl.map.Location;
import com.example.lenovo.murphysl.map.Location2;
import com.example.lenovo.murphysl.map.MyOrientationListener;
import com.example.lenovo.murphysl.model.UserModel;
import com.example.lenovo.murphysl.util.LocConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * DateActivity
 *
 * @author: lenovo
 * @time: 2016/8/ 13:47
 */

public class DateActivity extends ParentWithNaviActivity {

    private static final int MSG_CHANGE_GEO = 0;
    private static final int MSG_CREATE_GEO = 1;
    @Bind(R.id.mMapView)
    MapView mMapView;
    private BaiduMap mBaiduMap = null;

    private LatLng pt;//所在经纬
    private String city;//所在城市
    private int cityCode;//所在城市代码

    private Location location;
    private MyOrientationListener myOrientationListener;
    private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果
    private List<LatLng> historyList = new ArrayList<>();//记录轨迹
    private boolean isFirstIn = true;

    private float orientationX;//旋转方向

    private String otherID = null;

    @OnClick(R.id.stop)
    public void onClick() {
    }

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
                Algorithm(bdLocation);
            }
        }
    };

    @Override
    protected String title() {
        return "遇见";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        ButterKnife.bind(this);
        initLocation();
        otherID = getIntent().getBundleExtra("com.example.lenovo.murphysl").getString("ID");
        initMyLine();
        initOtherLine();

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

    private void initOtherLine() {
        MoveLine ml = new MoveLine();
        BmobQuery<MoveLine> q = new BmobQuery<>();
        q.addWhereEqualTo("user" , otherID);
        q.findObjects(DateActivity.this, new FindListener<MoveLine>() {
            @Override
            public void onSuccess(List<MoveLine> list) {
                if(list.size() == 0){
                    toast("对方还未开启路径分享功能");
                }else{
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
        handler.postDelayed(runnable , 5000);
        BmobQuery<MoveLine> q = new BmobQuery<>();
        q.addWhereEqualTo("user" , UserModel.getInstance().getUser());
        q.findObjects(DateActivity.this, new FindListener<MoveLine>() {
            @Override
            public void onSuccess(List<MoveLine> list) {
                if(list.size() != 0){
                    MoveLine ml = list.get(0);
                    String mlID = ml.getObjectId();
                    ml.delete(DateActivity.this, mlID, new DeleteListener() {
                        @Override
                        public void onSuccess() {
                            log("清理上次完毕");
                            Message msg = new Message();
                            msg.what = MSG_CREATE_GEO;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            log("清理上次失败");
                        }
                    });
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
        historyList.add(new LatLng(location.getLatitude() , location.getLongitude()));
        log(location.getLatitude() + "\\/" + location.getLongitude() + "");

        if (location != null) {
            MyLocationData data = new MyLocationData.Builder()
                    .direction(orientationX)//定位数据方向
                    .accuracy(location.getRadius())//定位数据精度信息
                    .latitude(location.getLatitude())//定位数据的纬度
                    .longitude(location.getLongitude())//定位数据的经度
                    .build();//生成定位数据对象
            mBaiduMap.setMyLocationData(data);

            BitmapDescriptor mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
            MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mIconLocation);
            mBaiduMap.setMyLocationConfigeration(config);

            //更新经纬度
            pt = new LatLng(location.getLatitude(), location.getLongitude());
            city = location.getCity();
            cityCode = Integer.parseInt(String.valueOf(location.getCityCode()));

            if (isFirstIn) {
                myLoction(pt);
                isFirstIn = false;
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        location.start();
        myOrientationListener.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        log("onStop");
        myOrientationListener.stop();
    }

    @Override
    protected void onDestroy() {
        log("onDestory");
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        location.unregisterLocListener(mListener);
        location.stop();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private String lineID;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CHANGE_GEO:
                    MoveLine ml = new MoveLine();
                    ml.setList(geoList);
                    ml.update(DateActivity.this,lineID , new UpdateListener() {
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
                    m.save(DateActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            log("上传成功");
                            lineID = m.getObjectId();
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
            handler.postDelayed(runnable , 5000);
        }
    };

    private List<BmobGeoPoint> geoList = new ArrayList<>();
    private void drawLine(List<LatLng> list){
        Iterator<LatLng> i = list.iterator();
        while (i.hasNext()){
            LatLng latLng  = i.next();
            Double latitude = latLng.latitude;
            Double longitude = latLng.longitude;
            BmobGeoPoint geo = new BmobGeoPoint(longitude , latitude);
            geoList.add(geo);
        }
        Message msg = new Message();
        msg.what = MSG_CHANGE_GEO;
        handler.sendMessage(msg);

        //另一个人的路径
        if(otherID != null || !otherID.equals("")){
            log("otherID" + otherID);
            BmobQuery<List> q = new BmobQuery<>();//?List<BmobGeoPoint>
            q.getObject(DateActivity.this, null , new GetListener<List>() {
                @Override
                public void onSuccess(List bmobGeoPoints) {
                    Iterator i = bmobGeoPoints.iterator();
                    List<LatLng> ll = new ArrayList<LatLng>();
                    while(i.hasNext()){
                        BmobGeoPoint b = (BmobGeoPoint) i.next();
                        Double la = b.getLatitude();
                        Double lo = b.getLongitude();
                        LatLng l = new LatLng(la , lo);
                        ll.add(l);
                    }
                    log("draw");
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

                }
            });
        }

        log("draw");
        BitmapDescriptor custom1 = BitmapDescriptorFactory
                .fromResource(R.drawable.low_poly_texture);

        if(list.size() > 2){
            OverlayOptions polygonOption = new PolylineOptions()
                    .points(list)
                    .width(15)
                    .color(0xAAFF0000)
                    .customTexture(custom1);
            mBaiduMap.addOverlay(polygonOption);
        }

    }
}
