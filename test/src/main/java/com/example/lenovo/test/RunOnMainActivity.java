package com.example.lenovo.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * RunOnMainActivity
 *
 * @author: lenovo
 * @time: 2016/8/13 19:13
 */

public class RunOnMainActivity extends Activity {


    @Bind(R.id.tv_rom)
    TextView tvRom;

    private int i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runonmain);
        ButterKnife.bind(this);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*while(true){
                    try {
                        tvRom.setText(i + "");
                        Log.i("RunOnMain" , i + "");
                        i ++;
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }*/

                for(int j = 0 ;j < 3;j ++){
                    try {
                        tvRom.setText(i + "");
                        Log.i("RunOnMain" , i + "");
                        i ++;
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

}
