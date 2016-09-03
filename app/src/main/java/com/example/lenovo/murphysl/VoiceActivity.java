package com.example.lenovo.murphysl;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.bean.VoiceBean;
import com.example.lenovo.murphysl.map.Location;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * VoiceActivity
 *
 * @author: lenovo
 * @time: 2016/9/1 20:05
 */

public class VoiceActivity extends ParentWithNaviActivity {

    private File soundFile;
    private MediaRecorder mRecorder;
    private Location location;
    private Double longitude;
    private Double latitude;

    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            longitude = bdLocation.getLongitude();
            latitude = bdLocation.getLatitude();
        }
    };

    @Override
    protected String title() {
        return "留声机";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        ButterKnife.bind(this);
        initLoc();
        initRecord();
    }

    private void initRecord() {
        mRecorder = new MediaRecorder();
    }

    private void initLoc() {
        location= new Location(this);
        location.registerLocListener(mListener);
        location.start();
    }


    @Override
    protected void onDestroy() {
        if(soundFile != null &&soundFile.exists()){
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        super.onDestroy();
    }

    @OnClick({R.id.voice_close, R.id.voice_send , R.id.voice_open})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.voice_close:
                mRecorder.stop();
                mRecorder.release();
                break;
            case R.id.voice_send:
                final BmobFile sound = new BmobFile(soundFile);
                sound.upload(VoiceActivity.this, new UploadFileListener() {
                    @Override
                    public void onSuccess() {
                        log("已上传");
                        VoiceBean voiceBean = new VoiceBean();
                        BmobGeoPoint point = new BmobGeoPoint(longitude , latitude);
                        voiceBean.setUser(BmobUser.getCurrentUser(VoiceActivity.this , UserBean.class));
                        voiceBean.setFile(sound);
                        voiceBean.setGeo(point);
                        voiceBean.save(VoiceActivity.this, new SaveListener() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailure(int i, String s) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
                break;
            case R.id.voice_open:
                try {
                    toast("录音中");
                    soundFile = new File(Environment.getExternalStorageDirectory().getCanonicalFile() + "/sound.amr");
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mRecorder.setOutputFile(soundFile.getAbsolutePath());
                    mRecorder.prepare();
                    mRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
        }
    }
}
