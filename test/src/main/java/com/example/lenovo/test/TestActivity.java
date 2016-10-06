package com.example.lenovo.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * TestActivity
 *
 * @author: lenovo
 * @time: 2016/9/15 18:16
 */

public class TestActivity extends AppCompatActivity {

    private ListView listView;
    private List<String> s = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anim_list);
        listView = (ListView) findViewById(R.id.listView);

        initList();
    }

    private void initList() {
        TranslateAnimation translate = (TranslateAnimation) AnimationUtils.loadAnimation(this , R.anim.translate);
        LayoutAnimationController controller = new LayoutAnimationController(translate);
        controller.setDelay(1);
        controller.setOrder(LayoutAnimationController.ORDER_RANDOM);
        listView.setLayoutAnimation(controller);
        for(int i = 0 ; i < 10 ; i ++)
            s.add("标题" + i);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this , R.layout.list_item , R.id.text , s);
        listView.setAdapter(adapter);

    }

}
