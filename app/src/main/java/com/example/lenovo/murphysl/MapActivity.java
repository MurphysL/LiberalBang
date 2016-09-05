package com.example.lenovo.murphysl;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
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
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
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
import com.baidu.mapapi.utils.AreaUtil;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.Friend;
import com.example.lenovo.murphysl.bean.MyDate;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.bean.VoiceBean;
import com.example.lenovo.murphysl.com.baidu.mapapi.overlayutil.BikingRouteOverlay;
import com.example.lenovo.murphysl.com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.example.lenovo.murphysl.com.baidu.mapapi.overlayutil.OverlayManager;
import com.example.lenovo.murphysl.com.baidu.mapapi.overlayutil.PoiOverlay;
import com.example.lenovo.murphysl.com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.example.lenovo.murphysl.com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.example.lenovo.murphysl.event.LocationEvent;
import com.example.lenovo.murphysl.event.UserEvent;
import com.example.lenovo.murphysl.map.Location;
import com.example.lenovo.murphysl.map.MyOrientationListener;
import com.example.lenovo.murphysl.map.RouteLineAdapter;
import com.example.lenovo.murphysl.model.UserModel;
import com.example.lenovo.murphysl.model.i.QueryUserListener;
import com.example.lenovo.murphysl.ui.ArcMenu;
import com.example.lenovo.murphysl.util.LocConfig;
import com.example.lenovo.murphysl.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * MapActivity
 *
 * 问题：MapActivity关闭过快时，location关闭不及时，导致重启MapActivity定位失败
 *
 * @author: lenovo
 * @time: 2016/8/4 18:01
 */

public class MapActivity extends ParentWithNaviActivity {

    @Bind(R.id.bmapView)
    MapView mMapView;
    @Bind(R.id.id_arc_menu)
    ArcMenu mArcMenu;
    @Bind(R.id.search_city)
    EditText etCity;
    @Bind(R.id.searchkey)
    AutoCompleteTextView searchkey;
    @Bind(R.id.poisearch)
    LinearLayout poisearch;
    @Bind(R.id.start)
    EditText start;
    @Bind(R.id.end)
    EditText end;
    @Bind(R.id.pre)
    Button pre;
    @Bind(R.id.next)
    Button next;
    @Bind(R.id.routeplan)
    LinearLayout routeplan;

    private BaiduMap mBaiduMap = null;

    /**
     * 用户信息
     */
    private String userID = "";//设置用户,id为空默认为设备标识
    private String userComment;//用户备注信息
    private LatLng pt;//所在经纬
    private String city;//所在城市
    private int cityCode;//所在城市代码
    private float orientationX;//旋转方向
    private String mHobby;//用户爱好
    private String mSort;//选择种类
    private List<Friend> friendList;//用户好友列表

    /**
     * 定位
     */
    private Location location;
    private MyOrientationListener myOrientationListener;
    private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果
    private boolean isFirstIn = true;
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


    /**
     * 雷达
     */
    private RadarSearchManager manager;
    private MyRadarSearchListener myRadarSearchListener;
    private MyRadarUploadInfoCallback myRadarUploadInfoCallback;
    private TextView popupText = null; // 泡泡view
    private boolean radar_flag = true;



    /**
     * 周边搜索
     */
    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private List<String> suggest;
    private String searchKey;
    private ArrayAdapter<String> sugAdapter = null;
    private int loadIndex = 0;
    private boolean search_visible_flag = true;


    /**
     * 路线规划
     */
    private RoutePlanSearch mSearch = null;// 搜索模块，也可去掉地图模块独立使用
    private TransitRouteResult nowResult = null;
    private DrivingRouteResult nowResultd = null;
    private RouteLine route = null;
    private OverlayManager routeOverlay = null;
    private int nodeIndex = -1; // 节点索引,供浏览节点时使用
    private boolean useDefaultIcon = false;
    private boolean routeplan_visible_flag = true;

    /**
     * 地址分享
     */
    private ShareUrlSearch mShareUrlSearch = null;
    private GeoCoder mGeoCoder = null;
    private String currentAddr = null;// 保存搜索结果地址
    private Marker mAddrMarker = null;
    private RouteShareURLOption.RouteShareMode mRouteShareMode;
    private PlanNode startNode;
    private PlanNode enPlanNode;


