package com.pham.accessmap;

/**
 * Created by mc976 on 1/12/15.
 *
 *
 */
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TabMenuActivity extends TabActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab);

        Resources ressources = getResources();
        TabHost tabHost = getTabHost();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

//        boolean isReturn = bundle.getBoolean("isReturn",false);
//        if (isReturn == true)
//        {
//            intent.putExtra("originLatitude",bundle.getString("originLatitude"));
//            intent.putExtra("originLongitude",bundle.getString("originLongitude"));
//            intent.putExtra("isBus",false);
//            intent.putExtra("isSwitch",false);
//            intent.putExtra("isReturn",true);
//            setResult(Activity.RESULT_OK,intent);
//            finish();
//        }
        // Android tab
        Intent intentAndroid = new Intent().setClass(this, TabDetailActivity.class);
        intentAndroid.putExtra("id",bundle.getInt("id"));
        intentAndroid.putExtra("title",bundle.getString("title"));
        intentAndroid.putExtra("address",bundle.getString("address"));
        intentAndroid.putExtra("phone",bundle.getString("phone"));
        TabSpec tabSpecAndroid = tabHost
                .newTabSpec("Details")
                .setIndicator("", ressources.getDrawable(R.drawable.tab_icon))
                .setContent(intentAndroid);

        // Apple tab
        Intent intentApple = new Intent().setClass(this, TabFeedbackActivity.class);
        intentApple.putExtra("id",bundle.getInt("id"));
        TabSpec tabSpecApple = tabHost
                .newTabSpec("Feedback")
                .setIndicator("", ressources.getDrawable(R.drawable.feedback_icon))
                .setContent(intentApple);

        // add all tabs
        tabHost.addTab(tabSpecAndroid);
        tabHost.addTab(tabSpecApple);

        //set Windows tab as default (zero based)
        tabHost.setCurrentTab(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
}
