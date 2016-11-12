package com.aaish.tushare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by aaishsindwani on 13/09/16.
 */
public class Frag2 extends Fragment {
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frag2view = inflater.inflate(R.layout.frag2, container, false);
        return frag2view;
    }
}
