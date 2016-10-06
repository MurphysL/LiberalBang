package com.example.lenovo.murphysl.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.adapter.AIContentAdapter;
import com.example.lenovo.murphysl.adapter.HistoryContentAdapter;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.QiangYu;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.model.UserModel;
import com.example.lenovo.murphysl.moment.CommentActivity;
import com.example.lenovo.murphysl.moment.EditActivity;
import com.example.lenovo.murphysl.util.Constant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

/**
 * UserHistoryActivity
 *
 * @author: MurphySL
 * @time: 2016/9/20 16:45
 */

public class UserHistoryActivity extends ParentWithNaviActivity {

    @Bind(R.id.context_listview)
    ListView contextListview;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout swRefresh;

    private int currentIndex;
    private int pageNum;
    private String lastItemTime;//当前列表结尾的条目的创建时间，
    private UserBean user = UserModel.getInstance().getUser();


    private ArrayList<QiangYu> mListItems = new ArrayList<QiangYu>();
    private HistoryContentAdapter mAdapter;


    @Override
    protected String title() {
        return "历史";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
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
        ButterKnife.bind(this);
    }

    @Override
    public void onResume() {
        fetchData();
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        initListView();
        swRefresh.setEnabled(true);
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });
    }

    private void initListView() {
        mAdapter = new HistoryContentAdapter(UserHistoryActivity.this, mListItems);
        contextListview.setAdapter(mAdapter);
        contextListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                log("position" + position);
                Intent intent = new Intent();
                intent.setClass(UserHistoryActivity.this, CommentActivity.class);
                intent.putExtra("data", mListItems.get(position));
                startActivity(intent);
            }
        });
    }

    public void fetchData() {
        BmobQuery<QiangYu> query = new BmobQuery<QiangYu>();
        query.order("-createdAt");
        query.setLimit(Constant.NUMBERS_PER_PAGE);
        BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
        query.order("-love");
        query.addWhereLessThan("createdAt", date);
        log("SIZE:" + Constant.NUMBERS_PER_PAGE * pageNum);
        query.include("author");
        query.addWhereEqualTo("author", user);
        if(user == null) {
            log("whatfuck");
        }
        query.findObjects(UserHistoryActivity.this, new FindListener<QiangYu>() {

            @Override
            public void onSuccess(List<QiangYu> list) {
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
                    pageNum--;
                }
                swRefresh.setRefreshing(false);
            }

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                log("find failed." + arg1);
                swRefresh.setRefreshing(false);
                pageNum--;
            }
        });
    }

    @Override
    public Object right() {
        return R.drawable.base_action_bar_add_bg_selector;
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {

            }

            @Override
            public void clickRight() {
                startActivity(EditActivity.class, null);
            }
        };
    }
}
