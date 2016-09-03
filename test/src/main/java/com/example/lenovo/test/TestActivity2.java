package com.example.lenovo.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

/**
 * TestActivity2
 *
 * @author: lenovo
 * @time: 2016/8/11 23:53
 */

public class TestActivity2 extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
    }

    public void doClick(View v){
        EventBus.getDefault().post(new EventBusTestActivity("Change"));
    }
}
