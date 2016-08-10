package com.example.lenovo.murphysl;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.share.LocationShareURLOption;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.PoiDetailShareURLOption;
import com.baidu.mapapi.search.share.RouteShareURLOption;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.lenovo.murphysl.Map.MyOrientationListener;
import com.example.lenovo.murphysl.Map.RouteLineAdapter;
import com.example.lenovo.murphysl.UI.ArcMenu;
import com.example.lenovo.murphysl.com.baidu.mapapi.overlayutil.BikingRouteOverlay;
import com.example.lenovo.murphysl.com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.example.lenovo.murphysl.com.baidu.mapapi.overlayutil.OverlayManager;
import com.example.lenovo.murphysl.com.baidu.mapapi.overlayutil.PoiOverlay;
import com.example.lenovo.murphysl.com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.example.lenovo.murphysl.com.baidu.mapapi.overlayutil.WalkingRouteOverlay;

import java.util.ArrayList;
import java.util.List;

/**
 * MapActivity
 *
 * @author: lenovo
 * @time: 2016/8/4 18:01
 */

public class MapActivity extends AppCompatActivity{
    private static final String TAG = "Map";
    private Context context;
    private ArcMenu mArcMenu; //卫星菜单

    /**
     * 地图相关
     */
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;

    /**
     * 定位
     */
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private MyOrientationListener myOrientationListener;
    private LatLng latLng;//所在经纬
    private String city;//所在城市
    private int cityCode = 131;//所在城市代码
    private float mCurrentX;//旋转方向
    private BitmapDescriptor mIconLocation;//自定义图标
    private boolean isFirstIn = true;

    /**
     * 雷达
     */
    private RadarSearchManager manager;
    private MyRadarSearchListener myRadarSearchListener;
    private MyRadarUploadInfoCallback myRadarUploadInfoCallback;
    private int pageIndex = 0;
    private int curPage = 0;
    private int totalPage = 0;
    private String userID = "1";
    private String userComment = "13";//用户备注信息
    private View radar_view;
    private Button mapPreBtn;
    private Button mapNextBtn;
    private TextView popupText = null; // 泡泡view
    //private BitmapDescriptor ff3 = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
    private boolean flag = true;

    /**
     * 周边搜索
     */
    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private List<String> suggest;
    private String searchKey = "餐馆";
    private View search_view;
    private EditText editCity = null;
    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int loadIndex = 0;
    private boolean search_visible_flag = true;
    private int searchType = 0;  // 搜索的类型，在显示时区分


    /**
     * 地理范围构造器
     */
    LatLng center = new LatLng(39.92235, 116.380338);
    int radius = 500;
    LatLng southwest = new LatLng( 39.92235, 116.380338 );
    LatLng northeast = new LatLng( 39.947246, 116.414977);
    LatLngBounds searchbound = new LatLngBounds.Builder()
            .include(southwest)
            .include(northeast)
            .build();

    /**
     * 路线规划
     */
    private RoutePlanSearch mSearch = null;// 搜索模块，也可去掉地图模块独立使用
    private TransitRouteResult nowResult = null;
    private DrivingRouteResult nowResultd  = null;
    private RouteLine route = null;
    private OverlayManager routeOverlay = null;
    private View routeplan_view;
    private Button mBtnPre = null; // 上一个节点
    private Button mBtnNext = null; // 下一个节点
    private int nodeIndex = -1; // 节点索引,供浏览节点时使用
    private boolean useDefaultIcon = false;
    private boolean routeplan_visible_flag = true;

