package com.example.lenovo.murphysl.face;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.BoringLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.murphysl.ImageActivity;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FaceActivity extends ParentWithNaviActivity implements View.OnClickListener {

    private static final int CODE_IMAGE = 1;
    private static final String FacePP = "FacePP";
    private Button get_image;
    private Button decet;
    private TextView textView;
    private ImageView imageView;
    private View waitting;
    private String mCurrentPhotoStr;
    private Bitmap mPhotoImage;
    private Bitmap mPhotoImage2;

    private static final int MSG_SUCCESS = 0X111;
    private static final int MSG_ERROR = 0X112;

    private Paint mPaint;

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

                    prepareRsBitmap(rs);
                    imageView.setImageBitmap(mPhotoImage);

                    break;
                case MSG_ERROR:
                    waitting.setVisibility(View.GONE);
                    String errorMsg = (String) msg.obj;

                    if (TextUtils.isEmpty(errorMsg)) {
                        textView.setText("Error.");
                    } else {
                        textView.setText(errorMsg);
                    }

                    break;

            }
            super.handleMessage(msg);
        }
    };

    /**
     * 解析数据
     *
     * @param rs
     */
    private void prepareRsBitmap(JSONObject rs) {

        Bitmap bitmap = Bitmap.createBitmap(mPhotoImage.getWidth(), mPhotoImage.getHeight(), mPhotoImage.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(mPhotoImage, 0, 0, null);

        try {
            JSONArray faces = rs.getJSONArray("face");

            int faceCount = faces.length();
            textView.setText("find" + faceCount);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.decet:
                waitting.setVisibility(View.VISIBLE);
                FacePPDecet.decet(mPhotoImage, new FacePPDecet.CallBack() {
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
            case R.id.get_image:
                startActivityForResult(new Intent(this, ImageActivity.class), CODE_IMAGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_IMAGE) {
            if (data != null) {
                List<String> list = data.getStringArrayListExtra("imgs");
                Iterator<String> iterator = list.iterator();
                Boolean flag = true;
                while (iterator.hasNext()) {
                    String address = iterator.next();
                    if(flag){
                        mPhotoImage = BitmapFactory.decodeFile(address);
                        imageView.setImageBitmap(mPhotoImage);
                        textView.setText("Click Detect==>");
                    }else{
                        mPhotoImage2 = BitmapFactory.decodeFile(address);
                        log("加载完成");
                    }
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取图片缩略图
     */
    private void resizePhoto() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        //options 存储图片尺寸及宽度
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoStr, options);

        double ratio = Math.max(options.outWidth * 1.0d / 1024f,
                options.outHeight * 1.0d / 1024f);

        options.inSampleSize = (int) Math.ceil(ratio);

        options.inJustDecodeBounds = false;

        mPhotoImage = BitmapFactory.decodeFile(mCurrentPhotoStr, options);
        Log.i(FacePP, "whatfuck");
    }

    @Override
    protected String title() {
        return "识别人脸";
    }

   /* @OnClick(R.id.get_compare)
    public void onClick() {
        waitting.setVisibility(View.VISIBLE);
        FacePPDecet.compare(mPhotoImage,mPhotoImage2, new FacePPDecet.CallBack() {
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
    }*/
}
