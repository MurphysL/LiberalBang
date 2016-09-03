package com.example.lenovo.murphysl;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.lenovo.murphysl.base.ParentWithNaviActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * PoiDetailActivity
 *
 * @author: lenovo
 * @time: 2016/9/1 9:08
 */

public class PoiDetailActivity extends ParentWithNaviActivity {

    @Bind(R.id.c1)
    TextView c1;
    @Bind(R.id.c2)
    TextView c2;
    @Bind(R.id.c3)
    TextView c3;
    @Bind(R.id.c5)
    TextView c5;
    @Bind(R.id.c6)
    TextView c6;
    @Bind(R.id.c7)
    TextView c7;
    @Bind(R.id.c8)
    TextView c8;
    @Bind(R.id.c9)
    TextView c9;
    @Bind(R.id.c10)
    TextView c10;


    @Override
    protected String title() {
        return "详情";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poidetail);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Bundle b = getIntent().getBundleExtra("com.example.lenovo.murphysl");
        String url = b.getString("url");
        c1.setText(b.getString("poiname"));
        c2.setText(b.getString("poiAddress"));
        c3.setText(b.getString("poiTele"));
        c5.setText(b.getString("poiTag"));
        c6.setText(b.getDouble("poiHRating") + "");
        c7.setText(b.getDouble("poiTRating") + "");
        c8.setText(b.getDouble("poiORating") + "");
        c9.setText(b.getDouble("poiSRating") + "");
        c10.setText(b.getString("poiTime"));

        b.getString("url");
        log(url);
        //idWebView.loadUrl(url);
    }
}
