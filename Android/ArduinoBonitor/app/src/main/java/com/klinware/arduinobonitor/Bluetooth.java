package com.klinware.arduinobonitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by klin on 23/06/15.
 *
 *
 Log.d(TAG, "Bluetooth - ");
 *
 */


public class Bluetooth {

    // Singleton Pattern
    private static Bluetooth            instance = null;
    // Log
    private static final String         TAG = "bonitor";
    // Bluetooth
    private BluetoothAdapter            btAdapter;
    private ArrayList<BluetoothDevice>  btDevices;
    private BluetoothSocket             btSocket;
    private static final String         UUID_CODE = "00001101-0000-1000-8000-00805F9B34FB";

    //private ConnectionThread            connectionThread;
    private BluetoothThreads            threads;

    private Bluetooth() {
        //super();
        Log.d(TAG, "Bluetooth()");
        btAdapter        = BluetoothAdapter.getDefaultAdapter();
        btDevices        = null;
        btSocket         = null;
        //connectionThread = null;

    }

    public static Bluetooth getInstance(){
        Log.d(TAG, "Bluetooth - getInstance()");
        if(instance == null){
            instance = new Bluetooth();
        }
        return instance;
    }

    // Check if BT is supported
    public boolean available(){
        Log.d(TAG, "Bluetooth - available()");
        if (btAdapter != null) {
            return true;
        }
        return false;
    }

    // Check if BT is activated
    public boolean on(){
        Log.d(TAG, "Bluetooth - on()");
        if(btAdapter.isEnabled()){
            return true;
        }
        return false;
    }

    // Check if BT is scanning BT devices
    public boolean scanning(){
        Log.d(TAG, "Bluetooth - scanning()");
        if(btAdapter.isDiscovering()){
            return true;
        }
        return false;
    }

    // Start to scan BT devices
    public void scan(){
        Log.d(TAG, "Bluetooth - scan()");
        btAdapter.startDiscovery();
        clearDevices();
    }

    // Cancel scanning
    public void cancelScan(){
        Log.d(TAG, "Bluetooth - cancelScan");
        btAdapter.cancelDiscovery();
    }

    // Add BT device
    public void add(BluetoothDevice device){
        Log.d(TAG, "Bluetooth - add()");
        if(btDevices != null){
            btDevices.add(device);
        }else{
            btDevices = new ArrayList<BluetoothDevice>();
            btDevices.add(device);
        }
    }

    // Check if there are BT devices stored
    public boolean isEmpty(){
        Log.d(TAG, "Bluetooth - isEmpty");
        if(btDevices != null && btDevices.size() > 0){
            return false;
        }
        return true;
    }

    // Erase all BT devices
    public void clearDevices(){
        Log.d(TAG, "Bluetooth - clearDevices");
        if(!isEmpty()){
            btDevices.clear();
        }
    }

    // Return BT devices name
    public ArrayList<BluetoothDevice> getDevices(){
        return btDevices;
    }

    // Return one BT device
    public BluetoothDevice getElement(int i){
        if(!isEmpty() && (i < btDevices.size()) ){
            return btDevices.get(i);
        }
        return null;
    }

    // Begin connection BT
    public void connection(BluetoothDevice device){
        //connectionThread = new ConnectionThread(device, btSocket);
        //connectionThread.start();
    }

    // Close all BT communications
    public void closeAll(){
        //if(connectionThread != null){
          //  connectionThread.cancel();
        //}
    }

    /*
     *  Threads
     */

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage = " + msg.obj.toString());
            // Creación del mensaje recibido
            String msjAux = (String)msg.obj;
            if(!msjAux.equals("btConnected")){
                for(int i = 0; i < msjAux.length(); i++){
                    if(msjAux.charAt(i) == '\n'){
                        // Mostrar el mensaje por pantalla
                        //printMonitor();
                        //msjTotal = "";
                        break;
                    }
                    //msjTotal += msjAux.charAt(i);
                }
            }else if(msjAux.equals("btConnected")){
                // Change UI

            }


            //cont++;
        }

        // escribirSeguido
        private void escribirSeguido() {
            Log.d(TAG,"escribirSeguido");

        }

        // sobreEscribir
        private void sobreEscribir() {
            Log.d(TAG,"sobreEscribir");

        }
    };

    // Thread para conectarse con otro dispositivo
    private class ConnectionThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        private static final String UUID_CODE = "00001101-0000-1000-8000-00805F9B34FB";
        private final BluetoothAdapter mBluetoothAdapter;

        public ConnectionThread(BluetoothDevice device, BluetoothSocket socket) {
            Log.w(TAG, "ConnectThread");
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            tmp = null;
            mmDevice = device;
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(UUID_CODE));
            } catch (IOException e) { }
            mmSocket = tmp;
            socket = mmSocket;
        }

        public void run() {
            Log.w(TAG, "run");
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
            // Do work to manage the connection (in a separate thread)
            //btSocket = mmSocket;
            Message btConnected = new Message();
            btConnected.obj = "btConnected";
            //handler.sendMessage(btConnected);
            //connected(mmSocket);

        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            Log.w(TAG, "cancel");
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

    }



}
