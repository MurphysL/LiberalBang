package com.example.lenovo.murphysl.Map;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.example.lenovo.murphysl.R;

/**
 * NotifyActivity
 *
 * 位置提醒
 *
 * @author: lenovo
 * @time: 2016/8/4 23:54
 */

public class NotifyActivity extends Activity {

    private Button startNotify;
    private Vibrator mVibrator;
    private LocationClient mLocationClient;
    private NotiftLocationListener listener;
    private double longtitude,latitude;
    private NotifyLister mNotifyLister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.notify);
        listener = new NotiftLocationListener();
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);

        startNotify = (Button)findViewById(R.id.notifystart);
        mLocationClient  = new LocationClient(this);
        mLocationClient.registerLocationListener(listener);
        mNotifyLister = new NotifyLister();
        mLocationClient.registerNotify(mNotifyLister);
        startNotify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(startNotify.getText().toString().equals("开启位置提醒")){
                    mLocationClient.start();
                    startNotify.setText("关闭位置提醒");
                }else{
                    if(mNotifyLister!=null){
                        mLocationClient.removeNotifyEvent(mNotifyLister);
                        startNotify.setText("开启位置提醒");
                    }

                }


            }
        });
    }

    @Override
    protected void onStop() {

        super.onStop();
        mLocationClient.removeNotifyEvent(mNotifyLister);
        mLocationClient = null;
        mNotifyLister= null;
        listener = null;

    }

    private Handler notifyHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            mNotifyLister.SetNotifyLocation(latitude,longtitude, 3000,mLocationClient.getLocOption().getCoorType());//4个参数代表要位置提醒的点的坐标，具体含义依次为：纬度，经度，距离范围，坐标系类型(gcj02,gps,bd09,bd09ll)
        }

    };
    public class NotiftLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            longtitude = location.getLongitude();
            latitude = location.getLatitude();
            notifyHandler.sendEmptyMessage(0);
        }
    }
    public class NotifyLister extends BDNotifyListener {
        public void onNotify(BDLocation mlocation, float distance){
            mVibrator.vibrate(1000);//振动提醒已到设定位置附近
            Toast.makeText(NotifyActivity.this, "震动提醒", Toast.LENGTH_SHORT).show();
        }
    }
}
