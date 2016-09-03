package com.example.lenovo.test;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用刷新数据
 */
public class MainActivity extends AppCompatActivity implements BackHandledInterface{
    private static final String TAG = "MAIN";

    private ViewPager viewPager;
    private List<Fragment> list;
    private Fragment f1 , f2 , f3 , f4;
    private boolean[] fragmentsUpdateFlag = { false, false, false, false };

    private BackHandledFragment mBackHandedFragment;
    //private boolean hadIntercept;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    fragmentsUpdateFlag[0] = true;
                    adapter.notifyDataSetChanged();
                    break;
                case 3:
                    fragmentsUpdateFlag[2] = true;
                    adapter.notifyDataSetChanged();
                    break;
                default:
            }
        }
    };

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
        list = new ArrayList<Fragment>();

        initView();
    }

    private void initView(){
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        f1 = new Fragment1();
        f2 = new Fragment2();
        f3 = new Fragment3();
        f4 = new Fragment4();
        list.add(f1);
        list.add(f2);
        list.add(f3);
        list.add(f4);

        viewPager.setAdapter(adapter);
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
        Log.i(TAG , "SET");
        this.mBackHandedFragment = selectedFragment;
    }
}
