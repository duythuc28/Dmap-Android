package com.pham.accessmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.pham.accessmap.Object.AccessType;
import com.pham.accessmap.Object.Guide_AccessApdater;
import com.pham.accessmap.Object.LanguageHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MeasureAccessibilityActivity extends ActionBarActivity implements Guide_AccessApdater.CheckBoxCallBack {

    ArrayList <Integer> mSelectedPlaces;

    public ArrayList<Integer> getSelectedPlaces() {
        if (mSelectedPlaces == null) {
            Bundle bundle = getIntent().getExtras();
            mSelectedPlaces = bundle.getIntegerArrayList("LIST_MEASURE_PLACES");
        }
        return mSelectedPlaces;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putIntegerArrayListExtra("LIST_SELECTED_CELL",mSelectedPlaces);
        setResult(RESULT_OK,intent);
        Log.e("back", "" + 1);
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_accessibility);
        getSupportActionBar().setTitle(R.string.title_activity_guide);
        final ListView listview = (ListView) findViewById(R.id.measure_accessibility_listView);
        AccessType accessType = new AccessType(this);
        ArrayList<AccessType> data = accessType.getAllData();
        Guide_AccessApdater adapter = new Guide_AccessApdater(this,data,getSelectedPlaces());
        listview.setAdapter(adapter);
        adapter.mListener = this;
    }

    @Override
    public void onCheckedChangedListener(int checkedPosition, boolean isChecked) {
        if (isChecked) {
            mSelectedPlaces.add(checkedPosition);
        } else {
            if (mSelectedPlaces.contains(checkedPosition)) {
                mSelectedPlaces.remove(checkedPosition);
            }
        }
        Collections.sort(mSelectedPlaces);
    }
    @Override
    protected void onResume() {
        super.onResume();
        String tLanguageCode = LanguageHelper.getInstance().getAppLanguage(this);
        LanguageHelper.getInstance().setAppLanguage(tLanguageCode, this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_guide, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                // click on 'up' button in the action bar, handle it here
                Intent intent = new Intent();
                intent.putIntegerArrayListExtra("LIST_SELECTED_CELL",mSelectedPlaces);
                setResult(RESULT_OK,intent);
                Log.e("home", "" + 1);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
