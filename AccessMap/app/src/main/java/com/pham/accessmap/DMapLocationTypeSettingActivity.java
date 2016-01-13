package com.pham.accessmap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.pham.accessmap.Object.LanguageHelper;
import com.pham.accessmap.Object.LocationType;
import com.pham.accessmap.Object.LocationType_Adapter;
import com.pham.accessmap.Utils.Utils;

import java.util.ArrayList;

public class DMapLocationTypeSettingActivity extends ActionBarActivity {

    private ArrayList<LocationType> placeTypes;
    private ArrayList<LocationType> savePlaceTypes;
    LocationType_Adapter adapter;
    boolean mIsCheckedAll = true;

    private LocationType_Adapter getAdapter () {
        if (adapter == null) {
            adapter = new LocationType_Adapter(this, getPlaceTypes());
        }
        return adapter;
    }

    private void setAdapter (LocationType_Adapter sAdapter) {
        this.adapter = sAdapter;
    }

    private ArrayList<LocationType> getPlaceTypes() {
        if (placeTypes == null) {
            LocationType locationType = new LocationType(this);
            placeTypes = locationType.getAllData();
            return placeTypes;
        }
        return placeTypes;
    }

    private void setPlaceTypes (ArrayList <LocationType> placeTypes) {
        this.placeTypes = placeTypes;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String tCurrentLanguage = LanguageHelper.getInstance().getAppLanguage(this);
        LanguageHelper.getInstance().setAppLanguage(tCurrentLanguage, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmap_location_type_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ListView listview = (ListView) findViewById(R.id.locationType_setting_listView);
        listview.setAdapter(getAdapter());
        // Switch

        Switch switchAll = (Switch) findViewById(R.id.locationType_setting_switch);
        switchAll.setChecked(Utils.getCheckAllPlaceTypes(this));
        switchAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsCheckedAll = isChecked;
                final ProgressDialog progress = new ProgressDialog(DMapLocationTypeSettingActivity.this);
                progress.setTitle("Loading");
                progress.show();
                savePlaceTypes = new ArrayList<>();
                if (isChecked) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (LocationType locationType : getPlaceTypes()) {
                                    locationType.isCheck = 1;
                                    savePlaceTypes.add(locationType);
//                                    locationType.checkLocationType(locationType.locationType_ID);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Your dialog code.
                                        reloadAllData();
                                        progress.dismiss();
                                    }
                                });
                            } catch (Exception e) {

                            }
                        }

                    }).start();

                } else {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (LocationType locationType : getPlaceTypes()) {
                                    locationType.isCheck = 0;
                                    savePlaceTypes.add(locationType);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Your dialog code.
                                        reloadAllData();
                                        progress.dismiss();
                                    }
                                });
                            } catch (Exception e) {

                            }
                        }
                    }).start();
                }
            }
        });
    }

    private void reloadAllData(){
        // get new modified random data
//        LocationType locationType = new LocationType(this);
//        setPlaceTypes(locationType.getAllData());
        adapter.clear();
        adapter.addAll(this.savePlaceTypes);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        setPlaceTypes(this.savePlaceTypes);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            LocationType locationType = new LocationType(this);
            for (LocationType loctype : getPlaceTypes())
            {
                boolean isTrue = false;
                for (int i : adapter.checkedList)
                {
                    if (i == loctype.locationType_ID) {
                        locationType.checkLocationType(i);
                        //Log.v("true",String.valueOf(i));
                        isTrue = true;
                    }
                    //locationType.getLocationTypeByID(adapter.checkedList.indexOf(i));
                }
                if (!isTrue)
                {
                    locationType.uncheckLocationType(loctype.locationType_ID);
                }
            }
            Utils.setCheckOnAllPlaceTypes(mIsCheckedAll, this);
            new AlertDialog.Builder(DMapLocationTypeSettingActivity.this)
                    .setTitle(R.string.alert_success_title)
                    .setMessage(getResources().getString(R.string.alert_save_data))
                    .setPositiveButton(getString(R.string.alert_ok_title), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
//                            restartActivity();

                        }
                    })
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
