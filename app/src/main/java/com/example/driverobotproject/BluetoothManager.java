package com.example.driverobotproject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author Benjamin BOURG
 * @version 1.3
 */
public class BluetoothManager{


    
    private static final BluetoothManager instance = new BluetoothManager();
    public static BluetoothManager getInstance() { return instance; }
    public static int REQUEST_ENABLE_BLUETOOTH = 1;

    //CHANGE THE UUID FOR YOUR CODE
    private static UUID MY_UUID;
    private static String SEARCH_NAME;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_VISIBLE_BT = 2;
    private BluetoothSocket mBtSocket;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTENING = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    public static final int STATE_CONNECTION_FAILED = 4;  // now connected to a remote device
    public static final int STATE_MESSAGE_RECEIVED = 5;  // now connected to a remote device


    SendReceive sendReiceve;
    boolean connected = false;


    private BluetoothAdapter myBluetoothAdapter;

    /**
     * Initialisation of the BT connection
     */
     public boolean initializeBluetooth(Activity activity, String uuid, String name, AppCompatActivity aCA){
        Log.e("BluetoothAdapter","initializeBluetooth");
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null) {
            Log.e("BluetoothAdapter","myBluetoothAdapter == null");
            return false;
        }
        if(!myBluetoothAdapter.isEnabled()){
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            aCA.startActivityForResult(enableBluetooth, REQUEST_ENABLE_BLUETOOTH);
        }
        MY_UUID = UUID.fromString(uuid);
        SEARCH_NAME = name;

        activity.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        activity.registerReceiver(bReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
        return true;
    }


    public void closeBluetooth(Activity activity){
        if(mBtSocket != null){
            try{
                mBtSocket.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            mBtSocket = null;
        }

        activity.unregisterReceiver(bReceiver);
    }

    public int turnOnBluetooth(Activity activity){
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
            return REQUEST_ENABLE_BT;
        }

        return 0;

    }

    public void turnOffBluetooth(){
        myBluetoothAdapter.disable();
    }

    public boolean isBluetoothOn(){
        return myBluetoothAdapter.isEnabled();
    }

    public void makeBluetoothDiscoverable(Activity activity, int time, BluetoothCallback bluetoothCallback){
        Intent turnVisibleIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        turnVisibleIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
        activity.startActivityForResult(turnVisibleIntent, REQUEST_VISIBLE_BT);
        callback = bluetoothCallback;
    }

    public void startDiscover(BluetoothCallback bluetoothCallback){
        callback = bluetoothCallback;
        if(myBluetoothAdapter.isDiscovering())
            return;
        myBluetoothAdapter.startDiscovery();
        Log.d("BT","startDiscovery");
    }
    BluetoothCallback callback;
    void startListening(BluetoothCallback bluetoothCallback){
        callback = bluetoothCallback;
        AcceptTask task = new AcceptTask();
        task.execute(MY_UUID);
    }

    //AsyncTask to accept incoming connections
    private class AcceptTask extends AsyncTask<UUID, Void, BluetoothSocket> {

        @Override
        protected BluetoothSocket doInBackground(UUID... params) {
            String name = myBluetoothAdapter.getName();
            try {
                //While listening, set the discovery name to a specific value
                myBluetoothAdapter.setName(SEARCH_NAME);
                BluetoothServerSocket socket = myBluetoothAdapter
                        .listenUsingRfcommWithServiceRecord("Server", params[0]);
                BluetoothSocket connected = socket.accept();
                //Reset the BT adapter name
                myBluetoothAdapter.setName(name);
                return connected;
            } catch (IOException e) {
                e.printStackTrace();


                return null;
            }


        }
        @Override
        protected void onPostExecute(BluetoothSocket socket) {
            if(socket == null) {
                if(callback != null){
                    callback.onBluetoothConnection(BLUETOOTH_CONNECTED_ERROR);
                }
                return;
            }

            if(callback != null){
                callback.onBluetoothConnection(BLUETOOTH_CONNECTED);
            }

            mBtSocket = socket;

        }

    }



