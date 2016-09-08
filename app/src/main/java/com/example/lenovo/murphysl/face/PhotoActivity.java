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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.murphysl.ImageActivity;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.MyDate;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.event.VetityEvent;
import com.example.lenovo.murphysl.model.BaseModel;
import com.example.lenovo.murphysl.model.UserModel;
import com.facepp.error.FaceppParseException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
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

    private static final int MSG_QUERY = 10;
    private static final int CODE_CAMERA = 1;
    private static final int MSG_IDENTIFY = 2;
    private static final int CODE_PHOTO = 3;
    private static final int MSG_UPLOAD_PHOTO = 4;
    private static final int MSG_SUCCESS = 5;
    private static final int MSG_ERROR = 6;
    private static final int MSG_TRAIN = 7;
    private static final int MSG_ADD_FACE = 8;
    private static final int MSG_PERSON_LIST = 9;
    private static final int MSG_VETIFY = 0;

    private final UserBean user = UserModel.getInstance().getUser();
    @Bind(R.id.share)
    Button share;

    private Bitmap mPhotoImage;
    private String s;
    private String address = Environment.getExternalStorageDirectory().getPath() +
            "/" + new Date(System.currentTimeMillis()).getTime() + ".png";
    private String url;

    private Boolean flag = true;
    private int faceNum;
    private List<JSONObject> jsList = new ArrayList<>();
    private List<String> personList = new ArrayList<>();
    private List<String> faceIDList = new ArrayList<>();
    private Map<String , String > owner = new HashMap<>();

    private HashMap<String , Integer > pMap = new HashMap<String , Integer>();
    private HashMap<String , Integer> fMap = new HashMap<String , Integer>();
    private List<String> nameList = new ArrayList<>();
    private int temp2;
    private int vFace;//人脸计数


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventVetity (final VetityEvent event){
        final String face = event.getFace();
        final String per = personList.get(pMap.get(face) - 1);

        FacePPDecet.verify(face ,per , new FacePPDecet.CallBack() {
            @Override
            public void success(JSONObject result) {
                try {
                    if (result.getBoolean("is_same_person")) {
                        owner.put(face , per);
                        nameList.add(per);
                        log(face + ": " + "添加" + per + "成功");
                        Message message = new Message();
                        Bundle vb = new Bundle();
                        vb.putString("Vface" , "fc");
                        message.setData(vb);
                        handler.sendMessage(message);
                    }else{
                        log("添加" + per + "失败");
                        Message msg = new Message();
                        msg.what = MSG_VETIFY;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void error(FaceppParseException e) {
                log("错误");
            }
        });

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_QUERY:
                    String nameL = msg.getData().getString("nameL");
                    log("handler name " + nameL);

                    UserModel.getInstance().queryUsers(nameL, BaseModel.DEFAULT_LIMIT, new FindListener<UserBean>() {
                        @Override
                        public void onSuccess(List<UserBean> list) {
                            final UserBean user = list.get(0);
                            final BmobFile bmobFile = new BmobFile(new File(s));

                            bmobFile.upload(PhotoActivity.this, new UploadFileListener() {
                                @Override
                                public void onSuccess() {
                                    log("success");
                                    MyDate datePhoto = new MyDate();
                                    datePhoto.setPhoto(bmobFile);
                                    datePhoto.setUser(user);
                                    datePhoto.save(PhotoActivity.this, new SaveListener() {
                                        @Override
                                        public void onSuccess() {
                                            toast("上传照片成功");
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            toast("上传照片失败");
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    toast("上传照片失败");
                                    log("上传照片失败");
                                }
                            });
                        }

                        @Override
                        public void onError(int i, String s) {
                            toast(s);
                            log(s);
                        }
                    });
                    break;
                case MSG_VETIFY:
                    String face = null;
                    if("fc".equals(msg.getData().getString("vFace"))){
                        vFace --;
                    }
                    if(vFace - 1 >= 0){
                        log("存在未辨识人脸");
                        face = faceIDList.get(vFace - 1);
                        if("pc".equals(msg.getData().getString("vFace"))){
                            pMap.put(face , pMap.get(face) - 1);
                        }

                        int pNum = pMap.get(face);
                        if(pNum - 1 > 0){
                            log("存在未辨识person" + "pMap" + pMap.get(face) + "vFace" + vFace);
                            EventBus.getDefault().post(
                                    new VetityEvent(face , personList.get(pNum - 1)));
                            pMap.put(face , pMap.get(face) - 1);//此人脸对应person减一

                        }else{
                            vFace --;//人脸减一
                            EventBus.getDefault().post(
                                    new VetityEvent(faceIDList.get(vFace) , personList.get(pNum - 1)));
                        }
                    }else{
                        log("循环完毕");
                        for(int o = 0 ; o < temp2 ;o ++){
                            prepareRsBitmap(owner.get(faceIDList.get(o)) , jsList.get(o), "Male");
                        }
                        image.setImageBitmap(mPhotoImage);
                        sendPhoto(nameList);
                    }

                    break;
                case MSG_IDENTIFY:
                    log("辨识成功");
                    if(flag){
                        faceNum = msg.arg1;
                        flag = false;
                    }
                    if(faceNum == 1){
                        jsList.add((JSONObject) msg.obj);
                        prepareRsBitmap(null , null, msg.getData().getString("gender"));
                        image.setImageBitmap(mPhotoImage);
                    }else{
                        jsList.add((JSONObject) msg.obj);
                        faceNum --;
                    }
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
                                    log("原图上传成功 URL" + url);
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
                case MSG_SUCCESS:
                    JSONObject rs = (JSONObject) msg.obj;
                    log("辨识人脸成功");
                    Message msg4 = new Message();
                    msg4.obj = rs;
                    msg4.what = MSG_ADD_FACE;
                    handler.sendMessage(msg4);
                    break;
                case MSG_ERROR:
                    String errorMsg = (String) msg.obj;

                    if (TextUtils.isEmpty(errorMsg)) {
                        toast("错误");
                    } else {
                        toast(errorMsg);
                    }
                    break;
                case MSG_TRAIN:
                    FacePPDecet.verifyTrain(UserModel.getInstance().getUser().getUsername(), new FacePPDecet.CallBack() {
                        @Override
                        public void success(JSONObject result) {
                            log("训练完成");
                        }

                        @Override
                        public void error(FaceppParseException e) {
                            log("训练失败");
                        }
                    });
                    break;
                case MSG_PERSON_LIST:
                    FacePPDecet.personList(new FacePPDecet.CallBack() {
                    @Override
                    public void success(JSONObject result) {
                        try {
                            JSONArray persons = result.getJSONArray("person");
                            for(int i = 0 ; i < persons.length() ; i ++ ){
                                JSONObject person = persons.getJSONObject(i);
                                String personID = person.getString("person_name");
                                personList.add(personID);
                            }
                            log("拉取personList成功：" + personList.size());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void error(FaceppParseException e) {
                        toast("拉取person失败");
                    }
                });
                break;
                case MSG_ADD_FACE:
                    try {
                        JSONObject rs2 = (JSONObject) msg.obj;
                        JSONArray faces = rs2.getJSONArray("face");
                        int faceCount = faces.length();
                        log("faceCount：" + faceCount);

                        if (faceCount == 1) {
                            String name = UserModel.getInstance().getUser().getUsername();
                            log("User name：" + name);

                            FacePPDecet.addFace(name, rs2, new FacePPDecet.CallBack() {
                                @Override
                                public void success(JSONObject result) {
                                    log("face name : 添加Face成功" );
                                    Message msg3 = new Message();
                                    msg3.what = MSG_TRAIN;
                                    handler.sendMessage(msg3);
                                }

                                @Override
                                public void error(FaceppParseException e) {

                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
            }

        }
    };

    private void sendPhoto(List<String> nameList) {
        Iterator<String> i = nameList.iterator();
        log("sendPhoto" + nameList.size());
        while(i.hasNext()){
            String name = i.next();
            Message msg = new Message();
            msg.what = MSG_QUERY;
            Bundle b = new Bundle();
            b.putString("nameL" , name);
            msg.setData(b);
            log("name" + name);
            handler.sendMessage(msg);
        }
    }

    @Override
    protected String title() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);
        Message msg = new Message();
        msg.what = MSG_PERSON_LIST;
        handler.sendMessage(msg);
    }


    /**
     * 解析数据
     *
     * @param name
     * @param rs
     * @param gender
     */
    private void prepareRsBitmap(String name ,JSONObject rs, String gender) {
        //将原图绘制在Bitmap上
        Bitmap bitmap = Bitmap.createBitmap(mPhotoImage.getWidth(), mPhotoImage.getHeight(), mPhotoImage.getConfig());
        log("width" + mPhotoImage.getWidth() + "height" + mPhotoImage.getHeight());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(mPhotoImage, 0, 0, null);
        try {
            //JSONArray faces = rs.getJSONArray("face");
            //int faceNum = faces.length() - 1;
           /* if (faceNum < 0) {
                toast("不存在已登记人脸");
            } else {*/

                JSONObject face = rs;
                log(face.toString());
                //JSONArray candidate = face.getJSONArray("candidate");
                JSONObject posObj = face.getJSONObject("position");//人脸位置
               /* Double temp = 0.0;
                int pos = 0;
                for (int i = 0; i < candidate.length(); i++) {
                    JSONObject confidences = candidate.getJSONObject(i);
                    Double confidence = confidences.getDouble("confidence");
                    if (temp < confidence) {
                        temp = confidence;
                        pos = i;
                    }
                }*/

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
           // }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        mPhotoImage = bitmap;
        log("draw");

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

    @OnClick({R.id.camera, R.id.photo, R.id.upload, R.id.share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share:
                share.setVisibility(View.VISIBLE);

                break;
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
                FacePPDecet.decet(url, new FacePPDecet.CallBack() {
                    @Override
                    public void success(JSONObject result) {
                        try {
                            JSONArray faces = result.getJSONArray("face");
                            final int faceCount = faces.length();
                            log("faceCount:" + faceCount);
                            if (faceCount == 0) {
                                toast("未识别出人脸");
                            }else{
                                for (int i = 0; i < faceCount; i++) {
                                    //得到单独face对象
                                    JSONObject face = faces.getJSONObject(i);
                                    final String gender = face.getJSONObject("attribute").getJSONObject("gender").getString("value");
                                    String faceId = face.getString("face_id");
                                    log("face_id；" + faceId);
                                    jsList.add(face);
                                    faceIDList.add(faceId);
                                }

                                for(int p = 0 ; p < faceIDList.size() ;p ++){
                                    Integer i = new Integer(personList.size());
                                    pMap.put(faceIDList.get(p) , i);
                                    log("face：" +faceIDList.get(p));
                                    fMap.put(faceIDList.get(p) , faceIDList.size());
                                }

                                vFace = faceIDList.size();
                                temp2 = vFace;

                                Message msg = new Message();
                                msg.what = MSG_VETIFY;
                                handler.sendMessage(msg);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void error(FaceppParseException e) {
                        toast("辨识失败");
                        log("辨识失败2");
                    }
                });

                break;
        }
    }

    @OnClick(R.id.face)
    public void onClick() {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CODE_CAMERA) {
                mPhotoImage = BitmapFactory.decodeFile(address);
                image.setImageBitmap(mPhotoImage);
                log("原图文件地址：" + address);
                s = address;
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