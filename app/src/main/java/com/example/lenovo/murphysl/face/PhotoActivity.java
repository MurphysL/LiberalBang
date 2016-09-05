package com.example.lenovo.murphysl.face;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.widget.TextView;

import com.example.lenovo.murphysl.ImageActivity;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.model.UserModel;
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
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * PhotoActivity
 *
 * @author: lenovo
 * @time: 2016/9/3 17:49
 */

public class PhotoActivity extends ParentWithNaviActivity {


    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.framelayout)
    FrameLayout framelayout;

    private static final int CODE_CAMERA = 1;
    private static final int MSG_IDENTIFY = 2;
    private static final int CODE_PHOTO = 3;
    private static final int MSG_UPLOAD_PHOTO = 4;

    private final UserBean user = UserModel.getInstance().getUser();

    private Bitmap mPhotoImage;
    private String s;
    private String address = Environment.getExternalStorageDirectory().getPath() +
            "/" + new Date(System.currentTimeMillis()).getTime() + ".png";
    private String url;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_IDENTIFY:
                    log("辨识成功");
                    prepareRsBitmap((JSONObject) msg.obj, msg.getData().getString("gender"));
                    image.setImageBitmap(mPhotoImage);
                    /**
                     * 在此处加入相片分发
                     */
                    break;
                case MSG_UPLOAD_PHOTO:
                    final BmobFile file = new BmobFile(new File(s));
                    file.upload(PhotoActivity.this, new UploadFileListener() {
                        @Override
                        public void onSuccess() {
                            user.setPic(file);
                            user.update(PhotoActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    url = file.getFileUrl(PhotoActivity.this);
                                    toast("原图已上传至云端");
                                    log("头像原图上传成功 URL" + url);
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
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);
    }

    /**
     * 解析数据
     *
     * @param rs
     * @param gender
     */
    private void prepareRsBitmap(JSONObject rs, String gender) {
        //将原图绘制在Bitmap上
        Bitmap bitmap = Bitmap.createBitmap(mPhotoImage.getWidth(), mPhotoImage.getHeight(), mPhotoImage.getConfig());
        log("width" + mPhotoImage.getWidth() + "height" + mPhotoImage.getHeight());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(mPhotoImage, 0, 0, null);

        try {
            log(rs.toString());
            JSONArray faces = rs.getJSONArray("face");
            int faceNum = faces.length() - 1;
            if(faceNum < 0){
                toast("不存在已登记人脸");
            }else{

                JSONObject face = faces.getJSONObject(faceNum);
                JSONArray candidate = face.getJSONArray("candidate");
                JSONObject posObj = face.getJSONObject("position");//人脸位置
                Double temp = 0.0;
                int pos = 0;
                for (int i = 0; i < candidate.length(); i++) {
                    JSONObject confidences = candidate.getJSONObject(i);
                    Double confidence = confidences.getDouble("confidence");
                    if (temp < confidence) {
                        temp = confidence;
                        pos = i;
                    }
                }

                String name = candidate.getJSONObject(pos).getString("person_name");

                float x = (float) posObj.getJSONObject("center").getDouble("x");
                float y = (float) posObj.getJSONObject("center").getDouble("y");

                float w = (float) posObj.getDouble("width");
                float h = (float) posObj.getDouble("height");

                //百分比转为像素值
                x = x / 100 * bitmap.getWidth();
                y = y / 100 * bitmap.getHeight();

                w = w / 100 * bitmap.getWidth();
                h = h / 100 * bitmap.getHeight();

                log("x:" + x + " \ny:" + y + "\nw" + w + "\nh" + h);

                Paint mPaint = new Paint();
                mPaint.setColor(Color.GREEN);
                mPaint.setStrokeWidth(5);

                //画BOX
                canvas.drawLine(x - w / 2, y - h / 2, x - w / 2, y + h / 2, mPaint);
                canvas.drawLine(x - w / 2, y - h / 2, x + w / 2, y - h / 2, mPaint);
                canvas.drawLine(x + w / 2, y - h / 2, x + w / 2, y + h / 2, mPaint);
                canvas.drawLine(x - w / 2, y + h / 2, x + w / 2, y + h / 2, mPaint);

                Bitmap ageBitmap = buildNameBitmap(name, "Male".equals(gender));

                //缩放
                int ageWidth = ageBitmap.getWidth();
                int ageHeight = ageBitmap.getHeight();
                if (bitmap.getWidth() < image.getWidth() && bitmap.getHeight() < image.getHeight()) {
                    float ratio = Math.max(bitmap.getWidth() * 1.0f / image.getWidth(), bitmap.getHeight() * 1.0f / image.getHeight());
                    ageBitmap = Bitmap.createScaledBitmap(ageBitmap, (int) (ageWidth * ratio), (int) (ageHeight * ratio), false);

                }
                canvas.drawBitmap(ageBitmap, x - ageBitmap.getWidth() / 2, y - h / 2 - ageBitmap.getHeight(), null);
                mPhotoImage = bitmap;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private Bitmap buildNameBitmap(String name, boolean isMale) {

        TextView tv = (TextView) framelayout.findViewById(R.id.age_gender);
        tv.setText(name);
        tv.setText(name);
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

    @OnClick({R.id.camera, R.id.photo, R.id.upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri = Uri.fromFile(new File(address));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, CODE_CAMERA);
                break;
            case R.id.photo:
                startActivityForResult(new Intent(PhotoActivity.this, ImageActivity.class), CODE_PHOTO);
                break;
            case R.id.upload:
                FacePPDecet.decet(mPhotoImage, new FacePPDecet.CallBack() {
                    @Override
                    public void success(JSONObject result) {
                        try {
                            JSONArray faces = result.getJSONArray("face");
                            int faceCount = faces.length();
                            log("faceCount:" + faceCount);
                            if(faceCount == 0){
                                toast("未识别出人脸");
                            }
                            for (int i = 0; i < faceCount; i++) {
                                //得到单独face对象
                                JSONObject face = faces.getJSONObject(i);
                                final String gender = face.getJSONObject("attribute").getJSONObject("gender").getString("value");
                                String faceId = face.getString("face_id");
                                log("face_id；" + faceId);

                                FacePPDecet.identify(url, faceId, new FacePPDecet.CallBack() {
                                    @Override
                                    public void success(JSONObject result) {
                                        Message msg = new Message();
                                        Bundle b = new Bundle();
                                        b.putString("gender", gender);
                                        msg.obj = result;
                                        msg.what = MSG_IDENTIFY;
                                        handler.sendMessage(msg);
                                    }

                                    @Override
                                    public void error(FaceppParseException e) {
                                        log("辨识失败");
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void error(FaceppParseException e) {
                        log("辨识失败2");
                    }
                });

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CODE_CAMERA) {
                mPhotoImage = BitmapFactory.decodeFile(address);
                image.setImageBitmap(mPhotoImage);
                log("原图文件地址：" + address);
                mPhotoImage = BitmapFactory.decodeFile(address);
                image.setImageBitmap(mPhotoImage);
                Message msg = new Message();
                msg.what = MSG_UPLOAD_PHOTO;
                handler.sendMessage(msg);
            } else if (requestCode == CODE_PHOTO) {
                List<String> list = data.getStringArrayListExtra("imgs");
                s = list.get(list.size() - 1);
                log("原图文件地址：" + s);
                mPhotoImage = BitmapFactory.decodeFile(s);
                image.setImageBitmap(mPhotoImage);
                Message msg = new Message();
                msg.what = MSG_UPLOAD_PHOTO;
                handler.sendMessage(msg);
            }
        }
    }
}