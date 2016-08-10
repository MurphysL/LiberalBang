package com.example.lenovo.murphysl.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.BackHandledFragment;

/**
 * FourthFragment
 *
 * @author: lenovo
 * @time: 2016/8/4 18:49
 */

public class FourthFragment extends BackHandledFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v1 = inflater.inflate(R.layout.for_fragment, container , false);
        return v1;
    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }
}
