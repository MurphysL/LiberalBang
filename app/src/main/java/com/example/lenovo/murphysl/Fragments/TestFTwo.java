package com.example.lenovo.murphysl.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.BackHandledFragment;

/**
 * TestFTwo
 *
 * @author: lenovo
 * @time: 2016/8/4 18:49
 */

public class TestFTwo extends BackHandledFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v2 = inflater.inflate(R.layout.f_fragment, container , false);
        //Log.i(FirstFragment.Tag , "T2");
        return v2;
    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }
}
