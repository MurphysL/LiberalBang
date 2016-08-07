package com.example.lenovo.murphysl.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lenovo.murphysl.R;

/**
 * TestFThree
 *
 * @author: lenovo
 * @time: 2016/8/4 18:49
 */

public class TestFThree extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v4 = inflater.inflate(R.layout.f_fragment, container , false);
        //Log.i(FirstFragment.Tag , "T3");
        return v4;
    }
}
