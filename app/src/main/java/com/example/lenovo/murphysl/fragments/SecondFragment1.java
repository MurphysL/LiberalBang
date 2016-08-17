package com.example.lenovo.murphysl.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.BackHandledFragment;
import com.example.lenovo.murphysl.ui.PopupListView;
import com.example.lenovo.murphysl.ui.PopupView;

import java.util.ArrayList;

/**
 * SecondFragment1
 *
 * @author: lenovo
 * @time: 2016/8/4 18:49
 */

public class SecondFragment1 extends BackHandledFragment {
    private static final String TAG = "SecondFragment1";

    PopupListView popupListView;
    ArrayList<PopupView> popupViews;
    int actionBarHeight;
    int p = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.s_fragment, container , false);
        popupViews = new ArrayList<>();
        popupListView = (PopupListView) v.findViewById(R.id.popupListView);
        for (int i = 0; i < 10; i++) {
            p = i;
            //修改
            PopupView popupView = new PopupView(getActivity(), R.layout.popup_view_item) {
                @Override
                public void setViewsElements(View view) {
                    TextView textView = (TextView) view.findViewById(R.id.title);
                    textView.setText("Popup View " + String.valueOf(p));
                }

                @Override
                public View setExtendView(View view) {
                    View extendView;
                    if (view == null) {
                        extendView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R
                                .layout.extend_view, null);//修改
                        TextView innerText = (TextView) extendView.findViewById(R.id.innerText);
                        innerText.setText("Inner View " + String.valueOf(p));
                    } else {
                        extendView = view;
                    }
                    return extendView;
                }
            };
            popupViews.add(popupView);
        }
        popupListView.init(null);
        popupListView.setItemViews(popupViews);
        return v;
    }

    @Override
    protected boolean onBackPressed() {
        if (popupListView.isItemZoomIn()) {
            popupListView.zoomOut();
            return true;
        } else {
            return false;
        }
    }
}
