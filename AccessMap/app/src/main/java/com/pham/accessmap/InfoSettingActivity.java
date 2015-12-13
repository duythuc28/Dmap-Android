package com.pham.accessmap;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class InfoSettingActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
