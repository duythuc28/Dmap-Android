package com.pham.accessmap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.pham.accessmap.Object.Location;
import com.pham.accessmap.Object.LocationAdapter;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class RoutesActivity extends ActionBarActivity {

    ListView listview;
    LocationAdapter adapter;
    ArrayList<Location> data;
    ArrayList<String> nameList;
    Location location;
    Boolean isRoute;
    EditText fromText;
    EditText toText;
    LatLng original;
    LatLng destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        listview  = (ListView) findViewById(R.id.list_route);
        fromText  = (EditText) findViewById(R.id.fromRoute);
        toText  = (EditText)findViewById(R.id.toRoute);

        isRoute = false;
        location = new Location(this);
        data = location.getAllData();
        nameList = new ArrayList<String>();
        for (Location location1 : data)
        {
            nameList.add(location1.location_title);
        }
        adapter = new LocationAdapter(this,data);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isRoute == true)
                {
                    Double latitude = Double.parseDouble(data.get(position).latitude);
                    Double longitude = Double.parseDouble(data.get(position).longitude);
                    destination = new LatLng(latitude,longitude);
                    toText.setText(data.get(position).location_title);
                }
                else {
                    //
                    Double latitude = Double.parseDouble(data.get(position).latitude);
                    Double longitude = Double.parseDouble(data.get(position).longitude);
                    original = new LatLng(latitude,longitude);
                    fromText.setText(data.get(position).location_title);
                }
            }
        });


        fromText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                isRoute = false;
                if (count == 0)
                {
                    reloadData();
                }
                else
                {
                    data = location.getSearchData(s);
                    adapter.clear();
                    adapter.addAll(data);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        toText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                isRoute = true;
                if (count == 0)
                {
                    reloadData();
                }
                else
                {
                    data = location.getSearchData(s);
                    adapter.clear();
                    adapter.addAll(data);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    public void onClick_switch(View v)
    {
        String temp = fromText.getText().toString();
        fromText.setText(toText.getText().toString());
        toText.setText(temp);
    }

    public void reloadData()
    {
        data = location.getAllData();
        adapter.clear();
        adapter.addAll(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_routes, menu);
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
        if (id == R.id.action_findRoute) {

            CharSequence[] transportType = new CharSequence[2];
            transportType[0] = "Bus";
            transportType[1] = "Bike";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose your transit mode")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                        }
                    })
                    .setItems(transportType, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            Intent intent = new Intent();
                            intent.putExtra("origin", original);
                            intent.putExtra("destination",destination);

                            if (which == 0) {
                                intent.putExtra("isBus", true);
                            } else {
                                intent.putExtra("isBus", false);
                            }
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    });
            builder.show();

        }

        return super.onOptionsItemSelected(item);
    }
}
