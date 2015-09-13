package com.klinware.arduinobonitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by klin on 23/06/15.
 *
 *
 Log.d(TAG, "Bluetooth - ");
 *
 */

public class Bluetooth {

    // Log
    private static final String         TAG = "bonitor";
    // Singleton Pattern
    private static Bluetooth            instance = null;
    // Bluetooth
    private BluetoothAdapter            btAdapter;
    private ArrayList<BluetoothDevice>  btDevices;

    private Bluetooth() {
        //super();
        Log.w(TAG, "Bluetooth()");
        btAdapter        = BluetoothAdapter.getDefaultAdapter();
        btDevices        = null;

    }

    public static Bluetooth getInstance(){
        Log.w(TAG, "Bluetooth - getInstance()");
        if(instance == null){
            instance = new Bluetooth();
        }
        return instance;
    }

    // Check if BT is supported
    public boolean available(){
        Log.w(TAG, "Bluetooth - available()");
        if (btAdapter != null) {
            return true;
        }
        return false;
    }

    // Check if BT is activated
    public boolean on(){
        Log.w(TAG, "Bluetooth - on()");
        if(btAdapter.isEnabled()){
            return true;
        }
        return false;
    }

    // Check if BT is scanning BT devices
    public boolean scanning(){
        Log.w(TAG, "Bluetooth - scanning()");
        if(btAdapter.isDiscovering()){
            return true;
        }
        return false;
    }

    // Start to scan BT devices
    public void scan(){
        Log.w(TAG, "Bluetooth - scan()");
        btAdapter.startDiscovery();
        clearDevices();
    }

    // Cancel scanning
    public void cancelScan(){
        Log.w(TAG, "Bluetooth - cancelScan()");
        btAdapter.cancelDiscovery();
    }

    // Add BT device
    public void add(BluetoothDevice device){
        Log.w(TAG, "Bluetooth - add()");
        if(btDevices != null){
            btDevices.add(device);
        }else{
            btDevices = new ArrayList<BluetoothDevice>();
            btDevices.add(device);
        }
    }

    // Check if there are BT devices stored
    public boolean isEmpty(){
        Log.w(TAG, "Bluetooth - isEmpty()");
        if(btDevices != null && btDevices.size() > 0){
            return false;
        }
        return true;
    }

    // Erase all BT devices
    public void clearDevices(){
        Log.w(TAG, "Bluetooth - clearDevices()");
        if(!isEmpty()){
            btDevices.clear();
        }
    }

    // Return BT devices name
    public ArrayList<BluetoothDevice> getDevices(){
        Log.w(TAG, "Bluetooth - getDevices()");
        return btDevices;
    }

    // Return one BT device
    public BluetoothDevice getElement(int i){
        Log.w(TAG, "Bluetooth - getElement() - " + i);
        if(!isEmpty() && (i < btDevices.size()) ){
            return btDevices.get(i);
        }
        return null;
    }
}
