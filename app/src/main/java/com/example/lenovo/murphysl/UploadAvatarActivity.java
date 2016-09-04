package com.example.lenovo.murphysl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.murphysl.base.BaseActivity;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.face.FacePPDecet;
import com.example.lenovo.murphysl.model.UserModel;
import com.example.lenovo.murphysl.util.BitmapCut;
import com.facepp.error.FaceppParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * UploadAvatarActivity
 *
 * @author: lenovo
 * @time: 2016/9/2 17:04
 */

public class UploadAvatarActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.get_image)
    Button getImage;
    @Bind(R.id.decet)
    Button btdecet;
    @Bind(R.id.get_upload)
    Button getUpload;
    private Button get_image;
    private Button decet;
    private ImageView imageView;
    private View waitting;

    private Bitmap mPhotoImage;
    private Paint mPaint;

    private UserBean user = UserModel.getInstance().getUser();

    private final String avatar = Environment.getExternalStorageDirectory().getPath() + "/" + user + ".png";//修改图片地址

    private String url;//原始图片URL
    private String newUrl;//头像URL

    private static final int CODE_IMAGE = 1;
    private static final int MSG_SUCCESS = 0X111;
    private static final int MSG_ERROR = 0X112;
    private static final int MSG_TARIN = 0X113;
    private static final int MSG_UPLOAD_PHOTO = 0X114;
    private static final int MSG_DECET = 0X115;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        ButterKnife.bind(this);
        init();
    }

    /**
     * 初始化控件
     */
    private void init() {
        get_image = (Button) findViewById(R.id.get_image);
        decet = (Button) findViewById(R.id.decet);
        decet.setOnClickListener(this);
        get_image.setOnClickListener(this);
        waitting = findViewById(R.id.framelayout);
        imageView = (ImageView) findViewById(R.id.mimage);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_SUCCESS:
                    waitting.setVisibility(View.GONE);
                    JSONObject rs = (JSONObject) msg.obj;
                    recognitionFaceNum(rs);
                    break;
                case MSG_ERROR:
                    waitting.setVisibility(View.GONE);
                    String errorMsg = (String) msg.obj;

                    if (TextUtils.isEmpty(errorMsg)) {
                        toast("错误");
                    } else {
                        toast(errorMsg);
                    }
                    break;
                case MSG_TARIN:
                    FacePPDecet.train(UserModel.getInstance().getUser().getUsername(), new FacePPDecet.CallBack() {
                        @Override
                        public void success(JSONObject result) {
                            log("训练完成");
                            startActivity(MainActivity.class, null, true);
                            finish();
                        }

                        @Override
                        public void error(FaceppParseException e) {
                            log("训练失败");
                        }
                    });
                    break;
                case MSG_UPLOAD_PHOTO:
                    final BmobFile file = new BmobFile(new File(avatar));
                    file.upload(UploadAvatarActivity.this, new UploadFileListener() {
                        @Override
                        public void onSuccess() {
                            newUrl = file.getUrl();
                            user.setPic(file);
                            user.setAvatar(newUrl);
                            log("newUrl:" + newUrl);
                            user.update(UploadAvatarActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    toast("头像上传成功");
                                    log("头像上传成功");
                                    waitting.setVisibility(View.GONE);
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    toast("头像上传失败" + s);
                                    waitting.setVisibility(View.GONE);
                                }
                            });
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            log("头像上传错误" + s);
                            toast("头像上传失败" + s);
                        }
                    });

                    break;
                case MSG_DECET:
                    FacePPDecet.decet(url, new FacePPDecet.CallBack() {
                        @Override
                        public void success(JSONObject result) {
                            Message message = Message.obtain();
                            message.what = MSG_SUCCESS;
                            message.obj = result;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void error(FaceppParseException e) {
                            Message message = Message.obtain();
                            message.what = MSG_ERROR;
                            message.obj = e.getErrorMessage();
                            handler.sendMessage(message);
                        }
                    });
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Bitmap cutRsBitmap(Bitmap bm, JSONObject rs) {
        Bitmap b = null;
        try {
            JSONArray faces = rs.getJSONArray("face");
            JSONObject face = faces.getJSONObject(0);
            double width = face.getJSONObject("position").getDouble("width");
            double height = face.getJSONObject("position").getDouble("height");
            double centerX = face.getJSONObject("position").getJSONObject("center").getDouble("x");
            double centerY = face.getJSONObject("position").getJSONObject("center").getDouble("y");
            b = BitmapCut.ImageCrop(bm, width, height, centerX, centerY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 识别人脸数
     *
     * @param rs
     */
    private void recognitionFaceNum(JSONObject rs) {
        Bitmap bitmap = Bitmap.createBitmap(mPhotoImage.getWidth(), mPhotoImage.getHeight(), mPhotoImage.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(mPhotoImage, 0, 0, null);
        try {
            JSONArray faces = rs.getJSONArray("face");
            int faceCount = faces.length();
            log("faceCount：" + faceCount);

            if (faceCount == 1) {
                String name = UserModel.getInstance().getUser().getUsername();
                log("User name：" + name);

                FacePPDecet.createPerson(rs, name, new FacePPDecet.CallBack() {
                    @Override
                    public void success(JSONObject result) {
                        try {
                            log("person name : " + result.getString("person_name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void error(FaceppParseException e) {
                        log("添加Person失败");
                    }
                });

                Bitmap bm = cutRsBitmap(bitmap, rs);

                mPhotoImage = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight());

                File f = new File(avatar);
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
                    bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    bos.flush();
                    bos.close();

                    waitting.setVisibility(View.VISIBLE);
                    Message msg = new Message();
                    msg.what = MSG_UPLOAD_PHOTO;
                    handler.sendMessage(msg);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                getImage.setVisibility(View.GONE);
                btdecet.setVisibility(View.GONE);
                getUpload.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(mPhotoImage);

            } else {
                mPhotoImage = null;
                toast("人数错误");
                log("人数错误");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        prepareRsBitmap(rs, bitmap, canvas);
    }

    /**
     * 解析数据
     *
     * @param rs
     */
    private void prepareRsBitmap(JSONObject rs, Bitmap bitmap, Canvas canvas) {
        try {
            JSONArray faces = rs.getJSONArray("face");

            int faceCount = faces.length();

            for (int i = 0; i < faceCount; i++) {
                //得到单独face对象
                JSONObject face = faces.getJSONObject(i);
                JSONObject posObj = face.getJSONObject("position");

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

                int age = face.getJSONObject("attribute").getJSONObject("age").getInt("value");
                String gender = face.getJSONObject("attribute").getJSONObject("gender").getString("value");

                Bitmap ageBitmap = buildAgeBitmap(age, "Male".equals(gender));

                //缩放
                int ageWidth = ageBitmap.getWidth();
                int ageHeight = ageBitmap.getHeight();
                if (bitmap.getWidth() < imageView.getWidth() && bitmap.getHeight() < imageView.getHeight()) {
                    float ratio = Math.max(bitmap.getWidth() * 1.0f / imageView.getWidth(), bitmap.getHeight() * 1.0f / imageView.getHeight());
                    ageBitmap = Bitmap.createScaledBitmap(ageBitmap, (int) (ageWidth * ratio), (int) (ageHeight * ratio), false);

                }
                canvas.drawBitmap(ageBitmap, x - ageBitmap.getWidth() / 2, y - h / 2 - ageBitmap.getHeight(), null);
                mPhotoImage = bitmap;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private Bitmap buildAgeBitmap(int age, boolean isMale) {

        TextView tv = (TextView) waitting.findViewById(R.id.age_gender);
        tv.setText(age + "");
        if (isMale) {
            tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.male), null, null, null);
        } else {
            tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.female), null, null, null);
        }
        tv.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(tv.getDrawingCache());
        tv.destroyDrawingCache();
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_IMAGE) {
            if (data != null) {
                waitting.setVisibility(View.VISIBLE);

                List<String> list = data.getStringArrayListExtra("imgs");
                String address = list.get(list.size() - 1);
                mPhotoImage = BitmapFactory.decodeFile(address);
                imageView.setImageBitmap(mPhotoImage);

                upLoadPhoto(address);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 上传原图片至Bmob
     *
     * @param s
     */
    private BmobFile upLoadPhoto(final String s) {
        log("原始图片地址:" + s);
        final BmobFile file = new BmobFile(new File(s));

        file.upload(UploadAvatarActivity.this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                url = file.getFileUrl(UploadAvatarActivity.this);
                toast("原图已上传至云端");
                log("头像原图上传成功 URL" + url);
                waitting.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int i, String s) {
                log("头像原图上传错误" + s);
            }
        });
        return file;
    }

    @OnClick({R.id.get_image, R.id.decet, R.id.get_upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_image:
                startActivityForResult(new Intent(this, ImageActivity.class), CODE_IMAGE);
                break;
            case R.id.decet:
                waitting.setVisibility(View.VISIBLE);
                Message msg = new Message();
                msg.what = MSG_DECET;
                handler.sendMessage(msg);
                break;
            case R.id.get_upload:
                Message msg2 = new Message();
                msg2.what = MSG_TARIN;
                handler.sendMessage(msg2);
                break;
        }
    }
}

