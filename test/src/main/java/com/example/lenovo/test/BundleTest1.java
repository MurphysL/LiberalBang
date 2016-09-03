package com.example.lenovo.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * BundleTest1
 *
 * @author: lenovo
 * @time: 2016/8/22 20:53
 */

public class BundleTest1 extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.b1);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button2)
    public void onClick() {
        Bundle bundle = new Bundle();
        bundle.putString("b" , "lalal");
        Intent intent = new Intent();
        intent.setClass(this , BundleTest2.class);
        //intent.putExtra();
    }
}
