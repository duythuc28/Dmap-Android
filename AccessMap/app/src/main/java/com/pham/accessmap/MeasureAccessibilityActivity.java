package com.pham.accessmap;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.pham.accessmap.Object.AccessType;
import com.pham.accessmap.Object.Guide_AccessApdater;
import com.pham.accessmap.Object.LanguageHelper;

import java.util.ArrayList;

public class MeasureAccessibilityActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_accessibility);
        getSupportActionBar().setTitle(R.string.title_activity_guide);
        final ListView listview = (ListView) findViewById(R.id.measure_accessibility_listView);
        AccessType accessType = new AccessType(this);
        ArrayList<AccessType> data = accessType.getAllData();
        Guide_AccessApdater adapter = new Guide_AccessApdater(this,data);
        listview.setAdapter(adapter);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

}
