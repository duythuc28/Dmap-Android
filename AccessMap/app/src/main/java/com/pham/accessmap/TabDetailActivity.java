package com.pham.accessmap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pham.accessmap.Object.AccessAdapter;
import com.pham.accessmap.Object.AccessType;
import com.pham.accessmap.Object.Location;

import java.util.ArrayList;


public class TabDetailActivity extends ActionBarActivity {
    TextView locationTitle ;
    TextView address;
    TextView phone ;
    int locationID;
    Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_detail);

        getSupportActionBar().setTitle(R.string.title_activity_tab_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        locationTitle = (TextView) findViewById(R.id.locationTitle);
        address = (TextView) findViewById(R.id.locationAddress);
        phone = (TextView) findViewById(R.id.locationPhone);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        locationTitle.setText(bundle.getString("title"));
        address.setText(bundle.getString("address"));
        phone.setText(bundle.getString("phone"));
        locationID = bundle.getInt("id");

        final ListView listview = (ListView) findViewById(R.id.listView);
        AccessType accessType = new AccessType(this);
        ArrayList<AccessType> data = accessType.getAccessTypeByLocationID(locationID);
        AccessAdapter adapter = new AccessAdapter(this, data);
        listview.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab_detail, menu);
        //getActionBar().setTitle("Details");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

    public void onClick_addBookmark (View v)
    {
        Location location = new Location(this);
        location.editBookmark(locationID);
        new AlertDialog.Builder(this)
                .setTitle(R.string.alert_success_title)
                .setMessage(R.string.alert_adding_success)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })

                .show();
    }

    public void onClick_findBikeRoute (View v)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.customdialog);
        dialog.setTitle(R.string.label_find_route);

        // set the custom dialog components - text, image and button
        final TextView from = (TextView) dialog.findViewById(R.id.from_route);
        from.setText(R.string.label_current_position);
        final TextView to = (TextView) dialog.findViewById(R.id.to_route);
        to.setText(locationTitle.getText());

        dialog.show();

        Button dialogButton = (Button) dialog.findViewById(R.id.cancel);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ImageButton switchRoute = (ImageButton) dialog.findViewById(R.id.switch_route);

        switchRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence temp = from.getText();
                from.setText(to.getText());
                to.setText(temp);
            }
        });

        Button findRoute = (Button) dialog.findViewById(R.id.find_route);

        findRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         getInfoFromId();
        Intent intent = new Intent();
        intent.putExtra("originLatitude",location.latitude);
        intent.putExtra("originLongitude", location.longitude);
        intent.putExtra("isBus", false);

        if (from.getText().toString().equals(getResources().getText(R.string.label_current_position)))
        {
            intent.putExtra("isSwitch",false);
        }
        else
        {
            intent.putExtra("isSwitch",true);
        }

        intent.putExtra("isReturn",true);

        //startActivity(intent);
        if (getParent() == null)
        {
            setResult(RESULT_OK,intent);
        }
        else
        {
            getParent().setResult(RESULT_OK,intent);
        }
        finish();
            }
        });


    }

    public void onClick_findBusRoute (View v)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.customdialog);
        dialog.setTitle(R.string.label_find_route);

        // set the custom dialog components - text, image and button
        final TextView from = (TextView) dialog.findViewById(R.id.from_route);
        from.setText(R.string.label_current_position);
        final TextView to = (TextView) dialog.findViewById(R.id.to_route);
        to.setText(locationTitle.getText());

        dialog.show();

        Button dialogButton = (Button) dialog.findViewById(R.id.cancel);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ImageButton switchRoute = (ImageButton) dialog.findViewById(R.id.switch_route);

        switchRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence temp = from.getText();
                from.setText(to.getText());
                to.setText(temp);
            }
        });

        Button findRoute = (Button) dialog.findViewById(R.id.find_route);

        findRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfoFromId();
                Intent intent = new Intent();
                intent.putExtra("originLatitude",location.latitude);
                intent.putExtra("originLongitude",location.longitude);
                intent.putExtra("isBus",true);

                if (from.getText().toString().equals(getResources().getText(R.string.label_current_position)))
                {
                    intent.putExtra("isSwitch",false);
                }
                else
                {
                    intent.putExtra("isSwitch",true);
                }

                intent.putExtra("isReturn",true);

                //startActivity(intent);
                if (getParent() == null)
                {
                    setResult(RESULT_OK,intent);
                }
                else
                {
                    getParent().setResult(RESULT_OK,intent);
                }
                finish();
            }
        });
    }

    public void getInfoFromId ()
    {
        location = new Location(this);
        location = location.getDataWithID(locationID);
    }
}