    /**
     * 地址分享
     */
    private ShareUrlSearch mShareUrlSearch = null;
    private GeoCoder mGeoCoder = null;
    private String currentAddr = null;// 保存搜索结果地址
    //private LatLng mPoint = new LatLng(40.056878, 116.308141);
    private Marker mAddrMarker = null;
    private RadioGroup mRouteMode;
    private RouteShareURLOption.RouteShareMode mRouteShareMode;
    private PlanNode startNode;
    private PlanNode enPlanNode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initView();
        initLocation();
        initRadar();
        initPoi();
        initRoute();
        initShare();
        initEvent();
    }

    private void initShare() {
        MyShareUrlResultListener myShareUrlResultListener = new MyShareUrlResultListener();
        MyGetGeoCoderResultListener myGetGeoCoderResultListener = new MyGetGeoCoderResultListener();

        mShareUrlSearch = ShareUrlSearch.newInstance();
        mShareUrlSearch.setOnGetShareUrlResultListener(myShareUrlResultListener);
        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(myGetGeoCoderResultListener);
        mRouteShareMode = RouteShareURLOption.RouteShareMode.FOOT_ROUTE_SHARE_MODE;
    }

    /**
     * 初始化路径规划
     */
    private void initRoute() {
        mSearch = RoutePlanSearch.newInstance();// 初始化搜索模块，注册事件监听
    }

    /**
     * 初始化Poi
     */
    private void initPoi() {
        MyPoiSearchResultListener myPoiSearchResultListener = new MyPoiSearchResultListener();
        MySuggestionResultListener mySuggestionResultListener = new MySuggestionResultListener();
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(myPoiSearchResultListener);

        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(mySuggestionResultListener);

        sugAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        keyWorldsView.setAdapter(sugAdapter);
        keyWorldsView.setThreshold(1);

        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        keyWorldsView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }

                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city(editCity.getText().toString()));
            }
        });
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//是否使用GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式 高精度(网络 gps） 低功耗(网络) 仅设备(GPS）
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);//是否需要地址信息
        option.setNeedDeviceDirect(true);//设备方向 真日了狗了
        option.setScanSpan(1000);//发送定位请求
        option.setIsNeedLocationPoiList(true);//附近地标

        mLocationClient.setLocOption(option);

        mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);//初始化自定义图标

        myOrientationListener = new MyOrientationListener(context);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
                Log.i(TAG , mCurrentX + "");
            }
        });

    }

    /**
     * 初始化雷达
     */
    private void initRadar(){
        manager = RadarSearchManager.getInstance();
        myRadarSearchListener = new MyRadarSearchListener();
        myRadarUploadInfoCallback = new MyRadarUploadInfoCallback();
        manager.addNearbyInfoListener(myRadarSearchListener);
        manager.setUserID(userID);//设置用户,id为空默认为设备标识
    }

    /**
     * 初始化视图
     */
    private void initView(){
        this.context = this;

        mMapView = (MapView) findViewById(R.id.bmapView);
        mArcMenu = (ArcMenu) findViewById(R.id.id_menu);

        //初始化搜索
        search_view  = findViewById(R.id.poisearch);
        editCity = (EditText) findViewById(R.id.city);
        keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchkey);

        // 初始化路径规划
        routeplan_view = findViewById(R.id.routeplan);
        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);

        //雷达周边
        radar_view = findViewById(R.id.radarmap);
        mapPreBtn = (Button)findViewById(R.id.radarmappre);
        mapNextBtn = (Button) findViewById(R.id.radarmapnext);

        mBaiduMap = mMapView.getMap();
        //设置初始视距
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
    }


    /**
     * 初始化监听事件
     * 1、定位
     * 2、周边雷达
     * 3、路径规划
     * 4、搜索
     */
    private void initEvent(){
        mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
                if (walkingRouteResult == null || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                if (walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    nodeIndex = -1;
                    mBtnPre.setVisibility(View.VISIBLE);
                    mBtnNext.setVisibility(View.VISIBLE);
                    route = walkingRouteResult.getRouteLines().get(0);
                    WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    routeOverlay = overlay;
                    overlay.setData(walkingRouteResult.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }
            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
                if (transitRouteResult == null || transitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                if (transitRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (transitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    nodeIndex = -1;
                    mBtnPre.setVisibility(View.VISIBLE);
                    mBtnNext.setVisibility(View.VISIBLE);


                    if (transitRouteResult.getRouteLines().size() > 1 ) {
                        nowResult = transitRouteResult;

                        MyTransitDlg myTransitDlg = new MyTransitDlg(context,
                                transitRouteResult.getRouteLines(),
                                RouteLineAdapter.Type.TRANSIT_ROUTE);
                        myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                            public void onItemClick(int position) {
                                route = nowResult.getRouteLines().get(position);
                                TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
                                mBaiduMap.setOnMarkerClickListener(overlay);
                                routeOverlay = overlay;
                                overlay.setData(nowResult.getRouteLines().get(position));
                                overlay.addToMap();
                                overlay.zoomToSpan();
                            }

                        });
                        myTransitDlg.show();

                    } else if ( transitRouteResult.getRouteLines().size() == 1 ) {
                        // 直接显示
                        route = transitRouteResult.getRouteLines().get(0);
                        TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
                        mBaiduMap.setOnMarkerClickListener(overlay);
                        routeOverlay = overlay;
                        overlay.setData(transitRouteResult.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();

                    } else {
                        Log.d("transitresult", "结果数<0" );
                        return;
                    }


                }
            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    nodeIndex = -1;


                    if (drivingRouteResult.getRouteLines().size() > 1 ) {
                        nowResultd = drivingRouteResult;

                        MyTransitDlg myTransitDlg = new MyTransitDlg(context,
                                drivingRouteResult.getRouteLines(),
                                RouteLineAdapter.Type.DRIVING_ROUTE);
                        myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                            public void onItemClick(int position) {
                                route = nowResultd.getRouteLines().get(position);
                                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                                mBaiduMap.setOnMarkerClickListener(overlay);
                                routeOverlay = overlay;
                                overlay.setData(nowResultd.getRouteLines().get(position));
                                overlay.addToMap();
                                overlay.zoomToSpan();
                            }

                        });
                        myTransitDlg.show();

                    } else if ( drivingRouteResult.getRouteLines().size() == 1 ) {
                        route = drivingRouteResult.getRouteLines().get(0);
                        DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                        routeOverlay = overlay;
                        mBaiduMap.setOnMarkerClickListener(overlay);
                        overlay.setData(drivingRouteResult.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                        mBtnPre.setVisibility(View.VISIBLE);
                        mBtnNext.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
                if (bikingRouteResult == null || bikingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                if (bikingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (bikingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    nodeIndex = -1;
                    mBtnPre.setVisibility(View.VISIBLE);
                    mBtnNext.setVisibility(View.VISIBLE);
                    route = bikingRouteResult.getRouteLines().get(0);
                    BikingRouteOverlay overlay = new MyBikingRouteOverlay(mBaiduMap);
                    routeOverlay = overlay;
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(bikingRouteResult.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }
            }
        });

        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (mArcMenu.isOpen()){
                    mArcMenu.toggleMenu(600);
                }
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        mArcMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                switch (pos){
                    case 1:
                        myLoction(latLng);
                        break;
                    case 2:
                        if(flag){
                            uploadContinueClick();
                            searchNearby();
                            flag = false;
                        }else{
                            stopUploadClick();
                            flag = true;
                        }
                        break;
                    case 3:
                        if(routeplan_visible_flag){
                            routeplan_view.setVisibility(View.VISIBLE);
                            search_view.setVisibility(View.GONE);
                            routeplan_visible_flag = false;
                        }else{
                            routeplan_view.setVisibility(View.GONE);
                            routeplan_visible_flag = true;
                        }
                        break;
                    case 4:
                        if(search_visible_flag){
                            search_view.setVisibility(View.VISIBLE);
                            routeplan_view.setVisibility(View.GONE);
                            search_visible_flag = false;
                        }else{
                            search_view.setVisibility(View.GONE);
                            search_visible_flag = true;
                        }
                        break;
                    case 5:

                }
                Toast.makeText(context, pos+":"+view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开始定位
        mBaiduMap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }
        //开启方向传感器
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
        //停止定位
        mBaiduMap.setMyLocationEnabled(false);
        if (mLocationClient.isStarted()){
            mLocationClient.stop();
        }
        manager.removeNearbyInfoListener(myRadarSearchListener);
        manager.clearUserInfo();

        //关闭方向传感器
        myOrientationListener.stop();
        mLocationClient.unRegisterLocationListener(mLocationListener);
    }
    @Override
    protected void onDestroy() {
        mSearch.destroy();
        manager.destroy();
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        mShareUrlSearch.destroy();
        mMapView.onDestroy();
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

    /**
     * 当前位置
     */
    private void myLoction(LatLng latLng) {
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);//设置动画
    }















    /**
     * 开始自动上传
     */
    public void uploadContinueClick() {
        if (latLng == null) {
            Toast.makeText(MapActivity.this, "未获取到位置", Toast.LENGTH_LONG).show();
            return;
        }
        manager.startUploadAuto(myRadarUploadInfoCallback, 5000);
    }

    /**
     * 停止自动上传
     */
    public void stopUploadClick() {
        manager.stopUploadAuto();
    }

    /**
     * 清除自己当前的信息
     */
    public void clearInfoClick() {
        manager.clearUserInfo();
    }

    /**
     * 查找周边的人
     */
    public void searchNearby() {
        if (latLng == null) {
            Toast.makeText(MapActivity.this, "未获取到位置", Toast.LENGTH_LONG).show();
            return;
        }
        RadarNearbySearchOption option = new RadarNearbySearchOption()
                .centerPt(latLng)//搜索中心点
                .pageNum(pageIndex)
                .radius(2000);//搜索半径

        manager.nearbyInfoRequest(option);//发起查询请求
    }



    /*private void searchRequest(int index) {
        curPage = 0;
        totalPage = 0;

        RadarNearbySearchOption option = new RadarNearbySearchOption()
                .centerPt(pt).pageNum(pageIndex).radius(2000).pageCapacity(11);
        RadarSearchManager.getInstance().nearbyInfoRequest(option);

        listPreBtn.setVisibility(View.INVISIBLE);
        listNextBtn.setVisibility(View.INVISIBLE);
        mapPreBtn.setVisibility(View.INVISIBLE);
        mapNextBtn.setVisibility(View.INVISIBLE);
        listCurPage.setText("0/0");
        mapCurPage.setText("0/0");
        mBaiduMap.hideInfoWindow();
    }*/

    /**
     * 清除查找结果
     *
     * @param v
     */
   /* public void clearResult(View v) {
        parseResultToList(null);
        parseResultToMap(null);
        clearRstBtn.setEnabled(false);
        listPreBtn.setVisibility(View.INVISIBLE);
        listNextBtn.setVisibility(View.INVISIBLE);
        mapPreBtn.setVisibility(View.INVISIBLE);
        mapNextBtn.setVisibility(View.INVISIBLE);
        listCurPage.setVisibility(View.INVISIBLE);
        mapCurPage.setVisibility(View.INVISIBLE);
        mBaiduMap.hideInfoWindow();
    }*/


    /**
     * 更新结果地图
     *
     * @param res
     */
   /* public void parseResultToMap(RadarNearbyResult res) {
        mBaiduMap.clear();
        if (res != null && res.infoList != null && res.infoList.size() > 0) {
            for (int i = 0; i < res.infoList.size(); i++) {
                MarkerOptions option = new MarkerOptions().icon(ff3).position(res.infoList.get(i).pt);
                Bundle des = new Bundle();
                if (res.infoList.get(i).comments == null || res.infoList.get(i).comments.equals("")) {
                    des.putString("des", "没有备注");
                } else {
                    des.putString("des", res.infoList.get(i).comments);
                }

                option.extraInfo(des);
                mBaiduMap.addOverlay(option);
            }
        }
        if (curPage > 0) {
            mapPreBtn.setVisibility(View.VISIBLE);
        }
        if (totalPage - 1 > curPage) {
            mapNextBtn.setVisibility(View.VISIBLE);
        }

    }*/

















    /**
     * 响应城市内搜索按钮点击事件
     *
     * @param v
     */
    public void searchPoiButtonProcess(View v) {
        searchType = 1;
        String cityTemp = editCity.getText().toString();
        searchKey = keyWorldsView.getText().toString();
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(cityTemp)
                .keyword(searchKey)
                .pageNum(loadIndex));
    }

    /**
     * 响应周边搜索按钮点击事件
     *
     * @param v
     */
    public void  searchNearbyProcess(View v) {
        searchType = 2;
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                .keyword(keyWorldsView.getText()
                .toString())
                .sortType(PoiSortType.distance_from_near_to_far)
                .location(center)
                .radius(radius)
                .pageNum(loadIndex);
        mPoiSearch.searchNearby(nearbySearchOption);
    }

    /**
     * 响应区域搜索按钮点击事件
     *
     * @param v
     */
    public void searchBoundProcess(View v) {
        searchType = 3;

        mPoiSearch.searchInBound(new PoiBoundSearchOption()
                .bound(searchbound)
                .keyword(keyWorldsView.getText().toString()));
    }

    public void goToNextPage(View v) {
        loadIndex++;
        searchPoiButtonProcess(null);
    }







    /**
     * 对周边检索的范围进行绘制
     * @param center
     * @param radius
     */
    public void showNearbyArea( LatLng center, int radius) {
        BitmapDescriptor centerBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
        MarkerOptions ooMarker = new MarkerOptions().position(center).icon(centerBitmap);
        mBaiduMap.addOverlay(ooMarker);

        OverlayOptions ooCircle = new CircleOptions()
                .fillColor( 0xCCCCCC00 )
                .center(center)
                .stroke(new Stroke(5, 0xFFFF00FF ))
                .radius(radius);
        mBaiduMap.addOverlay(ooCircle);
    }

    /**
     * 对区域检索的范围进行绘制
     * @param bounds
     */
    public void showBound( LatLngBounds bounds) {
        BitmapDescriptor bdGround = BitmapDescriptorFactory.fromResource(R.drawable.ground_overlay);

        /**
         * 绘制覆盖物
         */
        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds)
                .image(bdGround)
                .transparency(0.8f);
        mBaiduMap.addOverlay(ooGround);

        /**
         * 设置地图新中心点
         */
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(bounds.getCenter());
        mBaiduMap.setMapStatus(u);

        bdGround.recycle();
    }



    /**
     * 发起路线规划搜索
     * 起终点
     *
     * @param v
     */
    public void searchButtonProcess(View v) {
        // 重置浏览节点的路线数据 清空地图所有的 Overlay 覆盖物以及 InfoWindow
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaiduMap.clear();

        // 处理搜索按钮响应
        EditText editSt = (EditText) findViewById(R.id.start);
        EditText editEn = (EditText) findViewById(R.id.end);
        // 设置起终点信息，对于tranist search 来说，城市名无意义
        //startNode = PlanNode.withCityNameAndPlaceName(city, editSt.getText().toString());
        //enPlanNode = PlanNode.withCityNameAndPlaceName(city, editEn.getText().toString());
        startNode = PlanNode.withCityCodeAndPlaceName(cityCode , editSt.getText().toString());
        enPlanNode = PlanNode.withCityCodeAndPlaceName(cityCode, editEn.getText().toString());

        // 实际使用中请对起点终点城市进行正确的设定
        if (v.getId() == R.id.drive) {
            mSearch.drivingSearch((new DrivingRoutePlanOption())
                    .from(startNode).to(enPlanNode));
        } else if (v.getId() == R.id.transit) {
            mSearch.transitSearch((new TransitRoutePlanOption())
                    .from(startNode).city(city).to(enPlanNode));
        } else if (v.getId() == R.id.walk) {
            mSearch.walkingSearch((new WalkingRoutePlanOption())
                    .from(startNode).to(enPlanNode));
        } else if (v.getId() == R.id.bike) {
            mSearch.bikingSearch((new BikingRoutePlanOption())
                    .from(startNode).to(enPlanNode));
        }
    }

    /**
     * 节点浏览
     *
     * @param v
     */
    public void nodeClick(View v) {
        if (route == null || route.getAllStep() == null) {
            return;
        }
        if (nodeIndex == -1 && v.getId() == R.id.pre) {
            return;
        }
        // 设置节点索引
        if (v.getId() == R.id.next) {
            if (nodeIndex < route.getAllStep().size() - 1) {
                nodeIndex++;
            } else {
                return;
            }
        } else if (v.getId() == R.id.pre) {
            if (nodeIndex > 0) {
                nodeIndex--;
            } else {
                return;
            }
        }
        // 获取节结果信息
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        if (step instanceof DrivingRouteLine.DrivingStep) {
            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
        } else if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
        } else if (step instanceof BikingRouteLine.BikingStep) {
            nodeLocation = ((BikingRouteLine.BikingStep) step).getEntrance().getLocation();
            nodeTitle = ((BikingRouteLine.BikingStep) step).getInstructions();
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        // 移动节点至中心
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        TextView popupText = new TextView(context);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));

    }


    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyBikingRouteOverlay extends BikingRouteOverlay {
        public  MyBikingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }


    }


    /**
     * 响应DLg中的List item 点击
     */
    interface OnItemInDlgClickListener {
        public void onItemClick(int position);
    }

    /**
     * 供路线选择的Dialog
     */
    class MyTransitDlg extends Dialog {

        private List<? extends RouteLine> mtransitRouteLines;
        private ListView transitRouteList;
        private RouteLineAdapter mTransitAdapter;

        OnItemInDlgClickListener onItemInDlgClickListener;

        public MyTransitDlg(Context context, int theme) {
            super(context, theme);
        }

        public MyTransitDlg(Context context, List<? extends RouteLine> transitRouteLines, RouteLineAdapter.Type
                type) {
            this(context, 0);
            mtransitRouteLines = transitRouteLines;
            mTransitAdapter = new RouteLineAdapter(context, mtransitRouteLines, type);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transit_dialog);

            transitRouteList = (ListView) findViewById(R.id.transitList);
            transitRouteList.setAdapter(mTransitAdapter);

            transitRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onItemInDlgClickListener.onItemClick(position);
                    mBtnPre.setVisibility(View.VISIBLE);
                    mBtnNext.setVisibility(View.VISIBLE);
                    dismiss();

                }
            });
        }

        public void setOnItemInDlgClickLinster(OnItemInDlgClickListener itemListener) {
            onItemInDlgClickListener = itemListener;
        }
    }

    /**
     * 发起poi搜索
     * @param view
     */
    public void sharePoi(View view) {
        mPoiSearch.searchInCity((new PoiCitySearchOption()).city(city).keyword(searchKey));
        Toast.makeText(this, "在" + city + "搜索 " + searchKey, Toast.LENGTH_SHORT).show();
    }

    /**
     * 发起反地理编码请求
     * @param //view
     */
    /*public void shareAddr(View view) {
        mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(mPoint));
        Toast.makeText(this, String.format("搜索位置： %f，%f", mPoint.latitude, mPoint.longitude), Toast.LENGTH_SHORT).show();
    }*/

    /*public void setRouteMode(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.foot:
                if (checked) {
                    mRouteShareMode = RouteShareURLOption.RouteShareMode.FOOT_ROUTE_SHARE_MODE;
                }
                break;
            case R.id.cycle:
                if (checked) {
                    mRouteShareMode = RouteShareURLOption.RouteShareMode.CYCLE_ROUTE_SHARE_MODE;
                }
                break;
            case R.id.car:
                if (checked) {
                    mRouteShareMode = RouteShareURLOption.RouteShareMode.CAR_ROUTE_SHARE_MODE;
                }
                break;
            case R.id.bus:
                if (checked) {
                    mRouteShareMode = RouteShareURLOption.RouteShareMode.BUS_ROUTE_SHARE_MODE;
                }
                break;
            default:
                break;
        }
    }
*/
    /*public void shareRoute(View view) {
        //startNode  = PlanNode.withLocation(startNode.getLocation());
        //enPlanNode = PlanNode.withLocation(enPlanNode.getLocation());
        mShareUrlSearch.requestRouteShareUrl(new RouteShareURLOption()
                .from(startNode).to(enPlanNode).routMode(mRouteShareMode));
    }*/
    //分享
    public void shareRoute(View v){
        mShareUrlSearch.requestRouteShareUrl(new RouteShareURLOption().from(startNode).to(enPlanNode).routMode(mRouteShareMode));
    }

    /**
     * 使用PoiOverlay 展示poi点，在poi被点击时发起短串请求.
     */
    private class PoiShareOverlay extends PoiOverlay {

        public PoiShareOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int i) {
            PoiInfo info = getPoiResult().getAllPoi().get(i);
            currentAddr = info.address;
            mShareUrlSearch
                    .requestPoiDetailShareUrl(new PoiDetailShareURLOption()
                            .poiUid(info.uid));
            return true;
        }
    }
    /**
     * 使用PoiOverlay 展示poi点，在poi被点击时显示poi详情.
     */
    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            if (poi.hasCaterDetails) {
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
            }
            return true;
        }
    }


    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(bdLocation.getTime());
            sb.append("\nerror code : ");
            sb.append(bdLocation.getLocType());
            sb.append("\nlatitude : ");
            sb.append(bdLocation.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(bdLocation.getLongitude());
            sb.append("\nradius : ");
            sb.append(bdLocation.getRadius());
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(bdLocation.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(bdLocation.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(bdLocation.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(bdLocation.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(bdLocation.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(bdLocation.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(bdLocation.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(bdLocation.getLocationDescribe());// 位置语义化信息
            List<Poi> list = bdLocation.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i(TAG , sb.toString());

            city = bdLocation.getCity();
            cityCode = Integer.parseInt(String.valueOf(cityCode));

            //设置中心点
            MyLocationData data = new MyLocationData.Builder()
                    .direction(mCurrentX)//定位数据方向
                    .accuracy(bdLocation.getRadius())//定位数据精度信息
                    .latitude(bdLocation.getLatitude())//定位数据的纬度
                    .longitude(bdLocation.getLongitude())//定位数据的经度
                    .build();//生成定位数据对象
            mBaiduMap.setMyLocationData(data);

            //设置图标
            MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL , true ,mIconLocation );
            mBaiduMap.setMyLocationConfigeration(config);

            //更新经纬度
            latLng = new LatLng(bdLocation.getLatitude() , bdLocation.getLongitude());
            if(isFirstIn){
                myLoction(latLng);
                isFirstIn = false;
            }

        }
    }

    private class MyRadarSearchListener implements RadarSearchListener{

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

                Log.i(TAG , "查询周边成功" + latLng1.toString());
            } else {
                // 获取失败
                curPage = 0;
                totalPage = 0;
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

    private class MyRadarUploadInfoCallback implements RadarUploadInfoCallback {

        @Override
        public RadarUploadInfo onUploadInfoCallback() {
            if (latLng == null) {
                Toast.makeText(MapActivity.this, "未获取到位置", Toast.LENGTH_LONG).show();
                return null;
            }
            RadarUploadInfo info = new RadarUploadInfo();
            info.comments = userComment;
            info.pt = latLng;
            manager.uploadInfoRequest(info);
            Log.i(TAG , "已上传位置");
            return info;
        }
    }

    private class MyPoiSearchResultListener implements OnGetPoiSearchResultListener {

        /**
         * 获取POI搜索结果，包括searchInCity，searchNearby，searchInBound返回的搜索结果
         * @param result
         */
        public void onGetPoiResult(PoiResult result) {
            if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                Toast.makeText(context, "未找到结果", Toast.LENGTH_LONG).show();
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                mBaiduMap.clear();
                /*PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result);
                overlay.addToMap();
                overlay.zoomToSpan();*/

                PoiShareOverlay poiOverlay = new PoiShareOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(poiOverlay);
                poiOverlay.setData(result);
                poiOverlay.addToMap();
                poiOverlay.zoomToSpan();

                switch( searchType ) {
                    case 2:
                        myLoction(center);
                        showNearbyArea(center, radius);
                        break;
                    case 3:
                        myLoction(center);
                        showBound(searchbound);
                        break;
                    default:
                        break;
                }

                return;
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

                // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
                String strInfo = "在";
                for (CityInfo cityInfo : result.getSuggestCityList()) {
                    strInfo += cityInfo.city;
                    strInfo += ",";
                }
                strInfo += "找到结果";
                Toast.makeText(context, strInfo, Toast.LENGTH_LONG)
                        .show();
            }
        }

        /**
         * 获取POI详情搜索结果，得到searchPoiDetail返回的搜索结果
         * @param result
         */
        public void onGetPoiDetailResult(PoiDetailResult result) {
            if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT).show();
            }
        }

        //@Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    }

    private class MySuggestionResultListener implements OnGetSuggestionResultListener {

        /**
         * 获取在线建议搜索结果，得到requestSuggestion返回的搜索结果
         * @param res
         */
        @Override
        public void onGetSuggestionResult(SuggestionResult res) {
            if (res == null || res.getAllSuggestions() == null) {
                return;
            }
            suggest = new ArrayList<String>();
            for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
                if (info.key != null) {
                    suggest.add(info.key);
                }
            }
            sugAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, suggest);
            keyWorldsView.setAdapter(sugAdapter);
            sugAdapter.notifyDataSetChanged();
        }
    }

    private class MyShareUrlResultListener implements OnGetShareUrlResultListener{

        @Override
        public void onGetPoiDetailShareUrlResult(ShareUrlResult shareUrlResult) {
            // 分享短串结果
            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_TEXT, "您的朋友通过百度地图SDK与您分享一个POI点详情: " + currentAddr
                    + " -- " + shareUrlResult.getUrl());
            it.setType("text/plain");
            startActivity(Intent.createChooser(it, "将短串分享到"));
        }

        @Override
        public void onGetLocationShareUrlResult(ShareUrlResult shareUrlResult) {
            // 分享短串结果
            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_TEXT, "您的朋友通过百度地图SDK与您分享一个位置: " + currentAddr
                    + " -- " + shareUrlResult.getUrl());
            it.setType("text/plain");
            startActivity(Intent.createChooser(it, "将短串分享到"));

        }

        @Override
        public void onGetRouteShareUrlResult(ShareUrlResult shareUrlResult) {
            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_TEXT, "您的朋友通过百度地图SDK与您分享一条路线，URL "
                    + " -- " + shareUrlResult.getUrl());
            it.setType("text/plain");
            startActivity(Intent.createChooser(it, "将短串分享到"));

        }
    }

    private class MyGetGeoCoderResultListener implements OnGetGeoCoderResultListener{

        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
                return;
            }
            mBaiduMap.clear();
            mBaiduMap.setOnMarkerClickListener(new MyMarkerClickListener());
            mAddrMarker = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_marka))
                    .title(reverseGeoCodeResult.getAddress()).position(reverseGeoCodeResult.getLocation()));

        }
    }

    private class MyMarkerClickListener implements BaiduMap.OnMarkerClickListener{
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (marker == mAddrMarker) {
                mShareUrlSearch.requestLocationShareUrl(new LocationShareURLOption()
                        .location(marker.getPosition())
                        .snippet("测试分享点")
                        .name(marker.getTitle()));
            }
            return true;
        }
    }

    private class MyRadarMarkerClickListener implements BaiduMap.OnMarkerClickListener{
        @Override
        public boolean onMarkerClick(Marker marker) {
            mBaiduMap.hideInfoWindow();
            if (marker != null) {
                popupText = new TextView(context);
                popupText.setBackgroundResource(R.drawable.popup);
                popupText.setTextColor(0xFF000000);
                popupText.setText(marker.getExtraInfo().getString("des"));
                mBaiduMap.showInfoWindow(new InfoWindow(popupText, marker.getPosition(), -47));
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(marker.getPosition());
                mBaiduMap.setMapStatus(update);
                return true;
            } else {
                return false;
            }
        }
    }



    /**
     * 切换路线图标，刷新地图使其生效
     * 注意： 起终点图标使用中心对齐.
     */
    /*public void changeRouteIcon(View v) {
        if (routeOverlay == null) {
            return;
        }
        if (useDefaultIcon) {
            ((Button) v).setText("自定义起终点图标");
            Toast.makeText(this, "将使用系统起终点图标", Toast.LENGTH_SHORT).show();

        } else {
            ((Button) v).setText("系统起终点图标");
            Toast.makeText(this, "将使用自定义起终点图标", Toast.LENGTH_SHORT).show();

        }
        useDefaultIcon = !useDefaultIcon;
        routeOverlay.removeFromMap();
        routeOverlay.addToMap();
    }*/


}
