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
import android.widget.TextView;

import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
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

/**
 * PhotoActivity
 *
 * @author: lenovo
 * @time: 2016/9/3 17:49
 */

public class PhotoActivity extends ParentWithNaviActivity {


    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.age_gender)
    TextView ageGender;
    @Bind(R.id.framelayout)
    FrameLayout framelayout;

    private static final int CODE_CAMERA = 1;
    private static final int CODE_QUERY = 0;
    private static final int CODE_IDENTIFY = 2;
    private static final int CODE_PHOTO = 3;

    Bitmap mPhotoImage;
    String address = Environment.getExternalStorageDirectory().getPath() +
            "/" + new Date(System.currentTimeMillis()).getTime() + ".png";
    private Paint mPaint;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CODE_IDENTIFY) {
                FacePPDecet.decet(mPhotoImage, new FacePPDecet.CallBack() {
                    @Override
                    public void success(JSONObject result) {
                        try {
                            JSONArray faces = result.getJSONArray("face");
                            int faceCount = faces.length();
                            log("faceCount:" + faceCount);
                            for (int i = 0; i < faceCount; i++) {
                                //得到单独face对象
                                JSONObject face = faces.getJSONObject(i);
                                final String gender = face.getJSONObject("attribute").getJSONObject("gender").getString("value");
                                String faceId = face.getString("face_id");
                                log("face_id；" + faceId);
                                FacePPDecet.identify(mPhotoImage, new FacePPDecet.CallBack() {
                                    @Override
                                    public void success(JSONObject result) {
                                        prepareRsBitmap(result, gender);
                                        log("辨识成功");
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

        Bitmap bitmap = Bitmap.createBitmap(mPhotoImage.getWidth(), mPhotoImage.getHeight(), mPhotoImage.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(mPhotoImage, 0, 0, null);

        try {
            JSONObject faceInfo = rs.getJSONObject("face");
            JSONObject posObj = faceInfo.getJSONObject("position");
            JSONArray cans = faceInfo.getJSONArray("candidate");
            Double temp = 0.0;
            int pos = 0;
            for (int t = 0; t < cans.length(); t++) {
                JSONObject can = cans.getJSONObject(t);
                Double c = can.getDouble("confidence");
                if (c > temp) {
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
            if (bitmap.getWidth() < image.getWidth() && bitmap.getHeight() < image.getHeight()) {
                float ratio = Math.max(bitmap.getWidth() * 1.0f / image.getWidth(), bitmap.getHeight() * 1.0f / image.getHeight());
                ageBitmap = Bitmap.createScaledBitmap(ageBitmap, (int) (ageWidth * ratio), (int) (ageHeight * ratio), false);

            }
            canvas.drawBitmap(ageBitmap, x - ageBitmap.getWidth() / 2, y - h / 2 - ageBitmap.getHeight(), null);
            mPhotoImage = bitmap;
            image.setImageBitmap(mPhotoImage);

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
                Message msg = new Message();
                msg.what = CODE_IDENTIFY;
                handler.sendMessage(msg);
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
            } else if (requestCode == CODE_PHOTO) {
                List<String> list = data.getStringArrayListExtra("imgs");
                mPhotoImage = BitmapFactory.decodeFile(list.get(list.size() - 1));
                image.setImageBitmap(mPhotoImage);
            }
        }
    }
}
