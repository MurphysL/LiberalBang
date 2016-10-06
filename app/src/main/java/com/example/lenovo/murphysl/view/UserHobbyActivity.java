package com.example.lenovo.murphysl.view;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.lenovo.murphysl.R;
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
    @Bind(R.id.btn_eat_coffee)
    Button btnEatCoffee;
    @Bind(R.id.btn_eat_fast)
    Button btnEatFast;
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
    @Bind(R.id.basketball)
    Button basketball;
    @Bind(R.id.football)
    Button football;
    @Bind(R.id.badminton)
    Button badminton;
    @Bind(R.id.tableTennis)
    Button tableTennis;
    @Bind(R.id.volleyball)
    Button volleyball;
    @Bind(R.id.textView)
    EditText textView;
    private String hobby;

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

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(this.getResources().getColor(R.color.green_theme));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserModel.getInstance().queryUserInfo(getCurrentUid(), new QueryUserListener() {
            @Override
            public void done(UserBean s, BmobException e) {
                if (e == null) {
                    hobby = s.getHobby();
                    log("hobby" + hobby);
                    changeState(hobby);
                } else {
                    log("获取个人偏好错误");
                }
            }

        });

    }

    private void changeState(String hobby) {
        List<Button> buttons = new ArrayList<Button>();
        buttons.add(volleyball);
        buttons.add(tableTennis);
        buttons.add(badminton);
        buttons.add(football);
        buttons.add(basketball);
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
        if (hobby != null) {
            for (Button t : buttons) {
                if (hobby.equals(t.getText())) {
                    t.setBackgroundResource(R.color.green_theme3);
                    this.hobby = t.getText().toString();
                    log(this.hobby);
                } else {
                    t.setBackgroundResource(R.color.green_theme);
                }
            }
        } else {
            log("按键初始化出错");
        }
    }

    @OnClick({R.id.btn_eat_chinese, R.id.btn_eat_west, R.id.btn_eat_coffee, R.id.btn_eat_fast, R.id.btn_snack, R.id.btn_KTV, R.id.btn_movie, R.id.btn_bar, R.id.btn_gym, R.id.btn_internet, R.id.btn_zoo, R.id.btn_amusement, R.id.btn_park, R.id.btn_museum, R.id.btn_sight, R.id.basketball, R.id.football, R.id.badminton, R.id.tableTennis, R.id.volleyball, R.id.btn_comfirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_eat_chinese:
                hobby = btnEatChinese.getText().toString();
                changeState(hobby);
                break;
            case R.id.btn_eat_west:
                hobby = btnEatWest.getText().toString();
                changeState(hobby);
                break;
            case R.id.btn_eat_coffee:
                hobby = btnEatCoffee.getText().toString();
                changeState(hobby);
                break;
            case R.id.btn_eat_fast:
                hobby = btnEatFast.getText().toString();
                changeState(hobby);
                break;
            case R.id.btn_snack:
                hobby = btnSnack.getText().toString();
                changeState(hobby);
                break;
            case R.id.btn_KTV:
                hobby = btnKTV.getText().toString();
                changeState(hobby);
                break;
            case R.id.btn_movie:
                hobby = btnMovie.getText().toString();
                changeState(hobby);
                break;
            case R.id.btn_bar:
                hobby = btnBar.getText().toString();
                changeState(hobby);
                break;
            case R.id.btn_gym:
                hobby = btnGym.getText().toString();
                changeState(hobby);
                break;
            case R.id.btn_internet:
                hobby = btnInternet.getText().toString();
                changeState(hobby);
                break;
            case R.id.btn_zoo:
                hobby = btnZoo.getText().toString();
                changeState(hobby);
                break;
            case R.id.btn_amusement:
                hobby = btnAmusement.getText().toString();
                changeState(hobby);
                break;
            case R.id.btn_park:
                hobby = btnPark.getText().toString();
                changeState(hobby);
                break;
            case R.id.btn_museum:
                hobby = btnMuseum.getText().toString();
                changeState(hobby);
                break;
            case R.id.btn_sight:
                hobby = btnSight.getText().toString();
                changeState(hobby);
                break;
            case R.id.basketball:
                hobby = basketball.getText().toString();
                changeState(hobby);
                break;
            case R.id.football:
                hobby = football.getText().toString();
                changeState(hobby);
                break;
            case R.id.badminton:
                hobby = badminton.getText().toString();
                changeState(hobby);
                break;
            case R.id.tableTennis:
                hobby = tableTennis.getText().toString();
                changeState(hobby);
                break;
            case R.id.volleyball:
                hobby = volleyball.getText().toString();
                changeState(hobby);
                break;
            case R.id.btn_comfirm:
                UserBean user = UserModel.getInstance().getUser();
                if(textView.getText().toString() != null && !textView.getText().toString().equals("")){
                    log(textView.getText().toString());
                    user.setHobby(textView.getText().toString());
                }else{
                    log(hobby);
                }

                user.setHobby(hobby);
                user.update(UserHobbyActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        toast("上传偏好成功："  +hobby);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        toast("上传偏好失败" + "(" + i + ")" + s);
                    }
                });

                break;
        }
    }
}
