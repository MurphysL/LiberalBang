package com.example.lenovo.murphysl;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.SDKInitializer;
import com.example.lenovo.murphysl.base.BaseActivity;
import com.example.lenovo.murphysl.base.ParentWithNaviFragment;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.db.NewFriendManager;
import com.example.lenovo.murphysl.event.RefreshEvent;
import com.example.lenovo.murphysl.fragments.FourthFragment;
import com.example.lenovo.murphysl.fragments.NewFirstFragment;
import com.example.lenovo.murphysl.fragments.SecondFragment;
import com.example.lenovo.murphysl.fragments.ThirdFragment;
import com.example.lenovo.murphysl.ui.ChangeColorIconWithText;
import com.example.lenovo.murphysl.util.IMMLeaks;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * MainActivity
 *
 * @author: lenovo
 * @time: 2016/8/7 21:24
 */

public class MainActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, BackHandledInterface, ObseverListener {

    @Bind(R.id.id_indicator_one)
    ChangeColorIconWithText one;

    @Bind(R.id.id_indicator_two)
    ChangeColorIconWithText two;

    @Bind(R.id.id_indicator_three)
    ChangeColorIconWithText three;

    @Bind(R.id.id_indicator_four)
    ChangeColorIconWithText four;

    @Bind(R.id.iv_conversation_tips)
    ImageView iv_conversation_tips;

    @Bind(R.id.id_viewpager)
    ViewPager id_viewpager;

    private List<ParentWithNaviFragment> fragments = new ArrayList<ParentWithNaviFragment>();

    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();

    private ParentWithNaviFragment mParentWithNaviFragment;

    private FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

       /* if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
*/
        initView();
        initTab();
        initReceiver();
        initListener();
        initIM();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //显示小红点
        checkRedPoint();
        //进入应用后，通知栏应取消
        BmobNotificationManager.getInstance(this).cancelNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BmobIM.getInstance().clear();//清理导致内存泄露的资源
    }

    /**
     * 初始化即时通讯
     */
    private void initIM() {
        UserBean user = BmobUser.getCurrentUser(this, UserBean.class);
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    log("登录成功");
                    EventBus.getDefault().post(new RefreshEvent());//发送一个更新事件，同步更新会话及主页的小红点
                } else {
                    log(e.getErrorCode() + "/" + e.getMessage());
                }
            }
        });
        //监听连接状态，也可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().getCurrentStatus();
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus connectionStatus) {
                toast("" + connectionStatus.getMsg());
            }
        });
        //解决leancanary提示InputMethodManager内存泄露的问题
        IMMLeaks.fixFocusedViewLeak(getApplication());
    }

    /**
     * 初始化广播监听
     */
    private void initReceiver() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
    }

    /**
     * 初始化页面滑动监听器
     */
    private void initListener() {
        id_viewpager.setOnPageChangeListener(this);
    }

    /**
     * 初始化Fragment
     */
    @Override
    protected void initView() {
        super.initView();
        NewFirstFragment f1 = new NewFirstFragment();
        SecondFragment f2 = new SecondFragment();
        ThirdFragment f3 = new ThirdFragment();
        FourthFragment f4 = new FourthFragment();

        fragments.add(f1);
        fragments.add(f2);
        fragments.add(f3);
        fragments.add(f4);

        id_viewpager.setAdapter(adapter);
    }

    /**
     * 初始化控件
     */
    private void initTab() {
        mTabIndicators.add(one);
        mTabIndicators.add(two);
        mTabIndicators.add(three);
        mTabIndicators.add(four);

        if (one != null && two != null && three != null && four != null) {
            one.setOnClickListener(this);
            two.setOnClickListener(this);
            three.setOnClickListener(this);
            four.setOnClickListener(this);
            one.setIconAlpha(1.0f);
        } else {
            log("Tab初始化错误");
        }
    }

    @OnClick({R.id.id_indicator_one, R.id.id_indicator_two, R.id.id_indicator_three, R.id.id_indicator_four})
    public void onClick(View view) {
        resetOtherTabs();
        switch (view.getId()) {
            case R.id.id_indicator_one:
                mTabIndicators.get(0).setIconAlpha(1.0f);
                id_viewpager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_two:
                mTabIndicators.get(1).setIconAlpha(1.0f);
                id_viewpager.setCurrentItem(1, false);
                break;
            case R.id.id_indicator_three:
                mTabIndicators.get(2).setIconAlpha(1.0f);
                id_viewpager.setCurrentItem(2, false);
                break;
            case R.id.id_indicator_four:
                mTabIndicators.get(3).setIconAlpha(1.0f);
                id_viewpager.setCurrentItem(3, false);
                break;
        }
    }

    /**
     * 重置其他TabIndicator颜色
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicators.size(); i++) {
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }

    /**
     * 设置滑动TabIndicator颜色渐变
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0) {
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

    /**
     * Fragment后退键支持
     */
    @Override
    public void onBackPressed() {
        if (mParentWithNaviFragment == null || !mParentWithNaviFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void setSelectedFragment(ParentWithNaviFragment selectedFragment) {
        this.mParentWithNaviFragment = selectedFragment;
    }

    private void checkRedPoint() {
        int count = (int) BmobIM.getInstance().getAllUnReadCount();
        if (count > 0) {
            iv_conversation_tips.setVisibility(View.VISIBLE);
        } else {
            iv_conversation_tips.setVisibility(View.GONE);
        }
        //是否有好友添加的请求
        if (NewFriendManager.getInstance(this).hasNewFriendInvitation()) {
            iv_conversation_tips.setVisibility(View.VISIBLE);
        } else {
            iv_conversation_tips.setVisibility(View.GONE);
        }
    }

    /**
     * 注册消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册自定义消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshEvent event) {
        log("---主页接收到自定义消息---");
        checkRedPoint();
    }

}