    public static final int BLUETOOTH_ON = 1000;
    public static final int BLUETOOTH_OFF = -1000;

    public static final int BLUETOOTH_DISCOVERY_LISTEN = 1001;
    public static final int BLUETOOTH_DISCOVERY_CANCELED = -1001;

    public static final int BLUETOOTH_CONNECTED = 1002;
    public static final int BLUETOOTH_CONNECTED_ERROR = -1002;

    public static final int BLUETOOTH_DISCOVERABLE = 1003;
    public static final int BLUETOOTH_CONNECTABLE = 1004;
    public static final int BLUETOOTH_NOT_CONNECTABLE = 1004;

    public int CheckActivityResult(int requestCode, int resultCode){
        Log.d("BT","CheckActivityResult"+requestCode);
        if(requestCode == REQUEST_ENABLE_BT) {
            if (myBluetoothAdapter.isEnabled()) {
                return BLUETOOTH_ON;
            } else {
                return BLUETOOTH_OFF;
            }
        }
        if(requestCode == REQUEST_VISIBLE_BT){
            if(resultCode == Activity.RESULT_CANCELED){

                return BLUETOOTH_DISCOVERY_CANCELED;
            }else{

                startListening(callback);
                return BLUETOOTH_DISCOVERY_LISTEN;
            }
        }

        return 0;
    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("BT","ACTION_FOUND");

                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                if(TextUtils.equals(device.getName(), SEARCH_NAME)) {
                    //Matching device found, connect
                    myBluetoothAdapter.cancelDiscovery();
                    try {
                        mBtSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                        mBtSocket.connect();

                        if(callback != null){
                            callback.onBluetoothConnection(BLUETOOTH_CONNECTED);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        if(callback != null){
                            callback.onBluetoothConnection(BLUETOOTH_CONNECTED_ERROR);
                        }

                    }
                }
            }else if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                Log.d("BT","ACTION_SCAN_MODE_CHANGED");

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,
                        BluetoothAdapter.ERROR);


                switch(mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        if(callback != null){
                            callback.onBluetoothDiscovery(BLUETOOTH_DISCOVERABLE);
                        }
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        if(callback != null){
                            callback.onBluetoothDiscovery(BLUETOOTH_CONNECTABLE);
                        }
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        if(callback != null){
                            callback.onBluetoothDiscovery(BLUETOOTH_NOT_CONNECTABLE);
                        }
                        break;
                }


            }



        }
    };

    public void sendData(BluetoothCallback bluetoothCallback, String data){
        callback = bluetoothCallback;
        SendDataTask task = new SendDataTask();
        task.execute(data);
    }
    ReadThread readThread;
    public void startReadingData(BluetoothCallback bluetoothCallback){
        callback = bluetoothCallback;
        readThread = new ReadThread(mBtSocket,callback);
        readThread.start();
    }

    public void stopReadingData(){
        if(readThread != null){
            readThread.interrupt();
            readThread = null;
        }

    }


    //AsyncTask to receive a single line of data and post
    private class SendDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            InputStream in = null;
            OutputStream out = null;
            try {
                //Send your data
                out = mBtSocket.getOutputStream();
                out.write(params[0].getBytes());
                //Receive the other's data


                in = mBtSocket.getInputStream();
                //byte[] buffer = new byte[1024];
                //in.read(buffer);
                //Create a clean string from results
                //String result = new String(buffer);
                //Close the connection
                //mBtSocket.close();
                return "SENDED";
            } catch (Exception exc) {

                return exc.getMessage();
            }
        }



        @Override
        protected void onPostExecute(String result) {
            if(callback != null)
                callback.onReceiveData(result);
        }

    }

    private class ReadThread extends Thread {
        BluetoothCallback callback;
        private BluetoothSocket mmSocket;
        private InputStream mmInStream;

        public ReadThread(BluetoothSocket socket, BluetoothCallback bluetoothCallback) {
            mmSocket = socket;
            callback = bluetoothCallback;
            InputStream tmpIn = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();

            } catch (IOException e) {
                Log.e("IOException", "temp sockets not created", e);
            }

            mmInStream = tmpIn;
        }

        public void run() {

            byte[] buffer = new byte[1024];
            int bytes;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String result = new String(buffer);
                    Log.d("DATA_RECEIVED",result.trim());
                    callback.onReceiveData(result.trim());

                } catch (IOException e) {
                    Log.e("IOException", "disconnected", e);
                    callback.onBluetoothConnection(BLUETOOTH_CONNECTED_ERROR);
                    break;
                }
            }
        }

    }







