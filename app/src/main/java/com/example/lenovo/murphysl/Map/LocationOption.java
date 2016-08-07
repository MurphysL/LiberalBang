package com.example.lenovo.murphysl.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.baidu.location.LocationClientOption;
import com.example.lenovo.murphysl.MapActivity;
import com.example.lenovo.murphysl.R;

/**
 * LocationOption
 *
 * @author: lenovo
 * @time: 2016/8/4 23:23
 */

public class LocationOption extends Activity {

    private RadioGroup selectLocMode;
    private EditText scanSpan;
    private CheckBox geolocation,poi,describe,director;
    private LocationClientOption option;
    private Button startLoc;
    private Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_config);
        init();
        loc =  ((LocationApplication)getApplication()).location;
        option = new LocationClientOption();
        loc.stop();
    }

    private void init(){
        selectLocMode = (RadioGroup)findViewById(R.id.selectMode);
        scanSpan = (EditText)findViewById(R.id.frequence);
        geolocation = (CheckBox)findViewById(R.id.geolocation);
        poi = (CheckBox)findViewById(R.id.poiCheckBox);
        describe = (CheckBox)findViewById(R.id.Describe);
        director = (CheckBox)findViewById(R.id.Director);
        startLoc = (Button)findViewById(R.id.start);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLoc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                switch (selectLocMode.getCheckedRadioButtonId()) {
                    case R.id.radio_hight:
                        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
                        break;
                    case R.id.radio_low:
                        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
                        break;
                    case R.id.radio_device:
                        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
                        break;
                    default:
                        break;
                }

                try {
                    int frequence = Integer.parseInt(scanSpan.getText().toString());
                    option.setScanSpan(frequence);
                } catch (Exception e) {

                    option.setScanSpan(3000);
                }
                /**
                 * 地理位置信息
                 */
                if(geolocation.isChecked())
                    option.setIsNeedAddress(true);
                else
                    option.setIsNeedAddress(false);
                /**
                 * 周边poi列表
                 */
                if(poi.isChecked())
                    option.setIsNeedLocationPoiList(true);
                else
                    option.setIsNeedLocationPoiList(false);
                /**
                 * 位置语意化
                 */
                if(describe.isChecked())
                    option.setIsNeedLocationDescribe(true);
                else
                    option.setIsNeedLocationDescribe(false);
                /**
                 * 方向
                 */
                if(director.isChecked())
                    option.setNeedDeviceDirect(true);
                else
                    option.setNeedDeviceDirect(false);

                /**
                 * 设置前需停止定位服务，设置后重启定位服务才可以生效
                 */
                loc.setLocationOption(option);

                Intent locIntent = new Intent(LocationOption.this, MapActivity.class);
                locIntent.putExtra("from", 1);
                LocationOption.this.startActivity(locIntent);
            }

        });
    }
}
