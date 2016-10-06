package com.example.lenovo.murphysl.moment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.example.lenovo.murphysl.ImageActivity;
import com.example.lenovo.murphysl.MyApplication;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.QiangYu;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.map.Location;
import com.example.lenovo.murphysl.model.UserModel;
import com.example.lenovo.murphysl.util.ActivityUtil;
import com.example.lenovo.murphysl.util.CacheUtils;
import com.example.lenovo.murphysl.util.StringUtils;
import com.example.lenovo.murphysl.view.UserHobbyActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * EditActivity
 *
 * @author: lenovo
 * @time: 2016/8/21 18:01
 */

public class EditActivity extends ParentWithNaviActivity {

    @Bind(R.id.edit_content)
    EditText editContent;
    @Bind(R.id.take_layout)
    LinearLayout takeLayout;
    @Bind(R.id.open_layout)
    LinearLayout openLayout;
    @Bind(R.id.take_pic)
    ImageView takePic;
    @Bind(R.id.commit_edit)
    Button commitEdit;
    @Bind(R.id.et_money)
    EditText etMoney;

    private static final int REQUEST_CODE_ALBUM = 1;
    private static final int REQUEST_CODE_CAMERA = 2;

    private UserBean user = UserModel.getInstance().getUser();

    private String dateTime;
    private String geo;
    private BmobGeoPoint loc;

    private Location location;
    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null) {
                geo = bdLocation.getAddrStr();
                loc = new BmobGeoPoint(bdLocation.getLongitude(), bdLocation.getLatitude());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initNaviView();
        ButterKnife.bind(this);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(this.getResources().getColor(R.color.green_theme));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        initGeo();
    }

    private void initGeo() {
        location = MyApplication.getINSTANCE().location;
        location.registerLocListener(mListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        location.start();
    }

    @Override
    protected void onDestroy() {
        location.unregisterLocListener(mListener);
        location.stop();
        super.onDestroy();
    }


    /**
     * 发表带图片
     */
    private void publish(final String commitContent) {

        final BmobFile figureFile = new BmobFile(new File(targeturl));

        figureFile.upload(EditActivity.this, new UploadFileListener() {

            @Override
            public void onSuccess() {
                log(figureFile.getFileUrl(EditActivity.this));
                publishWithoutFigure(commitContent, figureFile);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                log("上传文件失败。" + arg1);
            }
        });

    }

    private void publishWithoutFigure(final String commitContent, final BmobFile figureFile) {
        String[] keyWords = {
                this.getResources().getString(R.string.ac_eat_chinese),
                this.getResources().getString(R.string.ac_eat_buffet_dinner),
                this.getResources().getString(R.string.ac_eat_fast),
                this.getResources().getString(R.string.ac_eat_snack),
                this.getResources().getString(R.string.ac_eat_west),

                this.getResources().getString(R.string.ac_bar),
                this.getResources().getString(R.string.ac_entertainment),
                this.getResources().getString(R.string.ac_gym),
                this.getResources().getString(R.string.ac_internet),
                this.getResources().getString(R.string.ac_movie),

                this.getResources().getString(R.string.ac_tour_amusement),
                this.getResources().getString(R.string.ac_tour_museum),
                this.getResources().getString(R.string.ac_tour_park),
                this.getResources().getString(R.string.ac_tour_zoo),
                this.getResources().getString(R.string.ac_tour_view),

                this.getResources().getString(R.string.ac_play_badminton),
                this.getResources().getString(R.string.ac_play_ball),
                this.getResources().getString(R.string.ac_play_basketball),
                this.getResources().getString(R.string.ac_play_football),
                this.getResources().getString(R.string.ac_play_volleyball)};

        final QiangYu qiangYu = new QiangYu();
        qiangYu.setAuthor(user);
        qiangYu.setContent(commitContent);
        qiangYu.setKeyWord(StringUtils.keyWord(commitContent, keyWords));

        if (figureFile != null) {
            qiangYu.setContentfigureurl(figureFile);
        }
        qiangYu.setState(0);
        qiangYu.setLove(0);
        log(etMoney.getText().toString());
        if(etMoney.getText().toString() != "0"){
            qiangYu.setMoney(Integer.parseInt(etMoney.getText().toString()));
        }else{
            qiangYu.setMoney(0);
        }

        qiangYu.setComment(0);
        qiangYu.setPass(true);
        qiangYu.setGeo(geo);
        qiangYu.setLoc(loc);
        qiangYu.save(EditActivity.this, new SaveListener() {

            @Override
            public void onSuccess() {
                log("创建成功");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                log("创建失败。" + arg1);
            }
        });
    }

    String targeturl = null;

    private Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置采样率

        newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;
    }

    public String saveToSdCard(Bitmap bitmap) {
        String files = CacheUtils.getCacheDirectory(EditActivity.this, true, "pic")
                + dateTime + "_11.jpg";
        File file = new File(files);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log(file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    @Override
    protected String title() {
        return "求助";
    }

    @OnClick({R.id.open_layout, R.id.take_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_layout:
                Date date1 = new Date(System.currentTimeMillis());
                dateTime = date1.getTime() + "";
                startActivityForResult(new Intent(EditActivity.this, ImageActivity.class), REQUEST_CODE_ALBUM);
                break;
            case R.id.take_layout:
                Date date = new Date(System.currentTimeMillis());
                dateTime = date.getTime() + "";

                File f = new File(CacheUtils.getCacheDirectory(EditActivity.this, true, "pic") + dateTime);
                if (f.exists()) {
                    f.delete();
                }
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri uri = Uri.fromFile(f);
                Log.e("uri", uri + "");

                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(camera, REQUEST_CODE_CAMERA);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ALBUM:
                    if (data != null) {
                        List<String> list = data.getStringArrayListExtra("imgs");
                        Iterator iterator = list.iterator();
                        if (iterator.hasNext()) {
                            targeturl = (String) iterator.next();
                            takeLayout.setVisibility(View.GONE);
                        }
                    }
                    break;
                case REQUEST_CODE_CAMERA:
                    String files = CacheUtils.getCacheDirectory(EditActivity.this, true,
                            "pic") + dateTime;
                    File file = new File(files);
                    if (file.exists()) {
                        Bitmap bitmap = compressImageFromFile(files);
                        targeturl = saveToSdCard(bitmap);
                        takePic.setBackgroundDrawable(new BitmapDrawable(bitmap));
                        openLayout.setVisibility(View.GONE);
                    }
                    break;
            }
            takeLayout.setVisibility(View.GONE);
            openLayout.setVisibility(View.GONE);
            commitEdit.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.commit_edit)
    public void onClick() {
        String commitContent = editContent.getText().toString().trim();
        if (TextUtils.isEmpty(commitContent)) {
            ActivityUtil.show(EditActivity.this, "内容不能为空");
            return;
        }
        String moneyContent = etMoney.getText().toString().trim();
        if (TextUtils.isEmpty(commitContent)) {
            ActivityUtil.show(EditActivity.this, "内容不能为空");
            return;
        }
        if (targeturl == null) {
            publishWithoutFigure(commitContent, null);
        } else {
            publish(commitContent);
            finish();
        }
        toast("发送成功");
    }

}
