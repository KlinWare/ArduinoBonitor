package com.klinware.arduinobonitor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;


public class BonitorActivity extends Activity {

    // Log
    private static final String         TAG = "bonitor";
    // Views
    private ScrollView                  scroll;
    private LinearLayout                linear;
    // Bluetooth
    private Bluetooth                   bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonitor);
        // Log
        Log.e(TAG, "BonitorActivity");
        // Views
        scroll = (ScrollView)   findViewById(R.id.monitorScroll);
        linear = (LinearLayout) findViewById(R.id.monitorLinear);
        // Bluetooth
        bt = Bluetooth.getInstance();


    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bonitor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
