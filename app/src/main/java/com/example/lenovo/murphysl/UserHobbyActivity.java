package com.example.lenovo.murphysl;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.model.UserModel;
import com.example.lenovo.murphysl.model.i.QueryUserListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * UserHobbyActivity
 *
 * @author: lenovo
 * @time: 2016/8/14 15:31
 */

public class UserHobbyActivity extends ParentWithNaviActivity {

    @Bind(R.id.btn_eat_chinese)
    Button btnEatChinese;
    @Bind(R.id.btn_eat_west)
    Button btnEatWest;
    @Bind(R.id.btn_eat_fast)
    Button btnEatFast;
    @Bind(R.id.btn_eat_coffee)
    Button btnEatCoffee;
    @Bind(R.id.btn_snack)
    Button btnSnack;
    @Bind(R.id.btn_KTV)
    Button btnKTV;
    @Bind(R.id.btn_movie)
    Button btnMovie;
    @Bind(R.id.btn_bar)
    Button btnBar;
    @Bind(R.id.btn_gym)
    Button btnGym;
    @Bind(R.id.btn_internet)
    Button btnInternet;
    @Bind(R.id.btn_zoo)
    Button btnZoo;
    @Bind(R.id.btn_amusement)
    Button btnAmusement;
    @Bind(R.id.btn_park)
    Button btnPark;
    @Bind(R.id.btn_museum)
    Button btnMuseum;
    @Bind(R.id.btn_sight)
    Button btnSight;
    @Bind(R.id.btn_sort_heat)
    Button btnSortHeat;
    @Bind(R.id.btn_sort_distance)
    Button btnSortDistance;
    @Bind(R.id.btn_comfirm)
    Button btnComfirm;

    private String hobby;
    private String sort;

    @Override
    protected String title() {
        return "个人偏好";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_hobby);
        ButterKnife.bind(this);
        initNaviView();

        UserModel.getInstance().queryUserInfo(getCurrentUid(), new QueryUserListener() {
            @Override
            public void done(UserBean s, BmobException e) {
                if(e == null){
                    hobby = s.getHobby();
                    sort = s.getSort();
                    log("hobby" + hobby + "sort" + sort);
                    changeState(hobby);
                    if(sort.equals("热度优先")){
                        btnSortHeat.setBackgroundResource(R.drawable.btn_login_p);
                    }else{
                        btnSortDistance.setBackgroundResource(R.drawable.btn_login_p);
                    }

                }else {
                    log("获取个人偏好错误");
                }
            }

        });

    }

    private void changeState(String activity) {
        List<Button> buttons = new ArrayList<Button>();
        buttons.add(btnAmusement);
        buttons.add(btnBar);
        buttons.add(btnSnack);
        buttons.add(btnKTV);
        buttons.add(btnMovie);
        buttons.add(btnSight);
        buttons.add(btnGym);
        buttons.add(btnInternet);
        buttons.add(btnEatChinese);
        buttons.add(btnEatFast);
        buttons.add(btnZoo);
        buttons.add(btnPark);
        buttons.add(btnMuseum);
        buttons.add(btnEatCoffee);
        buttons.add(btnEatWest);

        if(activity != null){
            for (Button t : buttons) {
                if (activity.equals(t.getText())) {
                    t.setBackgroundResource(R.drawable.btn_login_p);
                    hobby = t.getText().toString();
                    log(hobby);
                } else {
                    t.setBackgroundResource(R.drawable.btn_login_n);
                }
            }
        }else{
            log("按键初始化出错");
        }

    }

    @OnClick({R.id.btn_eat_chinese, R.id.btn_eat_west, R.id.btn_eat_fast, R.id.btn_eat_coffee, R.id.btn_snack, R.id.btn_KTV, R.id.btn_movie, R.id.btn_bar, R.id.btn_gym, R.id.btn_internet, R.id.btn_zoo, R.id.btn_amusement, R.id.btn_park, R.id.btn_museum, R.id.btn_sight, R.id.btn_sort_heat, R.id.btn_sort_distance})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_eat_chinese:
                changeState(btnEatChinese.getText().toString());
                break;
            case R.id.btn_eat_west:
                changeState(btnEatWest.getText().toString());
                break;
            case R.id.btn_eat_fast:
                changeState(btnEatFast.getText().toString());
                break;
            case R.id.btn_eat_coffee:
                changeState(btnEatCoffee.getText().toString());
                break;
            case R.id.btn_snack:
                changeState(btnSnack.getText().toString());
                break;
            case R.id.btn_KTV:
                changeState(btnKTV.getText().toString());
                break;
            case R.id.btn_movie:
                changeState(btnMovie.getText().toString());
                break;
            case R.id.btn_bar:
                changeState(btnBar.getText().toString());
                break;
            case R.id.btn_gym:
                changeState(btnGym.getText().toString());
                break;
            case R.id.btn_internet:
                changeState(btnInternet.getText().toString());
                break;
            case R.id.btn_zoo:
                changeState(btnZoo.getText().toString());
                break;
            case R.id.btn_amusement:
                changeState(btnAmusement.getText().toString());
                break;
            case R.id.btn_park:
                changeState(btnPark.getText().toString());
                break;
            case R.id.btn_museum:
                changeState(btnMuseum.getText().toString());
                break;
            case R.id.btn_sight:
                changeState(btnSight.getText().toString());
                break;
            case R.id.btn_sort_heat:
                btnSortHeat.setBackgroundResource(R.drawable.btn_login_p);
                btnSortDistance.setBackgroundResource(R.drawable.btn_login_n);
                sort = btnSortHeat.getText().toString();
                break;
            case R.id.btn_sort_distance:
                btnSortHeat.setBackgroundResource(R.drawable.btn_login_n);
                btnSortDistance.setBackgroundResource(R.drawable.btn_login_p);
                sort = btnSortDistance.getText().toString();
                break;
        }
    }

    @OnClick(R.id.btn_comfirm)
    public void onClick() {
        UserBean user = UserModel.getInstance().getUser();
        UserBean u = new UserBean();
        log(hobby);
        log(sort);
        u.setHobby(hobby);
        u.setSort(sort);
        u.setObjectId(user.getObjectId());
        u.update(UserHobbyActivity.this , new UpdateListener() {
            @Override
            public void onSuccess() {
                toast("上传偏好成功");
            }

            @Override
            public void onFailure(int i, String s) {
                toast("上传偏好失败"  + "(" + i + ")");
            }
        });
    }
}
