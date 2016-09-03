package com.example.lenovo.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment1
 *
 * @author: lenovo
 * @time: 2016/8/5 11:28
 */

public class MyFragment extends Fragment {
    private static final String TAG = "MyFragment";

    private Handler mHandler;

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        //此处产生了耦合
//        if(activity instanceof MainActivityTest){
//            mHandler =  ((MainActivityTest)activity).mHandler;
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment2, container, false);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0 ;i < 100 ;i ++){
                    try {
                        Message message = new Message();
                        message.what = i;
                        mHandler.sendMessage(message);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG , i+ "");
                }
            }
        }).start();
    }

}
