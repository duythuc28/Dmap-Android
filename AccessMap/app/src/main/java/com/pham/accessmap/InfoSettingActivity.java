package com.pham.accessmap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.pham.accessmap.Object.Download;
import com.pham.accessmap.Object.LanguageHelper;

public class InfoSettingActivity extends ActionBarActivity {

    private String      mLanguageCode ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpActivityView();
    }


    private void setUpActivityView () {
        RadioButton tEnRadioButton  = (RadioButton)findViewById(R.id.setting_radioButton_en);
        RadioButton tViRadioButton  = (RadioButton)findViewById(R.id.setting_radioButton_vi);
        mLanguageCode        = LanguageHelper.getInstance().getAppLanguage(this);
        tViRadioButton.setChecked(mLanguageCode.equals(LanguageHelper.VIETNAMESE));
        tEnRadioButton.setChecked(mLanguageCode.equals(LanguageHelper.ENGLISH));
    }

    public void onClick_helpButton(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.setting_action_save) {

        }
        return super.onOptionsItemSelected(item);
    }

    private void appChangeLanguage (String sLanguageCode) {
        LanguageHelper.getInstance().setAppLanguage(sLanguageCode,this);
        // Restart activity
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void onClick_updateButton(View view) {
        // TODO: Hard code
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, "Please wait ...", "Downloading ...", true);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Download download = new Download(InfoSettingActivity.this);
                    download.getAccessibilityType();
                    download.getLocationType();
                    download.getLocation();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Your dialog code.
                            new AlertDialog.Builder(InfoSettingActivity.this)
                                    .setTitle("Success")
                                    .setMessage("You have updated information")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                            dialog.cancel();
                                        }
                                    })
                                    .show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
                ringProgressDialog.dismiss();

            }

        }).start();
    }

    public void onClick_infoButton(View view) {
    }

    public void onClick_placeType(View view) {
    }

    public void onClickEnglishButton(View view) {
        mLanguageCode = LanguageHelper.ENGLISH;
        appChangeLanguage(mLanguageCode);
    }

    public void onClickViButton(View view) {
        mLanguageCode = LanguageHelper.VIETNAMESE;
        appChangeLanguage(mLanguageCode);
    }
}
