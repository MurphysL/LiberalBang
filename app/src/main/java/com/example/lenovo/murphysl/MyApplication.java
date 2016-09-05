package com.example.lenovo.murphysl;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.graphics.Bitmap;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.example.lenovo.murphysl.map.Location;
import com.example.lenovo.murphysl.base.UniversalImageLoader;
import com.example.lenovo.murphysl.model.UserModel;
import com.example.lenovo.murphysl.util.ActivityManagerUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
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

    private static MyApplication INSTANCE;
    public static MyApplication INSTANCE(){
        return INSTANCE ;
    }
    private void setInstance(MyApplication app) {
        setMyApplication(app);
    }
    public static MyApplication getINSTANCE() {
        return INSTANCE;
    }
    private static void setMyApplication(MyApplication a) {
        MyApplication.INSTANCE = a;
    }

    public Location location;


    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        location = new Location(getApplicationContext());

        /**
         * 初始化定位sdk
         */
        SDKInitializer.initialize(getApplicationContext());

        /**
         * 初始化IM（只有主进程运行的时候才需要初始化）
         */
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            BmobIM.init(this); //im初始化
            BmobIM.registerDefaultMessageHandler(new MyMessageHandler(this)); //注册消息接收器 this
        }
        UniversalImageLoader.initImageLoader(this);//UI初始化

        Logger.init("MurphySL");
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

    public DisplayImageOptions getOptions(int drawableId){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(drawableId)
                .showImageForEmptyUri(drawableId)
                .showImageOnFail(drawableId)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public Activity getTopActivity(){
        return ActivityManagerUtils.getInstance().getTopActivity();
    }
}
