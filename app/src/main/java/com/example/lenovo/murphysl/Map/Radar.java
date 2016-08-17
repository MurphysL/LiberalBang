package com.example.lenovo.murphysl.map;

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

    public void upload() {
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

    public void stopUpload() {
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