    @Override
    protected String title() {
        return "地图";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initNaviView();
        ButterKnife.bind(this);

        /*if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/

        initLocation();
        initRadar();
        initPoi();
        initRoute();
        initShare();
        initEvent();
        initUser();
        initSearch();
    }

    private void initSearch() {
        mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
                if (walkingRouteResult == null || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    toast("抱歉，未找到结果");
                }
                if (walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    nodeIndex = -1;
                    pre.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
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
                    toast("抱歉，未找到结果");
                }
                if (transitRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (transitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    nodeIndex = -1;
                    pre.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);


                    if (transitRouteResult.getRouteLines().size() > 1) {
                        nowResult = transitRouteResult;

                        MyTransitDlg myTransitDlg = new MyTransitDlg(MapActivity.this,
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

                    } else if (transitRouteResult.getRouteLines().size() == 1) {
                        // 直接显示
                        route = transitRouteResult.getRouteLines().get(0);
                        TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
                        mBaiduMap.setOnMarkerClickListener(overlay);
                        routeOverlay = overlay;
                        overlay.setData(transitRouteResult.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();

                    } else {
                        Log.d("transitresult", "结果数<0");
                        return;
                    }
                }
            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    toast("抱歉，未找到结果");
                }
                if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    nodeIndex = -1;


                    if (drivingRouteResult.getRouteLines().size() > 1) {
                        nowResultd = drivingRouteResult;

                        MyTransitDlg myTransitDlg = new MyTransitDlg(MapActivity.this,
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

                    } else if (drivingRouteResult.getRouteLines().size() == 1) {
                        route = drivingRouteResult.getRouteLines().get(0);
                        DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                        routeOverlay = overlay;
                        mBaiduMap.setOnMarkerClickListener(overlay);
                        overlay.setData(drivingRouteResult.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                        pre.setVisibility(View.VISIBLE);
                        next.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
                if (bikingRouteResult == null || bikingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    toast("抱歉，未找到结果");
                }
                if (bikingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (bikingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    nodeIndex = -1;
                    pre.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
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
    }

    private void initUser() {
        userComment = UserModel.getInstance().getUser().getObjectId();
        UserModel.getInstance().queryUserInfo(getCurrentUid(), new QueryUserListener() {
            @Override
            public void done(UserBean s, BmobException e) {
                if (e == null) {
                    mHobby = s.getHobby();
                    mSort = s.getSort();
                    log("hobby：" + mHobby + "\nsort：" + mSort);
                } else {
                    log("获取个人偏好错误");
                }
            }

        });


        UserModel.getInstance().queryFriends(new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                friendList = list;
            }

            @Override
            public void onError(int i, String s) {
               log("获取好友错误："+ s + "(" + i + ")");
            }
        });

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

    private void initRoute() {
        pre.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        mSearch = RoutePlanSearch.newInstance();// 初始化搜索模块，注册事件监听
    }

    private void initPoi() {
        mPoiSearch = PoiSearch.newInstance();
        mSuggestionSearch = SuggestionSearch.newInstance();

        MyPoiSearchResultListener myPoiSearchResultListener = new MyPoiSearchResultListener();
        MySuggestionResultListener mySuggestionResultListener = new MySuggestionResultListener();

        mPoiSearch.setOnGetPoiSearchResultListener(myPoiSearchResultListener);
        mSuggestionSearch.setOnGetSuggestionResultListener(mySuggestionResultListener);

        sugAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        searchkey.setAdapter(sugAdapter);
        searchkey.setThreshold(1);//联系词数

        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        searchkey.addTextChangedListener(new TextWatcher() {

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
                                .keyword(cs.toString()).city(etCity.getText().toString()));
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

    private void initRadar() {
        manager = RadarSearchManager.getInstance();
        myRadarSearchListener = new MyRadarSearchListener();
        myRadarUploadInfoCallback = new MyRadarUploadInfoCallback();
        manager.addNearbyInfoListener(myRadarSearchListener);
        manager.setUserID(userID);
    }

    private void initEvent() {

        Bundle bundle = getIntent().getBundleExtra("com.example.lenovo.murphysl");
        if(bundle != null){
            String c = bundle.getString("chat_location");
            if(c != null){
                if(c.equals("y")){
                    poisearch.setVisibility(View.VISIBLE);
                    search_visible_flag = false;
                    etCity.setText(city);
                }
            }
        }

        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (mArcMenu.isOpen()) {
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
                switch (pos) {
                    case 1:
                        myLoction(pt);
                        toast(view.getTag());
                        break;
                    case 2:
                        if (radar_flag) {
                            upload();
                            radar_flag = false;
                        } else {
                            stopUpload();
                            radar_flag = true;
                        }
                        toast(view.getTag());
                        break;
                    case 3:
                        mBaiduMap.clear();
                        if (search_visible_flag) {
                            etCity.setText(city);
                            poisearch.setVisibility(View.VISIBLE);
                            routeplan.setVisibility(View.GONE);
                            search_visible_flag = false;
                            routeplan_visible_flag = true;
                        } else {
                            poisearch.setVisibility(View.GONE);
                            search_visible_flag = true;
                        }
                        toast(view.getTag());
                        break;
                    case 4:
                        mBaiduMap.clear();
                        if (routeplan_visible_flag) {
                            routeplan.setVisibility(View.VISIBLE);
                            poisearch.setVisibility(View.GONE);
                            routeplan_visible_flag = false;
                            search_visible_flag = true;
                        } else {
                            routeplan.setVisibility(View.GONE);
                            routeplan_visible_flag = true;
                        }
                        toast(view.getTag());
                        break;
                    case 5:
                        if(startNode == null || enPlanNode == null){
                            toast("您还未进行路线规划");
                        }else{
                            mShareUrlSearch.requestRouteShareUrl(new RouteShareURLOption().from(startNode).to(enPlanNode).routMode(mRouteShareMode));
                            toast(view.getTag());
                        }
                        break;
                    case 6:
                        mBaiduMap.clear();
                        downloadVoice();
                        break;

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        location.start();
        myOrientationListener.start();
        isFirstIn = true;
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
        manager.removeNearbyInfoListener(myRadarSearchListener);
        myOrientationListener.stop();
        manager.clearUserInfo();
    }

    @Override
    protected void onDestroy() {
        log("onDestory");
        mSearch.destroy();
        manager.destroy();
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        mShareUrlSearch.destroy();
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


    /**
     * 录音
     */
    private List<BmobFile> voiceList = new ArrayList<>();
    private List<LatLng> geoList = new ArrayList<>();
    private List<UserBean> userList = new ArrayList<>();
    private void downloadVoice(){
        BmobQuery<VoiceBean> q = new BmobQuery<>();
        q.findObjects(MapActivity.this, new FindListener<VoiceBean>() {
            @Override
            public void onSuccess(List<VoiceBean> list) {
                Iterator<VoiceBean> i = list.iterator();
                log("size" + list.size());
                while(i.hasNext()){
                    VoiceBean voiceBean = i.next();
                    BmobFile voice = voiceBean.getFile();
                    UserBean user = voiceBean.getUser();
                    Double latitude = voiceBean.getGeo().getLatitude();
                    Double longitude= voiceBean.getGeo().getLongitude();
                    LatLng latLng = new LatLng(latitude , longitude);

                    voiceList.add(voice);
                    geoList.add(latLng);
                    userList.add(user);

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("geo" , latLng);
                    bundle.putSerializable("voice" , voice);
                    bundle.putSerializable("user" , user);

                    //构建Marker图标
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
                    //构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions()
                            .animateType(MarkerOptions.MarkerAnimateType.grow)
                            .position(latLng)
                            .extraInfo(bundle)
                            .icon(bitmap);
                    //在地图上添加Marker，并显示
                    mBaiduMap.addOverlay(option);
                    mBaiduMap.setOnMarkerClickListener(new MyVoiceMarkerClickListener());
                    toast("下载完成");
                    log("下载完成");
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private class MyVoiceMarkerClickListener implements BaiduMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            mBaiduMap.hideInfoWindow();

            if (marker != null) {
                LatLng geo = marker.getExtraInfo().getParcelable("geo");
                BmobFile voice  = (BmobFile) marker.getExtraInfo().getSerializable("voice");

                final Bundle bundle = new Bundle();
                bundle.putString("voice" , voice.getFileUrl(MapActivity.this));
                //MediaPlayer mp = new MediaPlayer();
                //try {
                    log("url" + Uri.parse(voice.getFileUrl(MapActivity.this)));
                    //mp.setDataSource(MapActivity.this , Uri.parse(voice.getFileUrl(MapActivity.this)));
                //} catch (IOException e) {
                    //e.printStackTrace();
                //}
                //mp.start();
               /* voice.download(MapActivity.this, new DownloadFileListener() {
                    @Override
                    public void onSuccess(String s) {
                        toast("下载完成");
                        MediaPlayer mp = new MediaPlayer();
                        try {
                            mp.setDataSource(s);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mp.start();
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });*/
                //UserBean user = (UserBean) marker.getExtraInfo().getSerializable("user");

               /* final Bundle bundle = new Bundle();
                UserModel.getInstance().queryUsers(around_name, 20, new FindListener<UserBean>() {
                    @Override
                    public void onSuccess(List<UserBean> list) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getUsername().equals(around_name)) {
                                bundle.putSerializable("u", list.get(i));
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });*/

                popupText = new TextView(MapActivity.this);
                popupText.setBackgroundResource(R.drawable.popup);
                popupText.setTextColor(0xFF000000);
                popupText.setText("地理" + geo.toString());
                popupText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        log("点击");
                        startActivity(PlayerActivity.class , bundle);
                    }
                });

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







































    /**
     * 开始自动上传
     */
    public void upload() {
        manager.startUploadAuto(myRadarUploadInfoCallback, 5000);
        RadarNearbySearchOption option = new RadarNearbySearchOption()
                .centerPt(pt)
                .radius(2000);
        manager.nearbyInfoRequest(option);
    }

    /**
     * 停止自动上传
     */
    public void stopUpload() {
        manager.stopUploadAuto();
        parseResult(null);
        manager.clearUserInfo();
        mBaiduMap.hideInfoWindow();
    }

    /**
     * 更新结果地图
     *
     * @param res
     */
    public void parseResult(RadarNearbyResult res) {
        mBaiduMap.clear();

        mBaiduMap.setOnMarkerClickListener(new MyRadarMarkerClickListener());

        if (res != null && res.infoList != null && res.infoList.size() > 0) {
            for (int i = 0; i < res.infoList.size(); i++) {
                RadarNearbyInfo nearbyInfo = res.infoList.get(i);

                Long other = nearbyInfo.timeStamp.getTime();
                Long mine = new Date().getTime();

                MarkerOptions option;
                BitmapDescriptor ff3;
                Boolean flag = false;

                //if(other > mine - TimeUtil.ONE_WEEK){
                    Iterator<Friend> iterator = friendList.iterator();


                    while(iterator.hasNext()){
                        if(iterator.next().getFriendUser().getObjectId().equals(nearbyInfo.comments)){
                            flag = true;
                            break;
                        }
                        flag = false;
                    }

                    if(flag){
                        ff3 = BitmapDescriptorFactory.fromResource(R.drawable.friend_mark);
                    }else {
                        ff3 = BitmapDescriptorFactory.fromResource(R.drawable.stranger_mark);
                    }



                    option = new MarkerOptions().icon(ff3).position(res.infoList.get(i).pt);
                    option.animateType(MarkerOptions.MarkerAnimateType.grow);

                    final Bundle des = new Bundle();
                    if (nearbyInfo.comments == null || res.infoList.get(i).comments.equals("")) {
                        log("用户信息不完整");
                    } else {
                        UserModel.getInstance().queryUserInfo(res.infoList.get(i).comments, new QueryUserListener() {
                            @Override
                            public void done(UserBean s, BmobException e) {
                                if (e == null) {
                                    des.putString("radar_id" , s.getObjectId());
                                    des.putString("radar_name" , s.getUsername());
                                    des.putString("radar_hobby" , s.getHobby());
                                    des.putString("radar_sort" , s.getSort());
                                }
                            }

                        });

                    }

                    des.putDouble("radar_distance" , DistanceUtil.getDistance(pt, res.infoList.get(i).pt));
                    option.extraInfo(des);
                    mBaiduMap.addOverlay(option);

                //}
            }
        }
    }

    /*private void monitorResult(LatLng latLng , LatLng pt) {

        Double distance = DistanceUtil. getDistance(latLng, pt);

    }*/

    private class MyRadarSearchListener implements RadarSearchListener {

        @Override
        public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError radarSearchError) {
            if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
                toast("查询周边的人成功");

                parseResult(radarNearbyResult);
            } else {
                toast("查询周边的人失败");
                log("查询周边失败" + radarSearchError.toString());
            }
        }

        @Override
        public void onGetUploadState(RadarSearchError radarSearchError) {

        }

        @Override
        public void onGetClearInfoState(RadarSearchError radarSearchError) {
            if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
                Toast.makeText(MapActivity.this, "清除位置成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MapActivity.this, "清除位置失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class MyRadarUploadInfoCallback implements RadarUploadInfoCallback {

        @Override
        public RadarUploadInfo onUploadInfoCallback() {
            if (pt == null) {
                Toast.makeText(MapActivity.this, "未获取到位置", Toast.LENGTH_LONG).show();
                return null;
            }
            RadarUploadInfo info = new RadarUploadInfo();
            info.comments = userComment;
            info.pt = pt;
            manager.uploadInfoRequest(info);
            return info;
        }
    }

    public String around_name;
    public String around_id;
    public String around_hobby;
    public String around_sort;
    public Double around_distance;

    private class MyRadarMarkerClickListener implements BaiduMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            mBaiduMap.hideInfoWindow();

            if (marker != null) {
                around_name = marker.getExtraInfo().getString("radar_name");
                around_id = marker.getExtraInfo().getString("radar_id");
                around_hobby = marker.getExtraInfo().getString("radar_hobby");
                around_sort = marker.getExtraInfo().getString("radar_sort");
                around_distance = marker.getExtraInfo().getDouble("radar_distance");

                EventBus.getDefault().post(new UserEvent(around_id , around_name , around_hobby , around_sort , around_distance));

                final Bundle bundle = new Bundle();
                UserModel.getInstance().queryUsers(around_name, 20, new FindListener<UserBean>() {
                    @Override
                    public void onSuccess(List<UserBean> list) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getUsername().equals(around_name)) {
                                bundle.putSerializable("u", list.get(i));
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });

               /* if(around_distance <= 10.0){
                    MyDate date = new MyDate();
                    date.setUser(BmobUser.getCurrentUser(MapActivity.this , UserBean.class));
                    date.setFriendID(around_name);
                    date.save(MapActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            log("上传成功");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            log("上传失败");
                        }
                    });
                }*/

                popupText = new TextView(MapActivity.this);
                popupText.setBackgroundResource(R.drawable.popup);
                popupText.setTextColor(0xFF000000);
                popupText.setText("用户" + around_name + "\n偏好" + around_hobby + "\nsort" + around_sort + "\n距离" + around_distance);
                popupText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(UserInfoActivity.class, bundle, false);
                    }
                });

                mBaiduMap.showInfoWindow(new InfoWindow(popupText, marker.getPosition(), -47));
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(marker.getPosition());
                mBaiduMap.setMapStatus(update);

                return true;
            } else {
                return false;
            }
        }
    }



































