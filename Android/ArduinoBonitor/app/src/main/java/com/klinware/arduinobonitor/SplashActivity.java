package com.klinware.arduinobonitor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class SplashActivity extends Activity {

    // Log
    private static final String TAG = "bonitor";
    // Bluetooth
    private static final int    REQUEST_ENABLE_BT = 1234;
    // Splash time in miliseconds
    private static final int    SPLASH = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.e(TAG, "SplashActivity");
        // Bluetooth
        Bluetooth bt = Bluetooth.getInstance();
        // If the adapter is null, then Bluetooth is not supported
        if(!bt.available()) {
            Log.v(TAG, "There's not Bluetooth"); // Log
            Toast.makeText(this, R.string.splashErrorNoBt1, Toast.LENGTH_LONG).show();
            finish();
            return;
        } else if(!bt.on()){
            Log.v(TAG, "Bluetooth is OFF"); // Log
            Toast.makeText(this, R.string.splashBtON, Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
        } else {
            Log.v(TAG, "Bluetooth is activated, application begins"); // Log
            splash();
        }
    }

    // onActivityResult
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        if( (requestCode == REQUEST_ENABLE_BT) && (resultCode == RESULT_OK) ){
            Log.v(TAG, "Bluetooth is activated, application begins"); // Log
            splash();
        }else if( (requestCode == REQUEST_ENABLE_BT) && (resultCode == RESULT_CANCELED) ){
            Log.v(TAG, "Bluetooth not activated by user"); // Log
            Toast.makeText(getApplicationContext(), R.string.splashErrorNoBt2, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void splash(){
        Log.i(TAG,"splash()");
        new Handler().postDelayed(new Runnable(){
            public void run(){
                // Wait SPLASH miliseconds & then go to next Activity
                startActivity(new Intent(SplashActivity.this, DevicesActivity.class));
                finish();
            };
        }, SPLASH);
    }
}
