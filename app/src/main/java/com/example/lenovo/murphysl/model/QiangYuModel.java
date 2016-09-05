package com.example.lenovo.murphysl.model;

import android.app.Activity;

import com.example.lenovo.murphysl.bean.QiangYu;
import com.example.lenovo.murphysl.util.ActivityManagerUtils;

/**
 * QiangYuModel
 *
 * @author: lenovo
 * @time: 2016/9/4 17:11
 */

public class QiangYuModel {

    private static QiangYuModel ourInstance = new QiangYuModel();

    public static QiangYuModel getInstance() {
        return ourInstance;
    }

    private QiangYuModel() {}

    private QiangYu currentQiangYu = null;

    public QiangYu getCurrentQiangYu() {
        return currentQiangYu;
    }

    public void setCurrentQiangYu(QiangYu currentQiangYu) {
        this.currentQiangYu = currentQiangYu;
    }

    public void addActivity(Activity ac){
        ActivityManagerUtils.getInstance().addActivity(ac);
    }

    public void exit(){
        ActivityManagerUtils.getInstance().removeAllActivity();
    }


}
