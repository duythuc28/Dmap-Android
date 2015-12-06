package com.pham.accessmap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pham.accessmap.Object.Download;


public class InfoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getSupportActionBar().setTitle(R.string.title_activity_info);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public void onClick_Update(View v)
    {

        final ProgressDialog ringProgressDialog = ProgressDialog.show(InfoActivity.this, "Please wait ...", "Downloading ...", true);
        final boolean isSuccess = false;
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Download download = new Download(InfoActivity.this);
                    download.getAccessibilityType();
                    download.getLocationType();
                    download.getLocation();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Your dialog code.
                            new AlertDialog.Builder(InfoActivity.this)
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

                }
                ringProgressDialog.dismiss();

            }

        }).start();



    }

    public void onClick_Guide(View v)
    {
        Intent intent = new Intent(this, GuideActivity.class);
        startActivity(intent);

    }
    public void onClick_Share(View v)
    {
        CharSequence[] items =  {"DRD Staff", "End User"};
        new AlertDialog.Builder(this)
                .setTitle("You are ")
                .setItems(items, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch(which)
                        {
                            case 0:
                                Intent intent = new Intent(InfoActivity.this, DRDStaffActivity.class);
                                startActivity(intent);
                                // foo case
                                break;
                            case 1:
                                Intent intent1 = new Intent(InfoActivity.this, EndUserActivity.class);
                                startActivity(intent1);
                                break;

                        }
                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })

                .show();
    }

}
