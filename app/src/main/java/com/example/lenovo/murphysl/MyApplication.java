package com.example.lenovo.murphysl;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.example.lenovo.murphysl.map.Location;
import com.example.lenovo.murphysl.base.UniversalImageLoader;
import com.example.lenovo.murphysl.model.UserModel;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;

/**
 * 主Application
 *
 * @author: lenovo
 * @time: 2016/8/4 23:38
 */

public class MyApplication extends Application {
    /**
     * 以下两个变量需要重构
     */
    public Location location;
    public Vibrator mVibrator;

    private static MyApplication INSTANCE;
    public static MyApplication INSTANCE(){
        return INSTANCE ;
    }
    private void setInstance(MyApplication app) {
        setMyApplication(app);
    }
    private static void setMyApplication(MyApplication a) {
        MyApplication.INSTANCE = a;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        Logger.init("MurphySL");

        location = new Location(getApplicationContext());

        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);

        /**
         * 初始化定位sdk
         */
        SDKInitializer.initialize(getApplicationContext());


        //只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            //im初始化
            BmobIM.init(this);
            //注册消息接收器 this
            BmobIM.registerDefaultMessageHandler(new MyMessageHandler(this));
        }

        //UI初始化
        UniversalImageLoader.initImageLoader(this);
    }

    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
