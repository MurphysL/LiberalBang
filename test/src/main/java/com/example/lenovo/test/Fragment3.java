package com.example.lenovo.test;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Fragment1
 *
 * @author: lenovo
 * @time: 2016/8/5 11:28
 */

public class Fragment3 extends Fragment {
    private static final String TAG = "Fragment3";

    private TextView tv;

    private int i = 0;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            i ++;
            Log.i(TAG , i + "");
            changeText(i);
            Message message = new Message();
            message.what = 3;
            new MainActivity().handler.sendMessage(message);
            handler.postDelayed(runnable , 1000);

        }
    };

    public void changeText(int i){
        tv.setText("This is fragment " + i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment3, container, false);
        tv = (TextView) v.findViewById(R.id.tv3);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        handler.postDelayed(runnable , 1000);
        Log.d(TAG, "onStart");
    }

}
