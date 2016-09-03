package com.example.lenovo.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * MainActivityTest
 *
 * @author: lenovo
 * @time: 2016/8/8 11:42
 */

public class MainActivityTest extends AppCompatActivity{

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        textView = (TextView) findViewById(R.id.ttv);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void doClick(View v){
        startActivity(new Intent(this , TestActivity2.class));
    }

//    @Subscribe
//    public void onEventMainThread(EventBusTestActivity event){
//        String msg = "onEventMainThread" + event.getMsg();
//        textView.setText(msg);
//        Toast.makeText(this , msg , Toast.LENGTH_LONG).show();
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hello(EventBusTestActivity event){
        String msg = "onEventMainThread" + event.getMsg();
        textView.setText(msg);
        Toast.makeText(this , msg , Toast.LENGTH_LONG).show();
    }



}
