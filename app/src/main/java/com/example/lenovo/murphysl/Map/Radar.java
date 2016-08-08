package com.example.lenovo.murphysl.Map;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
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
import com.example.lenovo.murphysl.MapActivity;
import com.example.lenovo.murphysl.R;

import java.util.List;

/**
 * Radar
 *
 * @author: lenovo
 * @time: 2016/8/5 0:00
 */

/*
public class Radar {
    private static final String TAG = "Radar";
    //雷达
    private RadarSearchManager manager;
    private MyRadarUploadInfoCallback myRadarUploadInfoCallback;
    private LatLng latLng;
    private Context radarContext;


    private String userID = "";
    private String userComment = "13";//用户备注信息
    private boolean flag = false;

    public Radar(Context radarContext , LatLng latLng) {
        manager = RadarSearchManager.getInstance();
        myRadarUploadInfoCallback = new MyRadarUploadInfoCallback();
        registerRadarListener()
        manager.setUserID(userID);//设置用户,id为空默认为设备标识

        this.latLng = latLng;
        this.radarContext = radarContext;
    }

    */
/**
     * 注册监听器
     * @param listener
     * @return
     *//*

    public boolean registerRadarListener(RadarSearchListener listener){
        boolean isSuccess = false;
        if(listener != null){
            manager.addNearbyInfoListener(listener);
            isSuccess = true;
        }
        return  isSuccess;
    }

    */
/**
     * 解除监听器
     * @param listener
     *//*

    public void unregisterRadarListener(RadarSearchListener listener){
        if(listener != null){
            manager.removeNearbyInfoListener(listener);
        }
    }

    */
/**
     * 开始自动上传
     *//*

    public void uploadContinueClick() {
        if (latLng == null) {
            Toast.makeText(radarContext , "未获取到位置", Toast.LENGTH_LONG).show();
            return;
        }
        //uploadAuto = true;
        manager.startUploadAuto(myRadarUploadInfoCallback, 5000);
        */
/*uploadContinue.setEnabled(false);
        stopUpload.setEnabled(true);
        clearInfoBtn.setEnabled(true);*//*

    }

    */
/**
     * 停止自动上传
     *//*

    public void stopUploadClick() {
        //uploadAuto = false;
        RadarSearchManager.getInstance().stopUploadAuto();
       */
/* stopUpload.setEnabled(false);
        uploadContinue.setEnabled(true);*//*

    }

    */
/**
     * 清除自己当前的信息
     *//*

    public void clearInfoClick() {
        manager.clearUserInfo();
    }

    */
/**
     * 查找周边的人
     *//*

    public void searchNearby() {
        if (latLng == null) {
            Toast.makeText(radarContext, "未获取到位置", Toast.LENGTH_LONG).show();
            return;
        }
        RadarNearbySearchOption option = new RadarNearbySearchOption()
                .centerPt(latLng)//搜索中心点
                .pageNum(0)
                .radius(2000);//搜索半径

        manager.nearbyInfoRequest(option);//发起查询请求
    }

    public void destroyRadar(){
        manager.destroy();
    }

    private class MyRadarUploadInfoCallback implements RadarUploadInfoCallback {

        @Override
        public RadarUploadInfo onUploadInfoCallback() {
            if (latLng == null) {
                Toast.makeText(radarContext , "未获取到位置", Toast.LENGTH_LONG).show();
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


}
*/
