package com.example.lenovo.murphysl.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.lenovo.murphysl.ImageActivity;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.MyDate;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.event.VetityEvent;
import com.example.lenovo.murphysl.face.FacePPDecet;
import com.example.lenovo.murphysl.face.UploadAvatarActivity;
import com.example.lenovo.murphysl.model.BaseModel;
import com.example.lenovo.murphysl.model.UserModel;
import com.facepp.error.FaceppParseException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


/**
 * UserIdentifyActivity
 *
 * @author: MurphySL
 * @time: 2016/9/24 13:46
 */

public class UserIdentifyActivity extends ParentWithNaviActivity {
    @Bind(R.id.personal_icon_content)
    ImageView personalIconContent;
    @Bind(R.id.personal_job_content)
    ImageView personalJobContent;

    private static final int CODE_PHOTO = 3;
    private static final int MSG_UPLOAD_PHOTO = 4;

    private Bitmap mPhotoImage;
    private String s;

    private final UserBean user = UserModel.getInstance().getUser();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPLOAD_PHOTO:
                    final BmobFile file = new BmobFile(new File(s));
                    file.upload(UserIdentifyActivity.this, new UploadFileListener() {
                        @Override
                        public void onSuccess() {
                            String url = file.getFileUrl(UserIdentifyActivity.this);
                            user.setProve(url);
                            user.update(UserIdentifyActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    toast("原图已上传至云端");
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    log("图片上传失败" + s);
                                }
                            });
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            log("头像上传错误" + s);
                        }
                    });
                    break;

            }

        }
    };

    @Override
    protected String title() {
        return "身份认证";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
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
    }

    @OnClick({R.id.personal_icon, R.id.personal_job})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.personal_icon:
                startActivity(UploadAvatarActivity.class , null);
                break;
            case R.id.personal_job:
                startActivityForResult(new Intent(UserIdentifyActivity.this, ImageActivity.class), CODE_PHOTO);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CODE_PHOTO) {
                List<String> list = data.getStringArrayListExtra("imgs");
                s = list.get(list.size() - 1);
                log("原图文件地址：" + s);
                mPhotoImage = BitmapFactory.decodeFile(s);
                personalJobContent.setImageBitmap(mPhotoImage);
                Message msg = new Message();
                msg.what = MSG_UPLOAD_PHOTO;
                handler.sendMessage(msg);
            }
        }
    }

}
