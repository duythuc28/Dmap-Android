package com.pham.accessmap;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.TextView;

import org.w3c.dom.Text;

public class About extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpActivityView();
    }

    private void setUpActivityView (){
        TextView tDrdTitle = (TextView)findViewById(R.id.about_drd_content);
        tDrdTitle.setText(R.string.about_drd_title);
        TextView tDrdDesctiption = (TextView)findViewById(R.id.about_drd_description);
        tDrdDesctiption.setText(R.string.about_drd_description);
        TextView tHoaSenTitle = (TextView)findViewById(R.id.about_hsu_content);
        tHoaSenTitle.setText(R.string.about_hoasen_title);
        TextView tHoaSenDesctiption = (TextView)findViewById(R.id.about_hsu_description);
        tHoaSenDesctiption.setText(R.string.about_hoasen_description);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_mark, menu);
        return true;
    }
}
