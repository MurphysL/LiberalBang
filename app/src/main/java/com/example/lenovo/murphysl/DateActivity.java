package com.example.lenovo.murphysl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.MyDate;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.face.FacePPDecet;
import com.facepp.error.FaceppParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * DateActivity
 *
 * @author: lenovo
 * @time: 2016/8/26 13:47
 */

public class DateActivity extends ParentWithNaviActivity {

    @Bind(R.id.iv)
    ImageView iv;
    /*@Bind(R.id.date_progress)
    ProgressBar dateProgress;*/
    /*@Bind(R.id.date_progress)
    ProgressBar dateProgress;*/
    @Bind(R.id.framelayout)
    FrameLayout framelayout;
    @Bind(R.id.age_gender)
    TextView ageGender;

    private String aroundID;
    private UserBean user;
    private Bitmap mPhotoImage;
    private Paint mPaint;

    private String address;
    private static final int CODE_CAMERA = 1;
    private static final int CODE_QUERY = 0;
    private static final int CODE_IDENTIFY = 2;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CODE_QUERY) {
                Bundle bundle = getIntent().getBundleExtra("com.example.lenovo.murphysl");
                aroundID = bundle.getString("ID");
                log("aroundID" + aroundID);

                BmobQuery<UserBean> query = new BmobQuery<>();
                query.getObject(DateActivity.this, aroundID, new GetListener<UserBean>() {
                    @Override
                    public void onSuccess(UserBean userBean) {
                        user = userBean;
                        log("附近的人失败");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        log("附近的人失败");
                    }
                });
            } else if (msg.what == CODE_IDENTIFY) {
                FacePPDecet.decet(mPhotoImage, new FacePPDecet.CallBack() {
                    @Override
                    public void success(JSONObject result) {
                        try {
                            JSONArray faces = result.getJSONArray("face");
                            int faceCount = faces.length();
                            for (int i = 0; i < faceCount; i++) {
                                //得到单独face对象
                                JSONObject face = faces.getJSONObject(i);
                                final String gender = face.getJSONObject("attribute").getJSONObject("gender").getString("value");
                                String name = face.getString("face_id");
                                FacePPDecet.identify(name, new FacePPDecet.CallBack() {
                                    @Override
                                    public void success(JSONObject result) {
                                        prepareRsBitmap(result , gender);
                                    }

                                    @Override
                                    public void error(FaceppParseException e) {

                                    }
                                });

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void error(FaceppParseException e) {

                    }
                });
                /*FacePPDecet.identify(mPhotoImage, new FacePPDecet.CallBack() {
                    @Override
                    public void success(JSONObject result) {
                        prepareRsBitmap(result);
                    }

                    @Override
                    public void error(FaceppParseException e) {

                    }
                });*/

            }

        }
    };


    @Override
    protected String title() {
        return "开始聚会";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        ButterKnife.bind(this);

        address = Environment.getExternalStorageDirectory().getPath() +
                "/" + new Date(System.currentTimeMillis()).getTime() + ".png";

        Message message = new Message();
        message.what = CODE_QUERY;
        handler.sendMessage(message);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @OnClick(R.id.start_photo)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_photo:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri = Uri.fromFile(new File(address));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, CODE_CAMERA);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        log(1 + "");
        //dateProgress.setVisibility(View.VISIBLE);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CODE_CAMERA) {
                final BmobFile bmobFile = new BmobFile(new File(address));

                mPhotoImage = BitmapFactory.decodeFile(address);
                iv.setImageBitmap(mPhotoImage);
                Message msg = new Message();
                msg.what = CODE_IDENTIFY;
                handler.sendMessage(msg);

                bmobFile.upload(this, new UploadFileListener() {
                    @Override
                    public void onSuccess() {
                        log("success");
                        MyDate datePhoto = new MyDate();
                        datePhoto.setPhoto(bmobFile);
                        datePhoto.setUser(BmobUser.getCurrentUser(DateActivity.this, UserBean.class));
                        datePhoto.setFriend(user);
                        datePhoto.save(DateActivity.this, new SaveListener() {
                            @Override
                            public void onSuccess() {
                                toast("上传照片成功");
                                //dateProgress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                toast("上传照片失败");
                            }
                        });
                    }

                    @Override
                    public void onProgress(Integer value) {
                        super.onProgress(value);
                        //dateProgress.setProgress(value);
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });

            }
        }
    }

    /**
     * 解析数据
     *
     * @param rs
     * @param gender
     */
    private void prepareRsBitmap(JSONObject rs, String gender) {

        Bitmap bitmap = Bitmap.createBitmap(mPhotoImage.getWidth(), mPhotoImage.getHeight(), mPhotoImage.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(mPhotoImage, 0, 0, null);

        try {
            JSONObject faceInfo = rs.getJSONObject("face");
            JSONObject posObj = faceInfo.getJSONObject("position");
            JSONArray cans = faceInfo.getJSONArray("candidate");
            Double temp = 0.0;
            int pos = 0;
            for(int t = 0 ; t < cans.length() ; t ++){
                JSONObject can = cans.getJSONObject(t);
                Double c = can.getDouble("confidence");
                if(c > temp){
                    temp = c;
                    pos = t;
                }
            }

            String name = cans.getJSONObject(pos).getString("person_name");

            float x = (float) posObj.getJSONObject("center").getDouble("x");
            float y = (float) posObj.getJSONObject("center").getDouble("y");

            float w = (float) posObj.getDouble("width");
            float h = (float) posObj.getDouble("height");

            //百分比转为像素值
            x = x / 100 * bitmap.getWidth();
            y = y / 100 * bitmap.getHeight();

            w = w / 100 * bitmap.getWidth();
            h = h / 100 * bitmap.getHeight();

            mPaint = new Paint();
            mPaint.setColor(0xffffffff);
            mPaint.setStrokeWidth(3);

            //画BOX
            canvas.drawLine(x - w / 2, y - h / 2, x - w / 2, y + h / 2, mPaint);
            canvas.drawLine(x - w / 2, y - h / 2, x + w / 2, y - h / 2, mPaint);
            canvas.drawLine(x + w / 2, y - h / 2, x + w / 2, y + h / 2, mPaint);
            canvas.drawLine(x - w / 2, y + h / 2, x + w / 2, y + h / 2, mPaint);

            Bitmap ageBitmap = buildNameBitmap(name, "Male".equals(gender));

            //缩放
            int ageWidth = ageBitmap.getWidth();
            int ageHeight = ageBitmap.getHeight();
            if (bitmap.getWidth() < iv.getWidth() && bitmap.getHeight() < iv.getHeight()) {
                float ratio = Math.max(bitmap.getWidth() * 1.0f / iv.getWidth(), bitmap.getHeight() * 1.0f / iv.getHeight());
                ageBitmap = Bitmap.createScaledBitmap(ageBitmap, (int) (ageWidth * ratio), (int) (ageHeight * ratio), false);

            }
            canvas.drawBitmap(ageBitmap, x - ageBitmap.getWidth() / 2, y - h / 2 - ageBitmap.getHeight(), null);
            mPhotoImage = bitmap;

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private Bitmap buildNameBitmap(String name, boolean isMale) {

        TextView tv = (TextView) framelayout.findViewById(R.id.age_gender);
        ageGender.setText(name);
        if (isMale) {
            ageGender.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.male), null, null, null);
        } else {
            ageGender.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.female), null, null, null);
        }
        ageGender.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(ageGender.getDrawingCache());
        ageGender.destroyDrawingCache();
        return bitmap;
    }


    @OnClick(R.id.look_photo)
    public void onClickLookPhoto() {
        final String add = Environment.getExternalStorageDirectory().getPath() + "/" + "1" + ".png";
        BmobQuery<MyDate> query = new BmobQuery<MyDate>();
        MyDate date = new MyDate();
        date.setUser(BmobUser.getCurrentUser(DateActivity.this, UserBean.class));
        query.order("-createdAt");
        query.addWhereEqualTo("user", BmobUser.getCurrentUser(DateActivity.this, UserBean.class));
        query.findObjects(DateActivity.this, new FindListener<MyDate>() {

            @Override
            public void onSuccess(List<MyDate> list) {
                log("url" + list.get(0).getPhoto().getFileUrl(DateActivity.this));
                BmobFile bmobFile = new BmobFile(add, "", list.get(0).getPhoto().getFileUrl(DateActivity.this));
                bmobFile.download(DateActivity.this, new DownloadFileListener() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        toast("开始加载");
                    }

                    @Override
                    public void onSuccess(String s) {
                        log("加载成功");
                        log(add);
                        Bitmap bt = BitmapFactory.decodeFile(add);
                        if (bt != null) {
                            iv.setImageBitmap(BitmapFactory.decodeFile(add));
                        } else {
                            log("图片为空" + s);

                            iv.setImageBitmap(BitmapFactory.decodeFile(s));
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }

            @Override
            public void onError(int arg0, String arg1) {
                // TODO 自动生成的方法存根

            }
        });


    }


}
