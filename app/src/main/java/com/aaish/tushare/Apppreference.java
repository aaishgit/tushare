package com.aaish.tushare;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by aaishsindwani on 11/09/16.
 */
public class Apppreference extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.appsettings);
    }

}
