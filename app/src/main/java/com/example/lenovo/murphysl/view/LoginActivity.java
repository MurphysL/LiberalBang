package com.example.lenovo.murphysl.view;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.lenovo.murphysl.MainActivity;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.base.BaseActivity;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.event.FinishEvent;
import com.example.lenovo.murphysl.model.UserModel;

import org.greenrobot.eventbus.Subscribe;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * LoginActivity
 *
 * @author: lenovo
 * @time: 2016/8/11 14:02
 */

public class LoginActivity extends BaseActivity{

    @Bind(R.id.et_username)
    EditText et_username;
    @Bind(R.id.et_password)
    EditText et_password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window window = this.getWindow();
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_login)
    public void onLoginClick(View view) {
        UserModel.getInstance().login(et_username.getText().toString(), et_password.getText().toString(), new LogInListener() {

            @Override
            public void done(Object o, BmobException e) {
                if (e == null) {
                    UserBean user = (UserBean) o;
                    BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar()));
                    startActivity(MainActivity.class, null, true);
                    log("agkaga");
                    finish();
                } else {
                    toast(e.getMessage() + "(" + e.getErrorCode() + ")");
                }
            }
        });
    }

    @OnClick(R.id.tv_register)
    public void onRegisterClick(View view) {
        startActivity(RegisterActivity.class, null, false);
    }

    @Subscribe
    public void onEventMainThread(FinishEvent event) {
        finish();
    }
}
