package com.klinware.arduinobonitor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DevicesActivity extends Activity {

    // Log
    private static final String         TAG = "bonitor";
    // Views
    private ListView                    btList;
    // Bluetooth
    private Bluetooth                   bt;
    private boolean                     scanning = false;
    private boolean                     connecting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        Log.e(TAG, "DevicesActivity - onCreate()");
        // Views
        btList = (ListView)findViewById(R.id.btList);
        // Bluetooth
        bt = Bluetooth.getInstance();
        if( bt.on() ){
            discovery();
        }else{
            Log.v(TAG,"Problems with Bluetooth");
            Toast.makeText(getApplicationContext(), R.string.btError, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // Start discovery BT devices
    private void discovery() {
        Log.i(TAG, "DevicesActivity - discovery()");
        scanning = true;
        connecting = false;
        String[] scanBT = {getString(R.string.scanning)};
        btList.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, scanBT));
        // If we're already scanning BT devices, stop it
        if ( bt.scanning() ){
            Log.v(TAG, "discovery() has been activated already, cancelDiscovery"); // Log
            bt.cancelScan();
        }
        // IntenFilters for Bluetooth detection
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(btReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(btReceiver, filter);
        // Begin scanning BT devices
        bt.scan();
    }

    // Click on Scan Button
    public void click(View v){
        Log.i(TAG, "DevicesActivity - click() " + scanning);
        if(!scanning){
            discovery();
        }
    }

    // onResume
    protected void onResume(){
        super.onResume();
        Log.i(TAG, "DevicesActivity - onResume()");
        // BT devices selected
        btList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "selected, element " + position);
                if (!bt.isEmpty()) {
                    if (!scanning) {
                        // Begin connection
                        connecting = true;
                        // Go to next Activity
                        startActivity(new Intent(DevicesActivity.this, BonitorActivity.class).putExtra("DEVICE", position));
                        // Return from BonitorActivity
                        connecting = false;
                    }
                }
            }
        });
    }

    // Back button pressed
    @Override
    public void onBackPressed(){
        Log.e(TAG, "DevicesActivity - onBackPressed()");
        finish();
    }

    // onDestroy
    @Override
    protected void onDestroy(){
        Log.e(TAG, "DevicesActivity - onDestroy()");
        super.onDestroy();
        if ( bt.scanning() ){
            bt.cancelScan();
        }
        unregisterReceiver(btReceiver);
        finish();
    }

    // BroadcastReceiver for discover BT devices
    private final BroadcastReceiver btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w(TAG, "BroadcastReceiver btReceiver - onReceive");
            Bluetooth bt = Bluetooth.getInstance();
            // BT device found
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                Log.w(TAG, "Device BT found");
                // Pick BluetoothDevice object from Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Store it
                bt.add(device);
                Log.w(TAG, device.getName() + " " + device.getAddress());
            }
            // BT Scanning is finished
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                Log.w(TAG, "BT search completed");
                ArrayAdapter<String> btDevicesName;
                if(!connecting){
                    // Load No BT devices found
                    if(bt.isEmpty()){
                        Log.w(TAG,"No BT devices");
                        String noDevices[] = {getString(R.string.no_devices_found)};
                        btDevicesName = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, noDevices);
                    }
                    // Load BT devices found
                    else {
                        Log.w(TAG, "Load BT devices");
                        //noBt = false;
                        ArrayList<String> btNames = new ArrayList<String>();
                        for(BluetoothDevice d : bt.getDevices()){
                            btNames.add(d.getName());
                        }
                        btDevicesName = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, btNames);
                    }
                    btList.setAdapter(btDevicesName);
                }
                scanning = false;
            }
        }
    };

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_devices, menu);
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
    }*/
}
