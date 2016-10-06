package com.example.lenovo.murphysl.face;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.murphysl.MyApplication;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.model.UserModel;
import com.facepp.error.FaceppParseException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

/**
 * SimilarActivity
 *
 * @author: lenovo
 * @time: 2016/9/8 12:47
 */

public class SimilarActivity extends ParentWithNaviActivity {
    private static final int CODE_PHOTO = 1;
    private static final int MSG_UPLOAD_PHOTO = 2;
    @Bind(R.id.iv1)
    ImageView iv1;
    @Bind(R.id.iv2)
    ImageView iv2;
    @Bind(R.id.similar)
    TextView similar;

    private UserBean user = UserModel.getInstance().getUser();

    private List<String> personList = new ArrayList<>();
    private String avatarUrl;

    @Override
    protected String title() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar);
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

        FacePPDecet.personList(new FacePPDecet.CallBack() {
            @Override
            public void success(JSONObject result) {
                try {
                    JSONArray persons = result.getJSONArray("person");
                    for (int i = 0; i < persons.length(); i++) {
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
    }


    @OnClick(R.id.yes)
    public void onClick() {
        BmobQuery<UserBean> q = new BmobQuery<>();
        q.getObject(SimilarActivity.this, user.getObjectId(), new GetListener<UserBean>() {
            @Override
            public void onSuccess(UserBean userBean) {

                if (userBean.getAvatar() != null) {
                    avatarUrl = userBean.getAvatar();
                }
                ImageLoader.getInstance().displayImage(avatarUrl, iv1,
                        MyApplication.getINSTANCE().getOptions(R.drawable.user_icon_default_main),
                        new SimpleImageLoadingListener() {

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                            }

                        });


                FacePPDecet.identify(avatarUrl, new FacePPDecet.CallBack() {
                    @Override
                    public void success(JSONObject result) {
                        try {
                            log(result.toString());
                            JSONArray f = result.getJSONArray("face");
                            JSONObject face = f.getJSONObject(0);
                            JSONArray candidate = face.getJSONArray("candidate");
                            JSONObject sencond = candidate.getJSONObject(1);
                            final String name = sencond.getString("person_name");
                            final Double confidence = sencond.getDouble("confidence");
                            log("name:" + name + "confidence:" + confidence);

                            final BmobQuery<UserBean> q = new BmobQuery<UserBean>();
                            q.addWhereEqualTo("username", name);
                            q.findObjects(SimilarActivity.this, new FindListener<UserBean>() {
                                @Override
                                public void onSuccess(List<UserBean> list) {
                                    String userID = list.get(0).getObjectId();
                                    String url = list.get(0).getAvatar();
                                    similar.setVisibility(View.VISIBLE);
                                    similar.setText("相似度：" + confidence);
                                    ImageLoader.getInstance().displayImage(
                                            url,
                                            iv2,
                                            MyApplication.getINSTANCE().getOptions(R.drawable.user_icon_default_main),
                                            new SimpleImageLoadingListener() {

                                                @Override
                                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                    super.onLoadingComplete(imageUri, view, loadedImage);
                                                }

                                            });
                                }

                                @Override
                                public void onError(int i, String s) {

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void error(FaceppParseException e) {

                    }
                });


            }

            @Override
            public void onFailure(int i, String s) {

            }
        });


    }

}
