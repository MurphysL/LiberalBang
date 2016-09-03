package com.example.lenovo.murphysl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.lenovo.murphysl.base.BaseActivity;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.event.FinishEvent;
import com.example.lenovo.murphysl.face.FacePPDecet;
import com.example.lenovo.murphysl.model.UserModel;
import com.facepp.error.FaceppParseException;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * LoginActivity
 *
 * @author: lenovo
 * @time: 2016/8/11 14:02
 */
public class LoginActivity extends BaseActivity {

    @Bind(R.id.et_username)
    EditText et_username;

    @Bind(R.id.et_password)
    EditText et_password;
    @Bind(R.id.iv_icon)
    ImageView ivIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    Bundle b = getIntent().getBundleExtra("com.example.lenovo.murphysl");
                    if(b != null && b.getInt("fir") == 1){
                        startActivity(UploadAvatarActivity.class , null , true);
                    }else{
                        startActivity(MainActivity.class , null , true);
                    }
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
