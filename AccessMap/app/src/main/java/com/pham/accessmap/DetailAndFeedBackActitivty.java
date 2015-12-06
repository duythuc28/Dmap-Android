package com.pham.accessmap;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.pham.accessmap.Object.AccessAdapter;
import com.pham.accessmap.Object.AccessType;
import com.pham.accessmap.Object.Download;
import com.pham.accessmap.Object.Feedback_Adapter;
import com.pham.accessmap.Object.Location;

import java.util.ArrayList;


public class DetailAndFeedBackActitivty extends ActionBarActivity {

    TextView locationTitle ;
    TextView address;
    TextView phone ;
    int locationID;
    Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_and_feed_back_actitivty);

        Resources res = getResources();
        final TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("details");
        tabSpec.setContent(R.id.tab_detail);
        tabSpec.setIndicator("", res.getDrawable(R.drawable.tab_icon));
        tabHost.addTab(tabSpec);

        locationTitle = (TextView) findViewById(R.id.locationTitle);
        address = (TextView) findViewById(R.id.locationAddress);
        phone = (TextView) findViewById(R.id.locationPhone);


        if (savedInstanceState == null)
        {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            locationTitle.setText(bundle.getString("title"));
            address.setText(bundle.getString("address"));
            phone.setText(bundle.getString("phone"));
            locationID = bundle.getInt("id");
        }
        else
        {
            locationTitle.setText(savedInstanceState.getString("title"));
            address.setText(savedInstanceState.getString("address"));
            phone.setText(savedInstanceState.getString("phone"));
            locationID = savedInstanceState.getInt("id");
        }



        //List View

        final ListView listview = (ListView) findViewById(R.id.listView);
        AccessType accessType = new AccessType(this);
        ArrayList<AccessType> data = accessType.getAccessTypeByLocationID(locationID);
        AccessAdapter adapter = new AccessAdapter(this, data);
        listview.setAdapter(adapter);
        // Feedback View
        //Intent intentFeedback = new Intent().setClass(this, Feedback.class);
        //intentFeedback.putExtra("id",locationID);
        TabHost.TabSpec feedbackspec = tabHost.newTabSpec("feedback");
        feedbackspec.setContent(R.id.tab_feedback);
        feedbackspec.setIndicator("", res.getDrawable(R.drawable.feedback_icon));
        tabHost.addTab(feedbackspec);

        Download download = new Download(this);
        final ListView listview1 = (ListView) findViewById(R.id.feedback_listView);
        ArrayList<com.pham.accessmap.Object.Feedback> feedbacks = download.getFeedbackFromLocationID(locationID);
        Feedback_Adapter feedback_adapter = new Feedback_Adapter(this, feedbacks);
        listview1.setAdapter(feedback_adapter);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO save your instance to outState
        outState.putString("title", locationTitle.getText().toString());
        outState.putString("address", address.getText().toString());
        outState.putString("phone",phone.getText().toString());
        outState.putInt("id",locationID);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_and_feed_back_actitivty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            AddFeedBack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void AddFeedBack()
    {
        Intent intent = new Intent(this, AddFeedbackActivity.class);
        intent.putExtra("id",locationID);
        startActivity(intent);
    }

}
