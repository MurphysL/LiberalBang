package com.example.lenovo.murphysl.view;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.lenovo.murphysl.MainActivity;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.base.BaseActivity;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.event.FinishEvent;
import com.example.lenovo.murphysl.model.BaseModel;
import com.example.lenovo.murphysl.model.UserModel;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * RegisterActivity
 *
 * @author: lenovo
 * @time: 2016/8/12 20:19
 */

public class RegisterActivity extends BaseActivity {

    @Bind(R.id.et_username)
    EditText et_username;
    @Bind(R.id.et_password)
    EditText et_password;
    @Bind(R.id.et_password_again)
    EditText et_password_again;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window window = this.getWindow();
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_register);
    }

    @OnClick(R.id.btn_register)
    public void onRegisterClick(View view){
        UserModel.getInstance().register(et_username.getText().toString(), et_password.getText().toString(),et_password_again.getText().toString(),new LogInListener() {
            @Override
            public void done(Object o, BmobException e) {
                if(e==null){
                    EventBus.getDefault().post(new FinishEvent());
                    UserModel.getInstance().login(et_username.getText().toString(), et_password.getText().toString(), new LogInListener() {

                        @Override
                        public void done(Object o, BmobException e) {
                            if (e == null) {
                                UserBean user = (UserBean) o;
                                BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar()));
                                startActivity(MainActivity.class, null, true);
                                finish();
                            } else {
                                toast(e.getMessage() + "(" + e.getErrorCode() + ")");
                            }
                        }
                    });
                    toast("注册成功");
                }else{
                    if(e.getErrorCode()== BaseModel.CODE_NOT_EQUAL){
                        et_password_again.setText("");
                    }else{
                        toast("注册失败" + e.getMessage()+"("+e.getErrorCode()+")");
                    }
                }
            }
        });
    }

}
