package com.example.lenovo.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

/**
 * TestActivity1
 *
 * @author: lenovo
 * @time: 2016/9/16 0:29
 */

public class TestActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);

        final BezierLayout layout = (BezierLayout) findViewById(R.id.bezier);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layout.addHeart();
                return false;
            }
        });

    }
}
