package com.example.lenovo.murphysl.Map;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.lenovo.murphysl.MapActivity;

import java.util.List;

/**
 * MyListener
 *
 * @author: lenovo
 * @time: 2016/8/6 1:01
 */

public class MyListener implements BDLocationListener{
    private static final String TAG = "MyListener";


    private Context context;

    /**
     * 方向传感器
     */
//    private SensorManager mSensorManager;
//    private Sensor sensor;
//    private float lastX;

    /**
     * 定位传感器
     */
    private String loc;
    private MyLocationData data;
    private LatLng latLng;

    private OnMyListener onMyListener;

    public MyListener(){

    }

    public MyListener(Context context) {
        this.context = context;
    }

    /*public void startOrientationListener(){
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null){
            sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);//获取方向传感器
        }

        if(sensor != null){
            mSensorManager.registerListener(this , sensor , SensorManager.SENSOR_DELAY_UI);
        }
    }*/

    /*public void stopOrientationListener(){
        mSensorManager.unregisterListener(this);
    }*/


    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

        if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
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
            loc = sb.toString();


          /*  //设置中心点
            data = new MyLocationData.Builder()
                    .direction(lastX)//定位数据方向
                    .accuracy(bdLocation.getRadius())//定位数据精度信息
                    .latitude(bdLocation.getLatitude())//定位数据的纬度
                    .longitude(bdLocation.getLongitude())//定位数据的经度
                    .build();//生成定位数据对象*/

            //更新经纬度
            latLng = new LatLng(bdLocation.getLatitude() , bdLocation.getLongitude());

           /* if(onMyListener != null){
                onMyListener.onMyListenerChanged(lastX , loc , data , latLng);
            }*/
        }

    }

   /* @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
            float x = event.values[SensorManager.DATA_X];

            if(Math.abs(x - lastX) > 1.0){
                if(onMyListener != null){
                    onMyListener.onMyListenerChanged(x , loc , data , latLng);
                }
            }

            lastX = x;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }*/

    public interface OnMyListener{
        void onMyListenerChanged(float x , String loc , MyLocationData data , LatLng latLng);
    }

    public void setOnMyListener(OnMyListener onMyListener) {
        this.onMyListener = onMyListener;
    }

}
