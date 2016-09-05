package com.example.lenovo.murphysl.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lenovo.murphysl.LoginActivity;
import com.example.lenovo.murphysl.MapActivity;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.StarActivity;
import com.example.lenovo.murphysl.UserHobbyActivity;
import com.example.lenovo.murphysl.UserInfoActivity;
import com.example.lenovo.murphysl.VoiceActivity;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.base.ParentWithNaviFragment;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.face.PhotoActivity;
import com.example.lenovo.murphysl.model.UserModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobUser;

//import com.example.lenovo.murphysl.UserInfoActivity;

/**
 * FourthFragment
 * <p/>
 * //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
 * //BmobConfig config =new BmobConfig.Builder(this)
 * ////设置appkey
 * //.setApplicationId("Your Application ID")
 * ////请求超时时间（单位为秒）：默认15s
 * //.setConnectTimeout(30)
 * ////文件分片上传时每片的大小（单位字节），默认512*1024
 * //.setUploadBlockSize(1024*1024)
 * ////文件的过期时间(单位为秒)：默认1800s
 * //.setFileExpiration(2500)
 * //.build();
 * //Bmob.initialize(config);
 *
 * @author: lenovo
 * @time: 2016/8/4 18:49
 */

public class FourthFragment extends ParentWithNaviFragment {

    @Bind(R.id.tv_set_name)
    TextView tv_set_name;

    public FourthFragment() {
    }

    @Override
    public Object right() {
        return R.drawable.base_action_bar_add_bg_selector;
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {

            }

            @Override
            public void clickRight() {

            }
        };
    }

    public static FourthFragment newInstance() {
        FourthFragment fragment = new FourthFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String title() {
        return "我";
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_for, container, false);
        initNaviView();
        ButterKnife.bind(this, rootView);
        String username = UserModel.getInstance().getUser().getUsername();
        tv_set_name.setText(TextUtils.isEmpty(username) ? "" : username);
        return rootView;
    }

    @OnClick(R.id.layout_info)
    public void onInfoClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("u", BmobUser.getCurrentUser(getActivity(), UserBean.class));
        startActivity(UserInfoActivity.class, bundle);
    }

    @OnClick(R.id.btn_logout)
    public void onLogoutClick(View view) {
        UserModel.getInstance().logout();
        //可断开连接
        BmobIM.getInstance().disConnect();
        getActivity().finish();
        startActivity(LoginActivity.class, null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.layout_hobby)
    public void onHobbyClick(View v) {
        startActivity(UserHobbyActivity.class, null);
    }

    @OnClick(R.id.layout_star)
    public void onClick() {
        startActivity(StarActivity.class, null);
    }

    @OnClick(R.id.layout_voice)
    public void onVoiceClick() {
        startActivity(VoiceActivity.class, null);
    }

    @OnClick(R.id.layout_photo)
    public void onPhotoClick() {
        startActivity(PhotoActivity.class, null);
    }

    @OnClick(R.id.layout_map)
    public void onMapClick() {
        startActivity(MapActivity.class, null);
    }
}
