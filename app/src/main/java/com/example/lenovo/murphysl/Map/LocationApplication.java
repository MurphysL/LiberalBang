package com.example.lenovo.murphysl.Map;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.example.lenovo.murphysl.Map.Location;

/**
 * LocationApplication
 *
 * 主Application
 *
 * @author: lenovo
 * @time: 2016/8/4 23:38
 */

public class LocationApplication extends Application {
    public Location location;
    public Vibrator mVibrator;

    @Override
    public void onCreate() {
        super.onCreate();

        location = new Location(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        /***
         * 初始化定位sdk
         */
        SDKInitializer.initialize(getApplicationContext());
    }
}
