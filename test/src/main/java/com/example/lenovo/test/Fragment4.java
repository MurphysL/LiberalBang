package com.example.lenovo.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Fragment1
 *
 * @author: lenovo
 * @time: 2016/8/5 11:28
 */

public class Fragment4 extends BackHandledFragment {
    private static final String TAG = "Fragment4";

    PopupListView popupListView;
    ArrayList<PopupView> popupViews;
    int actionBarHeight;
    int p = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popup_listview, container, false);
        popupViews = new ArrayList<>();
        popupListView = (PopupListView) v.findViewById(R.id.popupListView);
        for (int i = 0; i < 10; i++) {
            p = i;
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
                                .layout.extend_view, null);
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
    public boolean onBackPressed() {
        if (popupListView.isItemZoomIn()) {
            popupListView.zoomOut();
            return true;
        } else {
            return false;
        }
    }


}
