package com.example.driverobotproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Bluetooth manager class
 * @author Benjamin BOURG
 * @version 1.3
 */
public class BluetoothManager{

    private static final BluetoothManager instance = new BluetoothManager();
    public static BluetoothManager getInstance() { return instance; }

    /**
     * Enable BT code
     */
    public static int REQUEST_ENABLE_BLUETOOTH = 1;

    /**
     * The UUID
     */
    private static UUID MY_UUID;

    /**
     * BT socket
     */
    private BluetoothSocket mBtSocket;

    /**
     * Constants that indicate the current connection state
     */
    public static final int STATE_LISTENING = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    public static final int STATE_CONNECTION_FAILED = 4;  // connection to a remote device failed
    public static final int STATE_MESSAGE_RECEIVED = 5;  // message received from a remote device

    /**
     * Object for the emission and reception of a message
     */
    SendReceive sendReceive;

    /**
     * State of the Bluetooth
     */
    boolean connected = false;

    /**
     * The Bluetooth adapter
     */
    private BluetoothAdapter myBluetoothAdapter;

    /**
     * Initialisation of the BT connection
     */
     public boolean initializeBluetooth(String uuid){
        Log.e("BluetoothAdapter","initializeBluetooth");
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //If the device doesn't have BT
        if(myBluetoothAdapter == null) {
            Log.e("BluetoothAdapter","myBluetoothAdapter == null");
            return false;
        }
        //If the BT is not enable, the app asks for the BT
        if(!myBluetoothAdapter.isEnabled()){
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Singleton.getInstance().aCAMainAct.startActivityForResult(enableBluetooth, REQUEST_ENABLE_BLUETOOTH);
        }
        MY_UUID = UUID.fromString(uuid);

        return true;
    }


    /**
     * Close the BT
     * @param activity The activity
     */
    public void closeBluetooth(Activity activity){
        if(mBtSocket != null){
            try{
                mBtSocket.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            mBtSocket = null;
        }

        activity.unregisterReceiver(myReceiver);
    }

    /**
     * Turn on the Bluetooth
     * @param activity The activity
     * @return REQUEST_ENABLE_BT if BT is not enable else 0
     */
    public int turnOnBluetooth(Activity activity){
        //If the BT is not active, the app asks the permission to activate it
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(turnOnIntent, REQUEST_ENABLE_BLUETOOTH);
            return REQUEST_ENABLE_BLUETOOTH;
        }
        return 0;

    }

    /**
     * Turn off the Bluetooth
     */
    public void turnOffBluetooth(){
        myBluetoothAdapter.disable(); //Turn off BT
    }

    /**
     * Get the state of the Bluetooth
     * @return true if activate otherwise false
     */
    public boolean isBluetoothOn(){
        return myBluetoothAdapter.isEnabled();
    }







/************************************************************************************************************************/







    /**
     * List all the connected BT devices
     * @return devices a list of BT devices
     */
    public void bluetoothListDevices(){
        Set<BluetoothDevice> periphAppaires = myBluetoothAdapter.getBondedDevices();
        //Initialize the devices list
        Singleton.getInstance().devices = new ArrayList<BluetoothDevice>();
        for(BluetoothDevice bD : periphAppaires)
        {
            //Add the device to the list
            Singleton.getInstance().devices.add(bD);
        }
    }


    /**
     * Active research unpaired device
     */
    public void bluetoothSearchDevices(){
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        Singleton.getInstance().aCA.registerReceiver(myReceiver, intentFilter);
        //Start the discovery of unpaired devices
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
                //Add the device to the list
                Singleton.getInstance().devices.add(device);

                //Refresh the ListView
                Singleton.getInstance().adapter.notifyDataSetChanged();
            }
        }
    };


    /**
     * Handle the connection
     */
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
                    //In case of danger, the robot send "x"
                    if (tmpMsg.equals("x")){
                        dangerAlert(); //Call the method for the notification
                    }
                   break;
           }
           return true;
        }
    });


    /**
     * Connection to a BT device
     * @param device the device
     */
    public void connect(BluetoothDevice device){
        ClientClass clientClass = new ClientClass(device);
        //Start the connection to the device
        clientClass.start();
    }


    /**
     * Handle the connection
     */
    private class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        /**
         * Constructor
         * @param device the device to connect
         */
        public ClientClass (BluetoothDevice device){
            this.device = device;
            myBluetoothAdapter.cancelDiscovery();
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                connected = true;
                Toast.makeText(Singleton.getInstance().aCA.getApplicationContext(),
                        "Connection successful",Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(Singleton.getInstance().aCA.getApplicationContext(),
                        "Connection failed",Toast.LENGTH_SHORT).show();
            }
        }

        public void run(){
            try {
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                //Send a message to the device
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e){
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }


    /**
     * Send a message
     * @param msg the message
     */
    public void senReceiveMsg(String msg){
        sendReceive.write(msg.getBytes());
    }

    /**
     * Class to send or receive a message throw BT
     */
    private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        /**
         * Constructor
         * @param socket the socket to handle the connection
         * @throws IOException
         */
        public SendReceive (BluetoothSocket socket) throws IOException {
            bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            tmpIn = bluetoothSocket.getInputStream();
            tmpOut = bluetoothSocket.getOutputStream();
            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        /**
         * Class runs when the app receives a message
         */
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

        /**
         * Class used to send a message
         * @param bytes the message in byte
         */
        public void write(byte[] bytes){
            try {
                outputStream.write(bytes); //Write the data to the output buffer
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send a message to the firebase to get a notification
     */
    private void dangerAlert(){
        //Connection to the php file to send a notification to the device throw FCM
        (new Connectivity()).execute("https://benphototravel.000webhostapp.com/notif.php?send&token="+Singleton.getInstance().token);
        Log.i("Danger", "dangerAlert: "+Singleton.getInstance().token);
    }
}