/************************************************************************************************************************/







    /**
     * List all the connected BT devices
     * @return devices a list of BT devices
     */
    public void bluetoothListDevices(){
        Set<BluetoothDevice> periphAppaires = myBluetoothAdapter.getBondedDevices();
        Singleton.getInstance().list = new ArrayList<String>();
        Singleton.getInstance().devices = new ArrayList<BluetoothDevice>();
        for(BluetoothDevice bD : periphAppaires){
            Singleton.getInstance().devices.add(bD);
            Singleton.getInstance().list.add(bD.getName());
        }
    }


    /**
     * Active research unpaired device
     */
    public void bluetoothSearchDevices(){
        Singleton.getInstance().devices = new ArrayList<BluetoothDevice>();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        Singleton.getInstance().aCA.registerReceiver(myReceiver, intentFilter);
        myBluetoothAdapter.startDiscovery();
    }

    /**
     * Search unpaired device
     */
    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Singleton.getInstance().devices.add(device);
                if(device.getName() == null) {
                    Singleton.getInstance().list.add(device.getAddress());
                }else {
                    Singleton.getInstance().list.add(device.getName());
                }
                Singleton.getInstance().adapter.notifyDataSetChanged();
            }
        }
    };







    Handler handler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
           switch (msg.what) {
               case STATE_LISTENING:

                   break;
               case STATE_CONNECTING:

                   break;
               case STATE_CONNECTED:

                   break;
               case STATE_CONNECTION_FAILED:

                   break;
               case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tmpMsg = new String(readBuff, 0, msg.arg1);
                    //Message à afficher
                   break;
           }
           return true;
        }
    });



    public void connect(BluetoothDevice device){
        ClientClass clientClass = new ClientClass(device);
        clientClass.start();
    }


    private class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass (BluetoothDevice device){
            this.device = device;
            myBluetoothAdapter.cancelDiscovery();
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                connected = true;
                Toast.makeText(Singleton.getInstance().aCA.getApplicationContext(), "Connection successful",Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(Singleton.getInstance().aCA.getApplicationContext(), "Connection failed",Toast.LENGTH_SHORT).show();
            }
        }

        public void run(){
            try {
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendReiceve = new SendReceive(socket);
                sendReiceve.start();
            } catch (IOException e){
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }


    public void senReceiveMsg(String msg){
        sendReiceve.write(msg.getBytes());
    }

    private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket) throws IOException {
            bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            tmpIn = bluetoothSocket.getInputStream();
            tmpOut = bluetoothSocket.getOutputStream();
            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while (true){
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Alert when there is an obstacle
     * !!!! Doesn't work, app close !!!!
     */
    public void receiveAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Singleton.getInstance().aCAMainAct.getApplicationContext());
        builder.setTitle("Info");
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setMessage("There is a danger from of you !!");
        AlertDialog aD = builder.create();
        aD.show();
        //Snackbar.make(Singleton.getInstance().aCAMainAct.findViewById(R.id.myCoordinatorLayout), "There is a danger from of you !!", Snackbar.LENGTH_SHORT).show();
    }
}

