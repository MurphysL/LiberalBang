package com.example.lenovo.murphysl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
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
import com.baidu.mapapi.search.core.SearchResult;
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
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.lenovo.murphysl.BaiDu.DistrictSearchDemo;
import com.example.lenovo.murphysl.BaiDu.PoiSearchDemo;
import com.example.lenovo.murphysl.BaiDu.RoutePlanDemo;
import com.example.lenovo.murphysl.BaiDu.ShareDemoActivity;
import com.example.lenovo.murphysl.Map.MyOrientationListener;
import com.example.lenovo.murphysl.UI.ArcMenu;
import com.example.lenovo.murphysl.com.baidu.mapapi.overlayutil.PoiOverlay;

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

    /**
     * 地图相关
     */
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;

    /**
     * 定位
     */
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private boolean isFirstIn = true;
    private LatLng latLng;
    private MyOrientationListener myOrientationListener;
    private float mCurrentX;
    private BitmapDescriptor mIconLocation;//自定义图标

    /**
     * 雷达
     */
    private RadarSearchManager manager;
    private MyRadarSearchListener myRadarSearchListener;
    private String userID = "1";
    private String userComment = "13";//用户备注信息
    private MyRadarUploadInfoCallback myRadarUploadInfoCallback;
    private boolean flag = false;

    //private boolean flag = false;
    private boolean flag1 = false;

    /**
     * 周边搜索
     */
  /*  private PoiSearch mPoiSearch;
    private SuggestionSearch mSuggestionSearch = null;
    private MyGetPoiSearchResultListener myGetPoiSearchResultListener;
    private MyGetSuggestionResultListener myGetSuggestionResultListener;
    private List<String> suggest;*/

    /**
     * 搜索关键字输入窗口
     */
    private View search_view;
  /*  private boolean visible_flag = true;
    private EditText editCity = null;
    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int loadIndex = 0;

    LatLng center = new LatLng(39.92235, 116.380338);
    int radius = 500;
    LatLng southwest = new LatLng( 39.92235, 116.380338 );
    LatLng northeast = new LatLng( 39.947246, 116.414977);*/

    /**
     * 地理范围构造器
     */
  /*  LatLngBounds searchbound = new LatLngBounds.Builder()
            .include(southwest)
            .include(northeast)
            .build();

    int searchType = 0;  // 搜索的类型，在显示时区分*/

    //卫星菜单
    private ArcMenu mArcMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        this.context = this;

        initView();
        initLocation();
        initRadar();
        //initPoi();
        initEvent();
    }

    /**
     * 初始化Poi
     */
   /* private void initPoi() {
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(myGetPoiSearchResultListener);

        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(myGetSuggestionResultListener);

        sugAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        keyWorldsView.setAdapter(sugAdapter);
        keyWorldsView.setThreshold(1);

        *//**
         * 当输入关键字变化时，动态更新建议列表
         *//*
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

                *//**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 *//*
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city(editCity.getText().toString()));
            }
        });
    }*/

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
        mMapView = (MapView) findViewById(R.id.bmapView);
        mArcMenu = (ArcMenu) findViewById(R.id.id_menu);
        //editCity = (EditText) findViewById(R.id.city);
        //keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchkey);
        //search_view  = findViewById(R.id.poi_search);

        mBaiduMap = mMapView.getMap();
        //设置初始视距
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
    }

    //ctrl shift enter 自动括号
    private void Test() {

    }

    /**
     * 初始化监听事件
     * 1、定位
     * 2、雷达
     * 3、周边
     */
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
                        myLoction(latLng);
                        break;
                    case 2:
                       /* if(visible_flag){
                            //search_view.setVisibility(View.VISIBLE);
                            visible_flag = false;
                        }else{
                            search_view.setVisibility(View.GONE);
                            visible_flag = true;
                        }*/
                        Intent intent = new Intent(context , RoutePlanDemo.class);
                        startActivity(intent);
                        break;
                    case 3:
                       /* if(visible_flag){
                            //search_view.setVisibility(View.VISIBLE);
                            visible_flag = false;
                        }else{
                            search_view.setVisibility(View.GONE);
                            visible_flag = true;
                        }*/
                        Intent intent2 = new Intent(context , PoiSearchDemo.class);
                        startActivity(intent2);
                        break;
                    case 4:
                       /* if(visible_flag){
                            //search_view.setVisibility(View.VISIBLE);
                            visible_flag = false;
                        }else{
                            search_view.setVisibility(View.GONE);
                            visible_flag = true;
                        }*/
                        Intent intent4 = new Intent(context , ShareDemoActivity.class);
                        startActivity(intent4);
                        break;
                   /* case 5:
                       *//* if(visible_flag){
                            //search_view.setVisibility(View.VISIBLE);
                            visible_flag = false;
                        }else{
                            search_view.setVisibility(View.GONE);
                            visible_flag = true;
                        }*//*
                        Intent intent5 = new Intent(context , DistrictSearchDemo.class);
                        startActivity(intent5);
                        break;*/

                  /*  case 3:
                        if(flag){
                            radar.stopUploadClick();
                            flag = false;
                        }else{
                            openRadar();
                            radar.uploadContinueClick();
                            radar.searchNearby();
                            flag = true;
                        }
                        break;*/
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
    }
    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        manager.destroy();
        //mPoiSearch.destroy();
        //mSuggestionSearch.destroy();

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

    //private class MyGetPoiSearchResultListener implements OnGetPoiSearchResultListener{

        /**
         * 获取POI搜索结果，包括searchInCity，searchNearby，searchInBound返回的搜索结果
         * @param result
         */
        /*public void onGetPoiResult(PoiResult result) {
            if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                Toast.makeText(context, "未找到结果", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                mBaiduMap.clear();
                PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result);
                overlay.addToMap();
                overlay.zoomToSpan();

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
        }*/

        /**
         * 获取POI详情搜索结果，得到searchPoiDetail返回的搜索结果
         * @param result
         */
       /* public void onGetPoiDetailResult(PoiDetailResult result) {
            if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT).show();
            }
        }*/

        //@Override
      /*  public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }*/
    //}

   /* private class MyGetSuggestionResultListener implements OnGetSuggestionResultListener{

        *//**
         * 获取在线建议搜索结果，得到requestSuggestion返回的搜索结果
         * @param res
         *//*
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
    }*/

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
        //uploadAuto = true;
        manager.startUploadAuto(myRadarUploadInfoCallback, 5000);
        /*uploadContinue.setEnabled(false);
        stopUpload.setEnabled(true);
        clearInfoBtn.setEnabled(true);*/
    }

    /**
     * 停止自动上传
     */
    public void stopUploadClick() {
        //uploadAuto = false;
        RadarSearchManager.getInstance().stopUploadAuto();
       /* stopUpload.setEnabled(false);
        uploadContinue.setEnabled(true);*/
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
                .pageNum(0)
                .radius(2000);//搜索半径

        manager.nearbyInfoRequest(option);//发起查询请求
    }

    /**
     * 响应城市内搜索按钮点击事件
     *
     * @param v
     */
   /* public void searchButtonProcess(View v) {
        searchType = 1;
        String citystr = editCity.getText().toString();
        String keystr = keyWorldsView.getText().toString();
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(citystr)
                .keyword(keystr)
                .pageNum(loadIndex));
    }*/

    /**
     * 响应周边搜索按钮点击事件
     *
     * @param v
     */
    /*public void  searchNearbyProcess(View v) {
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

    public void goToNextPage(View v) {
        loadIndex++;
        searchButtonProcess(null);
    }*/

    /**
     * 响应区域搜索按钮点击事件
     *
     * @param v
     */
   /* public void searchBoundProcess(View v) {
        searchType = 3;

        mPoiSearch.searchInBound(new PoiBoundSearchOption()
                .bound(searchbound)
                .keyword(keyWorldsView.getText().toString()));

    }*/


   /* private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            // }
            return true;
        }
    }*/

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

}
