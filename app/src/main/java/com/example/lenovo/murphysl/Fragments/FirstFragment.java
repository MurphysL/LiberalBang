package com.example.lenovo.murphysl.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.example.lenovo.murphysl.BackHandledInterface;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.map.Location;
import com.example.lenovo.murphysl.MapActivity;
import com.example.lenovo.murphysl.MyApplication;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.base.ParentWithNaviFragment;
import com.example.lenovo.murphysl.ui.PopupListView;
import com.example.lenovo.murphysl.ui.PopupView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * FirstFragment
 *
 * 问题：
 * 1、环形进度条
 *
 * @author: lenovo
 * @time: 2016/8/4 18:49
 */

public class FirstFragment extends ParentWithNaviFragment {

    private Location location;

    private String loc;

    public static boolean flag = true;

    PopupListView popupListView;
    ArrayList<PopupView> popupViews;
    int actionBarHeight;
    int p = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.f_fragment, container, false);
        initNaviView();
        ButterKnife.bind(this, rootView);

        popupViews = new ArrayList<>();
        popupListView = (PopupListView) rootView.findViewById(R.id.popupListView);
        for (int i = 0; i < 10; i++) {
            p = i;
            //修改
            PopupView popupView = new PopupView(getActivity(), R.layout.popup_view_item) {
                @Override
                public void setViewsElements(View view) {
                    TextView textView = (TextView) view.findViewById(R.id.title);
                    textView.setText("Popup View " + String.valueOf(p));
                }

                @Override
                public View setExtendView(View view) {
                    View extendView;
                    if (view == null) {
                        extendView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R
                                .layout.extend_view, null);//修改
                        TextView innerText = (TextView) extendView.findViewById(R.id.innerText);
                        innerText.setText("Inner View " + String.valueOf(p));
                    } else {
                        extendView = view;
                    }
                    return extendView;
                }
            };
            popupViews.add(popupView);
        }
        popupListView.init(null);
        popupListView.setItemViews(popupViews);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBackHandledInterface.setSelectedFragment(this); //告诉FragmentActivity，当前Fragment在栈顶
        location = ((MyApplication) getActivity().getApplication()).location;
        location.registerLocListener(mListener);
    }

    @Override
    public void onStop() {
        location.unregisterLocListener(mListener);
        location.stop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {

            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("获取位置时间 : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\n纬度 : ");
                sb.append(location.getLatitude());
                sb.append("          经度 : ");
                sb.append(location.getLongitude());
                sb.append("\nradius : ");
                sb.append(location.getRadius());
                sb.append("\n国家代码 : ");
                sb.append(location.getCountryCode());
                sb.append("          国家 : ");
                sb.append(location.getCountry());
                sb.append("\n城市代码 : ");
                sb.append(location.getCityCode());
                sb.append("          城市 : ");
                sb.append(location.getCity());
                sb.append("\nDistrict : ");
                sb.append(location.getDistrict());
                sb.append("\n街道 : ");
                sb.append(location.getStreet());
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\nDescribe: ");
                sb.append(location.getLocationDescribe());
                sb.append("\n方向: ");
                sb.append(location.getDirection());
                sb.append("\nPoi: ");
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 单位：米
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                log(sb.toString());

                loc = sb.toString();

            } else {
                log("定位失败");
            }
        }

    };

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
                startActivity(MapActivity.class,null);
            }
        };
    }

    @Override
    public boolean onBackPressed() {
        if (popupListView.isItemZoomIn()) {
            log("Test");
            popupListView.zoomOut();
            return true;
        } else {
            log("TestX");
            return false;
        }
    }
}
