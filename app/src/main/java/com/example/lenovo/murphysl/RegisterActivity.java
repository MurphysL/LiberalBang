package com.example.lenovo.murphysl;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lenovo.murphysl.base.BaseActivity;
import com.example.lenovo.murphysl.event.FinishEvent;
import com.example.lenovo.murphysl.model.BaseModel;
import com.example.lenovo.murphysl.model.UserModel;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.OnClick;
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

    @Bind(R.id.btn_register)
    Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @OnClick(R.id.btn_register)
    public void onRegisterClick(View view){
        UserModel.getInstance().register(et_username.getText().toString(), et_password.getText().toString(),et_password_again.getText().toString(),new LogInListener() {
            @Override
            public void done(Object o, BmobException e) {
                if(e==null){
                    EventBus.getDefault().post(new FinishEvent());
                    Bundle b = new Bundle();
                    b.putInt("fir" , 1);
                    startActivity(LoginActivity.class , b , true);
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
