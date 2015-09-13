package com.klinware.arduinobonitor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class BonitorActivity extends Activity {

    // Log
    private static final String         TAG = "bonitor";
    // Views
    private ScrollView                  scrollView;
    private LinearLayout                linearLayout;
    private EditText                    txt;
    // Bluetooth
    private Bluetooth                   bt;
    private ConnectionThread            connectionThread;
    private CommunicationThread         communicationThread;
    // Auxiliar
    private boolean                     freeze;
    private boolean                     topDown;
    private int                         count;
    private String                      msgTotal;
    // Bonitor File
    File                                file;
    FileWriter                          fileWriter;
    boolean                             fileWrited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonitor);
        // Log
        Log.e(TAG, "BonitorActivity - onCreate()");
        // Views
        scrollView   = (ScrollView)     findViewById(R.id.monitorScroll);
        linearLayout = (LinearLayout)   findViewById(R.id.monitorLinear);
        txt          = (EditText)       findViewById(R.id.txt);
        // Bluetooth
        bt = Bluetooth.getInstance();
        beginConnection();
        // Aux
        freeze = false;
        topDown = false;
        count = 0;
        msgTotal = "";
        // File
        file = null;
        fileWriter = null;
        fileWrited = false;
        // Begin the program
        initialPrint(true);
    }

    // Begin BT connection thread
    private void beginConnection(){
        Log.i(TAG, "BonitorActivity - beginConnection()");
        int element = getIntent().getIntExtra("DEVICE", -1);
        Log.v(TAG, "BT element = " + element);
        // Begin Bluetooth Connection
        if(connectionThread != null){
            connectionThread.cancel();
        }
        connectionThread = new ConnectionThread(bt.getElement(element));
        connectionThread.start();
    }

    // Begin BT communicaction thread
    private void beginCommunication(BluetoothSocket socket){
        Log.i(TAG, "BonitorActivity - beginComunication()");
        // Begin Bluetooth Communication
        if(communicationThread != null){
            communicationThread.cancel();
        }
        communicationThread = new CommunicationThread(socket);
        communicationThread.start();
    }

    // Click one button
    public void click(View v){
        Log.i(TAG, "BonitorActivity - click()");
        switch (v.getId()){
            case R.id.btnSend: // Send data
                if(communicationThread != null){
                    // To Arduino
                    String msg = txt.getText().toString();
                    Log.v(TAG, "SEND -> " + msg);
                    communicationThread.write(msg);
                    // To Monitor
                    //String aux = msgTotal;
                    //msgTotal = msg + '\n';
                    printMonitor(msg + '\n');
                    //msgTotal = aux;
                }
                txt.setText("");
                break;
            case R.id.btnFreeze: // Freeze monitor
                if( ((ToggleButton)v).isChecked() ){
                    Log.v(TAG, "FREEZE");
                    freeze = true;
                }else{
                    Log.v(TAG, "NOT FREEZE");
                    freeze = false;
                }
                break;
            case R.id.btnTopDown: // TopDown
                if( ((ToggleButton)v).isChecked()){
                    topDown = true;
                } else {
                    topDown = false;
                }
            case R.id.btnClean: // Clean monitor
                Log.v(TAG, "CLEAN MONITOR");
                linearLayout.removeAllViews();
                count = 0;
                break;
        }
    }

    // Initial prints
    private void initialPrint(boolean b){
        Log.i(TAG, "BonitorActivity - printInitial()");
        String aux = "";
        if (b){
            aux = getString(R.string.connecting);
        }else{
            aux = getString(R.string.connected);
        }
        TextView t = new TextView(getApplicationContext());
        t.setText(aux);
        t.setTextColor(Color.BLACK);
        linearLayout.addView(t, count);
        count++;
        scrollView.requestChildFocus(linearLayout, t);
    }

    // Print outputs
    private void printMonitor(String msg){
        Log.i(TAG, "BonitorActivity - printMonitor()");
        if(!freeze){
            Log.d(TAG, "output = " + msgTotal);
            TextView t = new TextView(getApplicationContext());
            t.setText(msg);
            t.setTextColor(Color.BLACK);
            if(topDown){
                linearLayout.addView(t, 0);
            }else{
                linearLayout.addView(t, count);
            }
            count++;
            scrollView.requestChildFocus(linearLayout, t);
        }
        // Save in file
        try{
            logSave();
        } catch (IOException e){
            Log.e(TAG, "Problem with file = " + e);
            e.printStackTrace();
        }
    }

    // Save outputs in a file
    private void logSave() throws IOException{
        Log.i(TAG, "BonitorActivity - logSave()");
        // Create a text File
        if(file == null){
            Log.e(TAG, "File Created");
             // File name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String txtFileName = "LOG_" + timeStamp; // + "_";
            File storageDir = new File( Environment.getExternalStorageDirectory(), "Arduino Bonitor");
            storageDir.mkdirs();
            file = new File(storageDir, txtFileName + ".txt");
        }
        // Open file
        if(fileWriter == null){
            Log.e(TAG, "File Opened");
            fileWriter = new FileWriter(file);
        }
        // Write in file
        fileWriter.append(msgTotal+'\n');
        fileWriter.flush();
    }

    // Back button pressed
    @Override
    public void onBackPressed(){
        Log.e(TAG, "BonitorActivity - onBackPressed()");
        if(connectionThread != null){
            connectionThread.cancel();
        }
        if(communicationThread != null){
            communicationThread.cancel();
        }
        if(fileWriter != null){
            try{
                fileWriter.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        finish();
    }

    // onDestroy
    @Override
    protected void onDestroy(){
        Log.e(TAG, "BonitorActivity - onDestroy()");
        super.onDestroy();
        if(connectionThread != null){
            connectionThread.cancel();
        }
        if(communicationThread != null){
            communicationThread.cancel();
        }
        if(fileWriter != null){
            try{
                fileWriter.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        finish();
    }

    /** Threads **/

    // Handler to communicate Bluetooth Threads with the UI Thread
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.w(TAG, "Handler = " + msg.obj.toString());
            // Create input message
            String msgAux = (String)msg.obj;
            if(!msgAux.equals("btConnected")){
                for(int i = 0; i < msgAux.length(); i++){
                    if(msgAux.charAt(i) == '\n'){
                        // Show input meesage
                        printMonitor(msgTotal);
                        msgTotal = "";
                        break;
                    }
                    msgTotal += msgAux.charAt(i);
                }
            }else if(msgAux.equals("btConnected")){
                initialPrint(false);
            }

        }
    };

    // Thread to Connect via Bluetooth
    private class ConnectionThread extends Thread {

        // Atributes
        private final BluetoothAdapter  connectAdapter;
        private final BluetoothSocket   connectSocket;
        private static final String     UUID_CODE = "00001101-0000-1000-8000-00805F9B34FB";
        private int                     count = 0;

        public ConnectionThread(BluetoothDevice device) {
            Log.w(TAG, "ConnectionThread()");
            // Temporary socket because connectSocket is final
            BluetoothSocket tmp = null;
            connectAdapter = BluetoothAdapter.getDefaultAdapter();
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // Secure connection
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(UUID_CODE));
                // Insecure connection
                //tmp = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(UUID_CODE));
            } catch (IOException e) { }
            connectSocket = tmp;
        }

        // Begin Bluetooth connection
        public void run() {
            Log.w(TAG, "ConnectionThread - run()");
            // Cancel discovery because it will slow down the connection
            connectAdapter.cancelDiscovery();
            try {
                // Connect through the socket will block until it succeeds or throws an exception
                connectSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    connectSocket.close();
                    // If connection fail less than 2 times
                    Log.w(TAG, "Connection fail " + count + 1 + " times");
                    if(count < 2) {
                        count++;
                        beginConnection();
                    }
                } catch (IOException closeException) { }
                return;
            }
            // Do work to manage the communication in CommunicationThread
            Message msg = new Message();
            msg.obj = "btConnected";
            handler.sendMessage(msg);
            beginCommunication(connectSocket);
        }

        // Cancel an in-progress connection and close the socket
        public void cancel() {
            Log.w(TAG, "ConnectionThread - cancel()");
            try {
                connectSocket.close();
            } catch (IOException e) { }
        }

    }

    // Thread para comunicarse con otro dispositivo
    private class CommunicationThread extends Thread {

        // Atributes
        private final BluetoothSocket   communicationSocket;
        private final InputStream       communicationInStream;
        private final OutputStream      communicationOutStream;

        public CommunicationThread(BluetoothSocket socket) {
            Log.w(TAG, "CommunicationThread()");
            communicationSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // To final Input and Output streams we use temp objects
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            communicationInStream = tmpIn;
            communicationOutStream = tmpOut;
        }

        public void run() {
            Log.w(TAG, "CommunicationThread - run()");
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; 						 // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = communicationInStream.read(buffer);
                    Log.w(TAG,"bytes = " + bytes);
                    String inStr = new String(buffer, 0, bytes);
                    Log.w(TAG, "msg = "+ inStr);
                    // Send msg to UI Thread
                    Message msg = new Message();
                    msg.obj = inStr;
                    handler.sendMessage(msg);
                } catch (IOException e) { break; }
            }
        }

        // Send data via Bluetooth
        public void write(String msg) {
            Log.w(TAG,"CommunicationThread() - write()");
            byte[] bytes = msg.getBytes();
            try {
                communicationOutStream.write(bytes);
            } catch (IOException e) {}
        }

        // Shutdown the communication
        public void cancel() {
            Log.w(TAG,"CommunicationThread() - write bytes");
            try {
                //communicationInStream.close();
                //communicationOutStream.close();
                communicationSocket.close();
            } catch (IOException e) { }
        }
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
