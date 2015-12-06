package com.pham.accessmap;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.pham.accessmap.Object.Download;
import com.pham.accessmap.Object.Feedback;
import com.pham.accessmap.Object.Feedback_Adapter;

import java.util.ArrayList;


public class TabFeedbackActivity extends ActionBarActivity {
    int locationID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_feedback);
        getSupportActionBar().setTitle(R.string.title_activity_feedback);
        //getActionBar().setTitle("Feedback");
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        locationID = bundle.getInt("id");

        Download download = new Download(this);
        final ListView listview1 = (ListView) findViewById(R.id.feedback_listView);
        ArrayList<Feedback> feedbacks = download.getFeedbackFromLocationID(locationID);
        Feedback_Adapter feedback_adapter = new Feedback_Adapter(this, feedbacks);
        listview1.setAdapter(feedback_adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab_feedback, menu);
        //getActionBar().setTitle("Feedback");
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
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, AddFeedbackActivity.class);
            intent.putExtra("id",locationID);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
