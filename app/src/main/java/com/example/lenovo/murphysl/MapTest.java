package com.example.lenovo.murphysl;

/**
 * MapTest
 *
 * @author: lenovo
 * @time: 2016/8/4 19:20
 */

/*public class MapTest{

    public static final String TAG = "Map";
    private Context context;
    private BaiduMap mBaiduMap;

    private Location location;

    private MapView mMapView = null;
    private boolean isFirstIn = true;

    private String location_msg;
    private MyLocationData location_data;
    private LatLng location_latLng;

    private BitmapDescriptor mIconLocation;//自定义图标

    private Radar radar;
    private MyRadarSearchListener myRadarSearchListener;
    private boolean flag = false;
    private boolean flag1 = false;

    private ArcMenu mArcMenu;

    public BitmapDescriptor getmIconLocation() {
        return mIconLocation;
    }

    public BaiduMap getmBaiduMap() {
        return mBaiduMap;
    }

    *//* private Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(isFirstIn){
                myLoction();
                isFirstIn = false;
            }
            handler.postDelayed(runnable , 1000);
        }
    };*//*


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_map);
        this.context = this;

        initView();
        initLocation();
        initEvent();
    }

    *//**
     * 初始化视图
     *//*
    private void initView(){
        mMapView = (MapView) findViewById(R.id.bmapView);
        mArcMenu = (ArcMenu) findViewById(R.id.id_menu);

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);//开启定位图层
        //设置初始视距
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);

        //初始化方向图标
        mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
    }

    *//**
     * 初始化定位
     *//*
    private void initLocation(){

        location = ((LocationApplication)getApplication()).location;
        *//*int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            location.setLocationOption(location.getDefaultLocationClientOption());
        } else if (type == 1) {
            location.setLocationOption(location.getOption());
        }*//*
        if(location.setLocationOption(location.getDefaultLocationClientOption())){
            location.start();
        }else{
            Log.i(TAG , "设置出错");
        }
    }

    *//**
     * 初始化监听事件
     *//*
    private void initEvent(){
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (mArcMenu.isOpen()){
                    mArcMenu.toggleMenu(600);
                }
            }
        });

        mArcMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                switch (pos){
                    case 1:
                        myLoction();
                        break;
                    case 2://定位
                        if(flag1){
                            location.stop();
                            flag = false;
                        }else{
                            location.start();
                            flag = true;
                        }
                        break;
                  *//*  case 3:
                        if(flag){
                            radar.stopUploadClick();
                            flag = false;
                        }else{
                            openRadar();
                            radar.uploadContinueClick();
                            radar.searchNearby();
                            flag = true;
                        }
                        break;*//*
                }
                Toast.makeText(MapActivity.this, pos+":"+view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    *//**
     * 初始化雷达
     *//*
    private void openRadar(){
        radar = new Radar(context , location.getLocation_latLng());
        myRadarSearchListener = new MyRadarSearchListener();
        radar.registerRadarListener(myRadarSearchListener);
    }

    @Override
    public void onStart() {
        super.onStart();

        location_msg = location.getLocation_msg();
        location_data = location.getLocation_data();
        location_latLng = location.getLocation_latLng();

        //设置图标
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL , true ,mIconLocation );
        mBaiduMap.setMyLocationConfigeration(config);

        if(isFirstIn){
            myLoction();
            isFirstIn = false;
        }

        //handler.postDelayed(runnable , 1000);

    }
    @Override
    public void onResume() {
        super.onResume();

        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();

        mMapView.onPause();
    }
    @Override
    public void onStop() {
        mBaiduMap.setMyLocationEnabled(false);//关闭定位图层

        //radar.unregisterRadarListener(myRadarSearchListener);

        //关闭方向传感器
        location.stop();
        super.onStop();
    }
    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        //radar.destroyRadar();
        super.onDestroy();
    }

    private class MyRadarSearchListener implements RadarSearchListener {

        @Override
        public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError radarSearchError) {
            if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
                Toast.makeText(MapActivity.this, "查询周边成功", Toast.LENGTH_LONG).show();
                List<RadarNearbyInfo> list = radarNearbyResult.infoList;
                LatLng latLng1 = list.get(0).pt;
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.maker);
                OverlayOptions option = new MarkerOptions()
                        .position(latLng1)
                        .icon(bitmap);
                mBaiduMap.addOverlay(option);
                String s = latLng1.toString();
                Log.i(TAG , "查询周边成功" + s);
            } else {
                Toast.makeText(MapActivity.this, "查询周边失败" , Toast.LENGTH_LONG).show();
                Log.i(TAG , "查询周边失败" + radarSearchError.toString());
            }
        }

        @Override
        public void onGetUploadState(RadarSearchError radarSearchError) {

        }

        @Override
        public void onGetClearInfoState(RadarSearchError radarSearchError) {
            if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
                //清除成功
                Toast.makeText(MapActivity.this, "清除位置成功", Toast.LENGTH_LONG)
                        .show();
            } else {
                //清除失败
                Toast.makeText(MapActivity.this, "清除位置失败", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    *//**
     * 当前位置
     *//*
    private void myLoction() {
        mBaiduMap.setMyLocationData(location_data);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(location_latLng);
        mBaiduMap.animateMapStatus(msu);//设置动画
    }
}*/