    @OnClick({R.id.search, R.id.map_next_data})
    public void onClickPoi(View view) {
        switch (view.getId()) {
            case R.id.search:
                searchPoi();
                break;
            case R.id.map_next_data:
                loadIndex++;
                searchPoi();
                break;
        }
    }
    public void searchPoi() {
        String cityTemp = etCity.getText().toString();
        searchKey = searchkey.getText().toString();
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(cityTemp)
                .keyword(searchKey)
                .pageNum(loadIndex));
    }

    private class MyPoiSearchResultListener implements OnGetPoiSearchResultListener {

        public void onGetPoiResult(PoiResult result) {
            if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                toast("未找到结果");
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                mBaiduMap.clear();

                PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result);
                overlay.addToMap();
                overlay.zoomToSpan();

                /*PoiShareOverlay poiOverlay = new PoiShareOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(poiOverlay);
                poiOverlay.setData(result);
                poiOverlay.addToMap();
                poiOverlay.zoomToSpan();*/

               /* switch (sort) {
                    case "热度优先":
                        //myLoction(center);
                        showNearbyArea(center, radius);
                        break;
                    case "距离优先":
                        //myLoction(center);
                        showBound(searchbound);
                        break;
                }*/

                return;
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
                toast("没有在附近找到相关信息");
            }
        }

        /**
         * 获取POI详情搜索结果，得到searchPoiDetail返回的搜索结果
         *
         * @param result
         */
        public void onGetPoiDetailResult(PoiDetailResult result) {
            log("onGetPoiDetailResult");
            if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                toast("抱歉，未找到结果");
            } else {
                log("已查询到结果");
                String poiUrl = result.getDetailUrl();
                String poiname = result.getName();
                String poiAddress = result.getAddress();
                String poiTele = result.getTelephone();
                String poiTag = result.getTag();
                String poiTime = result.getShopHours();
                Double poiHRating = result.getHygieneRating();//卫生
                Double poiTRating= result.getTasteRating();//味道
                Double poiORating= result.getOverallRating();//综合
                Double poiSRating = result.getServiceRating();//服务

                Bundle bundle =  new Bundle();
                bundle.putString("poiname" , poiname);
                bundle.putString("poiAddress" , poiAddress);
                bundle.putString("poiTele" , poiTele);
                bundle.putString("poiTag" , poiTag);
                bundle.putDouble("poiHRating" , poiHRating);
                bundle.putDouble("poiTRating" , poiTRating);
                bundle.putDouble("poiORating" , poiORating);
                bundle.putDouble("poiSRating" , poiSRating);
                bundle.putString("poiTime" , poiTime);
                bundle.putString("url" , poiUrl);
                startActivity(PoiDetailActivity.class , bundle);
            }
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

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
























    private class MySuggestionResultListener implements OnGetSuggestionResultListener {

        /**
         * 获取在线建议搜索结果，得到requestSuggestion返回的搜索结果
         *
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
            sugAdapter = new ArrayAdapter<String>(MapActivity.this, android.R.layout.simple_dropdown_item_1line, suggest);
            searchkey.setAdapter(sugAdapter);
            sugAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 发起路线规划搜索
     * 起终点
     *
     * @param view
     */
    @OnClick({R.id.drive, R.id.transit, R.id.walk, R.id.bike})
    public void onClickTransport(View view) {
        // 重置浏览节点的路线数据 清空地图所有的 Overlay 覆盖物以及 InfoWindow
        route = null;
        pre.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        mBaiduMap.clear();
        startNode = PlanNode.withCityCodeAndPlaceName(cityCode, start.getText().toString());
        enPlanNode = PlanNode.withCityCodeAndPlaceName(cityCode, end.getText().toString());
        switch (view.getId()) {
            case R.id.drive:
                mSearch.drivingSearch((new DrivingRoutePlanOption())
                        .from(startNode)
                        .to(enPlanNode));
                break;
            case R.id.transit:
                mSearch.transitSearch((new TransitRoutePlanOption())
                        .from(startNode)
                        .city(city)
                        .to(enPlanNode));
                break;
            case R.id.walk:
                mSearch.walkingSearch((new WalkingRoutePlanOption())
                        .from(startNode)
                        .to(enPlanNode));
                break;
            case R.id.bike:
                mSearch.bikingSearch((new BikingRoutePlanOption())
                        .from(startNode)
                        .to(enPlanNode));
                break;
        }
    }

    /**
     * 节点浏览
     *
     * @param view
     */
    @OnClick({R.id.pre, R.id.next})
    public void onClickNode(View view) {
        if (route == null || route.getAllStep() == null) {
            return;
        }
        if (nodeIndex == -1 && view.getId() == R.id.pre) {
            return;
        }
        // 设置节点索引
        if (view.getId() == R.id.next) {
            if (nodeIndex < route.getAllStep().size() - 1) {
                nodeIndex++;
            } else {
                return;
            }
        } else if (view.getId() == R.id.pre) {
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
        TextView popupText = new TextView(MapActivity.this);
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
        public MyBikingRouteOverlay(BaiduMap baiduMap) {
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
        void onItemClick(int position);
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
                    pre.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    dismiss();

                }
            });
        }

        public void setOnItemInDlgClickLinster(OnItemInDlgClickListener itemListener) {
            onItemInDlgClickListener = itemListener;
        }
    }

    private class MyShareUrlResultListener implements OnGetShareUrlResultListener {

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




    private class MyGetGeoCoderResultListener implements OnGetGeoCoderResultListener {

        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                toast("抱歉，未找到结果");
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

    /**
     * 点击分享短链
     */
    private class MyMarkerClickListener implements BaiduMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (marker == mAddrMarker) {
                mShareUrlSearch.requestLocationShareUrl(new LocationShareURLOption()
                        .location(marker.getPosition())
                        .name(marker.getTitle()));
            }
            return true;
        }
    }


}
