package com.klinware.arduinobonitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by klin on 1/07/15.
 */
public class BluetoothThreads {

    // Singleton Pattern
    private static BluetoothThreads     instance = null;
    // Log
    private static final String         TAG = "bonitor";
    // Bluetooth
    private ConnectionThread            connectionThread;
    /*
     *  Threads
     */

    private BluetoothThreads(){
        Log.d(TAG,"BluetoothThreads()");
        connectionThread = null;
    }

    public BluetoothThreads getInstance(){
        Log.d(TAG,"BluetoothThreads - getInstance()");
        if(instance==null){
            instance = new BluetoothThreads();
        }
        return instance;
    }

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

    private final Handler handler = new Handler(){

    };




}
