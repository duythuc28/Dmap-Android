package com.pham.accessmap;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.pham.accessmap.Object.Location;
import com.pham.accessmap.Object.LocationAdapter;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity {
    ListView listview;
    LocationAdapter adapter;
    ArrayList<Location> data;
    ArrayList<String> nameList;
    Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listview  = (ListView) findViewById(R.id.list_location);
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
                Intent intent = new Intent();
                Log.v("click",String.valueOf(data.get(position).latitude));
                Log.v("click",String.valueOf(data.get(position).longitude));
                intent.putExtra("latitude",data.get(position).latitude);
                intent.putExtra("longitude",data.get(position).longitude);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        EditText searchText = (EditText) findViewById(R.id.editTextSearch);
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    reloadData();
                    return true;
                }
                return false;
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count == 0)
                {
                    reloadData();
                }
                else
                {
                    data = location.getSearchData(s);
                    adapter.clear();;
                    adapter.addAll(data);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void reloadData()
    {
        data = location.getAllData();
        adapter.clear();
        adapter.addAll(data);
        adapter.notifyDataSetChanged();
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        return true;

    }
}
