package com.example.lenovo.murphysl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.SDKInitializer;
import com.example.lenovo.murphysl.Fragments.FirstFragment;
import com.example.lenovo.murphysl.Fragments.FourthFragment;
import com.example.lenovo.murphysl.Fragments.ThirdFragment;
import com.example.lenovo.murphysl.Fragments.TestFTwo;
import com.example.lenovo.murphysl.Map.LocationOption;
import com.example.lenovo.murphysl.Map.NotifyActivity;
import com.example.lenovo.murphysl.UI.ChangeColorIconWithText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , ViewPager.OnPageChangeListener ,BackHandledInterface {
    private static final String TAG = "MainActivity";

    private SDKReceiver mReceiver;

    private ViewPager mViewPager;
    private boolean[] fragmentsUpdateFlag = { false, false, false, false };
    private List<Fragment> list = new ArrayList<Fragment>();

    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();

    private BackHandledFragment mBackHandedFragment;

   /* private Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(FirstFragment.flag){
                adapter.notifyDataSetChanged();
                handler.postDelayed(runnable , 500);
            }
        }
    };*/

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    fragmentsUpdateFlag[0] = true;
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    fragmentsUpdateFlag[0] = true;
                    adapter.notifyDataSetChanged();
                    break;
                case 3:
                    fragmentsUpdateFlag[2] = true;
                    adapter.notifyDataSetChanged();
                    break;
                case 4:
                    fragmentsUpdateFlag[2] = true;
                    adapter.notifyDataSetChanged();
                    break;
                default:
            }
        }
    };

    /**
     * 监听异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            Log.i(TAG, "action: " + s);
            if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Log.i(TAG , "网络异常");
            }
        }
    }

    private FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = list.get(position);
            Log.i(TAG, "getItem:position=" + position + ",fragment:" + fragment.getClass().getName() + ",fragment.tag=" + fragment.getTag());
            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //得到缓存的Fragment
            Fragment f = (Fragment) super.instantiateItem(container , position);
            String Tag = f.getTag();

            //如果这个fragment需要更新
            if (fragmentsUpdateFlag[position]) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.remove(f);//移除旧的fragment
                f = list.get(position);//换成新的fragment
                ft.add(container.getId(), f, Tag);//添加新fragment时必须用前面获得的tag，这点很重要
                ft.attach(f);
                ft.commit();

                fragmentsUpdateFlag[position] = false;//复位更新标志
            }

            return f;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return list.size();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initView();
        initFragments();
        initReceiver();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /**
     * 初始化广播监听
     */
    private void initReceiver() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
    }

    /**
     * 初始化页面滑动监听器
     */
    private void initEvent() {
        mViewPager.setOnPageChangeListener(this);
    }

    /**
     * 初始化Fragment
     */
    private void initFragments() {
        FirstFragment f1 = new FirstFragment();
        list.add(f1);
        TestFTwo f2 = new TestFTwo();
        list.add(f2);
        ThirdFragment f3 = new ThirdFragment();
        list.add(f3);
        FourthFragment f4 = new FourthFragment();
        list.add(f4);

        mViewPager.setAdapter(adapter);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        ChangeColorIconWithText one = (ChangeColorIconWithText) findViewById(R.id.id_indicator_one);
        mTabIndicators.add(one);
        ChangeColorIconWithText two = (ChangeColorIconWithText) findViewById(R.id.id_indicator_two);
        mTabIndicators.add(two);
        ChangeColorIconWithText three = (ChangeColorIconWithText) findViewById(R.id.id_indicator_three);
        mTabIndicators.add(three);
        ChangeColorIconWithText four = (ChangeColorIconWithText) findViewById(R.id.id_indicator_four);
        mTabIndicators.add(four);

        if(one != null && two != null && three != null && four != null){
            one.setOnClickListener(this);
            two.setOnClickListener(this);
            three.setOnClickListener(this);
            four.setOnClickListener(this);
            one.setIconAlpha(1.0f);
        }else{
            Log.i(TAG , "底部导航初始化错误");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Class<?> TargetClass = null;
        switch (item.getItemId()){
            case R.id.action_map:
                TargetClass = MapActivity.class;
                break;
            case R.id.action_map_option:
                TargetClass = LocationOption.class;
                break;
            case R.id.action_map_notify:
                TargetClass = NotifyActivity.class;
                break;
        }
        if (TargetClass != null) {
            Intent intent = new Intent(MainActivity.this, TargetClass);
            intent.putExtra("from", 0);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        resetOtherTabs();
        switch (v.getId()){
            case R.id.id_indicator_one:
                mTabIndicators.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0 , false);
                break;
            case R.id.id_indicator_two:
                mTabIndicators.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1 , false);
                break;
            case R.id.id_indicator_three:
                mTabIndicators.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2 , false);
                break;
            case R.id.id_indicator_four:
                mTabIndicators.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3 , false);
                break;
        }
    }

    /**
     * 重置其他TabIndicator颜色
     */
    private void resetOtherTabs() {
        for(int i = 0 ;i < mTabIndicators.size() ; i ++){
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }

    /**
     * 设置滑动TabIndicator颜色渐变
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(positionOffset > 0){
            ChangeColorIconWithText left = mTabIndicators.get(position);
            ChangeColorIconWithText right = mTabIndicators.get(position + 1);

            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        if(mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()){
            if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                super.onBackPressed();
            }else{
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

}
