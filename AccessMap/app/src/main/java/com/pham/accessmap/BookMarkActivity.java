package com.pham.accessmap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.pham.accessmap.Object.Location;
import com.pham.accessmap.Object.LocationAdapter;

import java.util.ArrayList;


public class BookMarkActivity extends ActionBarActivity {

    ListView listview;
    LocationAdapter adapter;
    ArrayList<Location> data;
    Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark2);

        listview  = (ListView) findViewById(R.id.list_location);
        location = new Location(this);
        data= location.getBookmarkDataWithID();
        adapter = new LocationAdapter(this,data);
        listview.setAdapter(adapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("click",String.valueOf(data.get(position).latitude));
                Log.v("click",String.valueOf(data.get(position).longitude));
                Intent intent = new Intent();
                intent.putExtra("latitude",data.get(position).latitude);
                intent.putExtra("longitude",data.get(position).longitude);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder adb=new AlertDialog.Builder(BookMarkActivity.this);
                adb.setTitle(getResources().getString(R.string.dialog_delete_title));
                adb.setMessage(getResources().getString(R.string.dialog_delete_description));
                final int positionToRemove = position;
                adb.setNegativeButton(getResources().getString(R.string.dialog_cancel_button), null);
                adb.setPositiveButton(getResources().getString(R.string.alert_ok_title), new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v("data",String.valueOf(data.get(position).locationID));
                        location.removeBookmark(data.get(position).locationID);
                        reloadAllData();

                    }});
                adb.show();

                return true;
            }
        });

        EditText searchText = (EditText) findViewById(R.id.bookmarkSearchBar);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.v("text changed","text changed: " +s);
                if (count == 0)
                {
                    reloadAllData();
                }
                else
                {
                    data = location.getBookmarkDataWithText(s);
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

    private void reloadAllData(){
        // get new modified random data
        data = location.getBookmarkDataWithID();
        // update data in our adapter
        adapter.clear();
        adapter.addAll(data);
        // fire the event
        adapter.notifyDataSetChanged();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_mark, menu);
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
}
