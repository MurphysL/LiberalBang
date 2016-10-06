package com.example.lenovo.murphysl.view;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.adapter.StarContentAdapter;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.util.Constant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

/**
 * StarActivity
 *
 * @author: lenovo
 * @time: 2016/9/1 19:27
 */

public class StarActivity extends ParentWithNaviActivity {

    @Override
    protected String title() {
        return "个人排名";
    }

    private ListView lv;

    private ArrayList<UserBean> mListItems = new ArrayList<UserBean>();
    private StarContentAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);
        initNaviView();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(this.getResources().getColor(R.color.green_theme));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        lv = (ListView) findViewById(R.id.lv);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter = new StarContentAdapter(StarActivity.this, mListItems);
        lv.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        fetchData();
        super.onResume();
    }

    public void fetchData() {
        BmobQuery<UserBean> query = new BmobQuery<UserBean>();
        query.order("-success");
        query.setLimit(Constant.NUMBERS_PER_PAGE);
        BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
        query.addWhereLessThan("createdAt", date);
        query.include("author");

        query.findObjects(StarActivity.this, new FindListener<UserBean>() {

            @Override
            public void onSuccess(List<UserBean> list) {
                mListItems.clear();
                log("find success." + list.size());
                if (list.size() != 0 && list.get(list.size() - 1) != null) {
                    if (list.size() < Constant.NUMBERS_PER_PAGE) {
                        log("已加载完所有数据~");
                    }
                    mListItems.addAll(list);
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    log("暂无更多数据~");
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                log("find failed." + arg1);
            }
        });
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {
                finish();
            }

            @Override
            public void clickRight() {
            }
        };
    }

}
