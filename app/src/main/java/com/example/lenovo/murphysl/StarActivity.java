package com.example.lenovo.murphysl;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.Comment;
import com.example.lenovo.murphysl.bean.MyDate;
import com.example.lenovo.murphysl.bean.UserBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

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

    private List<String> userID = new ArrayList<>();
    private HashMap<String , Integer> rank = new HashMap();
    private String[] rList;
    int temp;

    CustomerListCellData[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);

        lv = (ListView) findViewById(R.id.lv);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchData();
    }

    private void fetchData() {
        BmobQuery<MyDate> bmobQuery = new BmobQuery<MyDate>();
        bmobQuery.addQueryKeys("user");
        bmobQuery.findObjects(StarActivity.this , new FindListener<MyDate>() {
            @Override
            public void onSuccess(List<MyDate> list) {
                Iterator<MyDate> i = list.iterator();
                while (i.hasNext()){
                    String id = i.next().getUser().getObjectId();
                    if(userID.size() == 0){
                        userID.add(id);
                    }else{
                        Iterator<String> it = userID.iterator();
                        while (it.hasNext()){
                            if(!id.equals(it.next())){
                                userID.add(id);
                                log("新加入ID" + id);
                                break;
                            }
                        }

                    }

                }
                log("userID人数: " + userID.size());
                temp = userID.size();
                rList = new String[temp];
                data = new CustomerListCellData[temp];
                doRank();
            }

            @Override
            public void onError(int i, String s) {
                log("查询错误");
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BmobQuery<MyDate> query = new BmobQuery<>();
            query.addWhereEqualTo("user", msg.obj);
            query.order("-friend");
            query.include("friend , user");
            query.findObjects(StarActivity.this, new FindListener<MyDate>() {
                @Override
                public void onSuccess(final List<MyDate> list) {
                    rank.put(userID.get(temp - 1) , list.size());
                    temp --;
                    doRank();
                }

                @Override
                public void onError(int i, String s) {
                    toast("初始化出错");
                }
            });

        }
    };

    private void doRank() {
        log("temp");
        if(temp - 1 >= 0 ){
            BmobQuery<UserBean> q = new BmobQuery<>();
            String u = userID.get(temp - 1);
            log("userID" + u);
            q.getObject(StarActivity.this ,u , new GetListener<UserBean>() {
                @Override
                public void onSuccess(UserBean userBean) {
                    Message msg = new Message();
                    msg.obj = userBean;
                    handler.sendMessage(msg);
                }

                @Override
                public void onFailure(int i, String s) {
                    log("读取信息失败");
                }
            });
        }else{
            List<Map.Entry<String, Integer>> infoIds
                    = new ArrayList<Map.Entry<String, Integer>>(rank.entrySet());

            //排序
            Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });

            for(int i = 0 ; i< userID.size();i ++){
                CustomerListCellData c =
                        new CustomerListCellData(infoIds.get(i).getKey() , null , R.drawable.img1 , i);
                data[i] = c ;
                log(data.length + "");
            }
            lv.setAdapter(adapter);

        }
    }

    private BaseAdapter adapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public CustomerListCellData getItem(int i) {
            return data[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LinearLayout ll = null;
            if (view != null) {
                ll = (LinearLayout) view;
            } else {
                ll = (LinearLayout) LayoutInflater.from(StarActivity.this).inflate(R.layout.list_cell, null);
            }

            CustomerListCellData data = getItem(i);

            ImageView icon = (ImageView) ll.findViewById(R.id.icon);
            TextView name = (TextView) ll.findViewById(R.id.name);
            TextView dec = (TextView) ll.findViewById(R.id.dec);

            icon.setImageResource(data.iconId);
            name.setText(data.name);
            dec.setText(data.dec);

            return ll;
        }
    };

    class CustomerListCellData {
        public CustomerListCellData(String name,String dec,int iconId , int rank){
            this.name = name;
            this.dec = dec;
            this.iconId = iconId;
            this.rank = rank;
        }
        public String name = "";
        public String dec ="";
        public int iconId =0;
        public int rank;
    }
}
