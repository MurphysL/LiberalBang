package com.example.lenovo.murphysl.map;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Location
 * 定位相关
 *
 * @author: lenovo
 * @time: 2016/8/4 22:53
 */

public class Location {

    private LocationClient mLocationClient = null;
    private LocationClientOption mOption = null;
    private Object  objLock = new Object();

    public Location(Context locationContext) {
        synchronized (objLock) {
            if(mLocationClient == null){
                mLocationClient = new LocationClient(locationContext);
                mLocationClient.setLocOption(getDefaultLocationClientOption());
            }
        }
    }

    /**
     * 开始定位
     */
    public void start(){
        synchronized (objLock) {
            if(mLocationClient != null && !mLocationClient.isStarted()){
                mLocationClient.start();
            }
        }
    }

    /**
     * 结束定位
     */
    public void stop(){
        synchronized (objLock) {
            if(mLocationClient != null && mLocationClient.isStarted()){
                mLocationClient.stop();
            }
        }
    }

    /**
     * 注册监听器
     * @param listener
     * @return
     */
    public boolean registerLocListener(BDLocationListener listener){
        boolean isSuccess = false;
        if(listener != null){
            mLocationClient.registerLocationListener(listener);
            isSuccess = true;
        }
        return  isSuccess;
    }

    /**
     * 解除监听器
     * @param listener
     */
    public void unregisterLocListener(BDLocationListener listener){
        if(listener != null){
            mLocationClient.unRegisterLocationListener(listener);
        }
    }

    /**
     * 默认设置
     * @return
     */
    public LocationClientOption getDefaultLocationClientOption(){
        if(mOption == null){
            mOption = new LocationClientOption();
            mOption.setOpenGps(true);//是否使用GPS
            mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            mOption.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
            mOption.setNeedDeviceDirect(true);//可选，设置是否需要设备方向结果
            mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            //mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        }
        return mOption;
    }

}
