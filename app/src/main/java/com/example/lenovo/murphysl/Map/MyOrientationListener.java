package com.example.lenovo.murphysl.Map;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.lenovo.murphysl.MapActivity;

/**
 * MyOrientationListener
 * 方向指示
 *
 * @author: lenovo
 * @time: 2016/7/30 20:57
 */

public class MyOrientationListener implements SensorEventListener{

    private SensorManager mSensorManager;
    private Context context;
    private Sensor sensor;
    private OnOrientationListener onOrientationListener;

    private float lastX;

    public MyOrientationListener(Context context) {
        this.context = context;
    }

    public void start(){
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null){
            sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);//获取方向传感器
        }

        if(sensor != null){
            mSensorManager.registerListener(this , sensor , SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop(){
        mSensorManager.unregisterListener(this);
    }

    /**
     * 开启方向传感器
     *//*
    private void openOrientationListener(Context context){
        myOrientationListener = new MyOrientationListener(context);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
                Log.i(MapActivity.TAG , mCurrentX + "");
            }
        });
    }*/

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
            float x = event.values[SensorManager.DATA_X];

            if(Math.abs(x - lastX) > 1.0){
                if(onOrientationListener != null){
                    onOrientationListener.onOrientationChanged(x);
                }
            }

            lastX = x;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface  OnOrientationListener{
        void onOrientationChanged(float x);
    }

    public void setOnOrientationListener(OnOrientationListener onOrientationListener) {
        this.onOrientationListener = onOrientationListener;
    }
}
