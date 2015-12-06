package com.pham.accessmap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.pham.accessmap.Object.LocationType;
import com.pham.accessmap.Object.LocationType_Adapter;

import java.util.ArrayList;
import java.util.Locale;


public class SettingActivity extends ActionBarActivity {

    SeekBar seekBar;
    TextView km;
    TextView radiusRange;
    LocationType_Adapter adapter;
    LocationType locationType;
    ArrayList<LocationType> data;
    boolean language;

    public void setLanguage()
    {
        radiusRange = (TextView)findViewById(R.id.tv_radius);
        radiusRange.setText(R.string.label_radius);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle(R.string.title_activity_setting);


        //adapter.notifyDataSetChanged();

        final Context context = this;
        SharedPreferences preferences = this.getSharedPreferences("MyPref",this.MODE_PRIVATE);
        language = preferences.getBoolean("language", false);
        // Language
        Switch switchLanguage = (Switch)findViewById(R.id.languageSwitch);
        switchLanguage.setTextOff("VN");
        switchLanguage.setTextOn("Eng");
        if (language == true)
        {
            switchLanguage.setChecked(true);
        }
        else
        {
            switchLanguage.setChecked(false);
        }
        setLanguage();
        final ListView listview = (ListView) findViewById(R.id.list_LocationType);
        locationType = new LocationType(this);
        data = locationType.getAllData();
        adapter = new LocationType_Adapter(this,data);
        listview.setAdapter(adapter);

        switchLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true)
                {
                    Locale locale = new Locale("en_US");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    context.getApplicationContext().getResources().updateConfiguration(config, null);
                    language = true;
                }
                else
                {
                    Locale locale = new Locale("vi_VN");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    context.getApplicationContext().getResources().updateConfiguration(config, null);
                    language = false;
                }
        }
        });

        // Switch
        Switch switchAll = (Switch) findViewById(R.id.switch_all);
        switchAll.setChecked(true);
        switchAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true)
                {
                    for (LocationType locationType1 : data)
                    {
                        locationType.checkLocationType(locationType1.locationType_ID);
                    }
                    reloadAllData();

                }
                else
                {

                    for (LocationType locationType1 : data)
                    {
                        locationType.uncheckLocationType(locationType1.locationType_ID);
                    }
                    reloadAllData();
                }
            }
        });

        //Seekbar
        seekBar = (SeekBar)findViewById(R.id.radius_range);
        km = (TextView)findViewById(R.id.tv_km);
        SharedPreferences prefs = this.getSharedPreferences("MyPref",this.MODE_PRIVATE);
        int kilomet = prefs.getInt("radius", 3);

        seekBar.setProgress(kilomet);
        km.setText(kilomet + 1 + "km");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 1;
            @Override

            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {

                progress = progresValue + 1;
                km.setText(progress + "km");
                //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();

            }

            @Override

            public void onStartTrackingTouch(SeekBar seekBar) {

                //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();

            }

            @Override

            public void onStopTrackingTouch(SeekBar seekBar) {


                //Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }

        });
    }


    private void reloadAllData(){
        // get new modified random data
        data = locationType.getAllData();
        // update data in our adapter
        adapter.clear();
        adapter.addAll(data);
        // fire the event
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

        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            //Button saveButton = (Button)findViewById(R.id.action_save);
            //saveButton.setText(R.string.label_save_button);
            SharedPreferences.Editor editor = this.getSharedPreferences("MyPref",this.MODE_PRIVATE).edit();
            editor.clear();
            editor.putInt("radius", seekBar.getProgress());
            editor.putBoolean("language",language);
            editor.commit();


            for (LocationType loctype : data)
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
                if (isTrue == false)
                {
                    locationType.uncheckLocationType(loctype.locationType_ID);
                    //Log.v("false",String.valueOf(loctype.locationType_ID));
                }
            }


            new AlertDialog.Builder(SettingActivity.this)
                    .setTitle(R.string.alert_success_title)
                    .setMessage(R.string.alert_save_data)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            restartActivity();
                        }
                    })

                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void restartActivity()
    {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }
}

