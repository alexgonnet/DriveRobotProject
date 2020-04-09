package com.example.driverobotproject;

/**
 * Manage all the bluetooth part
 * @author Benjamin BOURG
 * @version 1
 */

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Bluetooth{

    private BluetoothAdapter bA;
    private BroadcastReceiver bR;
    private Context context;
    private AppCompatActivity aCA;
    private UUID MY_UUID;
    private BluetoothSocket socket;

    /**
     * Constructor
     * @param context
     * @param aCA
     */
    public Bluetooth(Context context, AppCompatActivity aCA) {
        this.context = context;
        this.aCA = aCA;
        this.MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        bluetoothInit();
    }

    /**
     * Initialisation of the BT connection
     */
    public void bluetoothInit(){
        bA = BluetoothAdapter.getDefaultAdapter();
        int REQUEST_ENABLE_BLUETOOTH = 1;
        //Présence du BT
        if(bA != null){
            //Vérification si le BT est activé
            if(bA.isEnabled()){
                //bluetoothListDevices();
               // bluetoothSearchDevices();
            } else {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                aCA.startActivityForResult(enableBluetooth, REQUEST_ENABLE_BLUETOOTH);
                //onActivityResult(REQUEST_ENABLE_BLUETOOTH, RESULT_OK, enableBluetooth);
            }
        }
        //Absence du BT
        else {
            Toast.makeText(context,"The Bluetooth is not available on your device",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * List all the connected BT devices
     * @return devices a list of BT devices
     */
    public ArrayList<BluetoothDevice> bluetoothListDevices(){
        Set<BluetoothDevice> periphAppaires = bA.getBondedDevices();
        ArrayList<BluetoothDevice> devices = new ArrayList();
        for(BluetoothDevice bD : periphAppaires){
            devices.add(bD);
            Singleton.getInstance().list.add(bD.getName());
            //Toast.makeText(aCA.getApplicationContext(), "UUID device = "+(bD.getUuids())[0].getUuid() , Toast.LENGTH_LONG).show();
        }
        return devices;
    }


    public void bluetoothSearchDevices(){
       Singleton.getInstance().devices = new ArrayList();
        bR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                    BluetoothDevice bD = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //Singleton.getInstance().devices.add(new BluetoothDeviceCaracteristics(bD.getName(), bD.getAddress(), (bD.getUuids())[0].getUuid()));
                    Singleton.getInstance().devices.add(bD);
                }

                if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                    Toast.makeText(aCA.getApplicationContext(), "Finish size = " + Singleton.getInstance().devices.size(), Toast.LENGTH_LONG).show();
                    bA.cancelDiscovery();
                }
            }
        };

        bA.startDiscovery();
        aCA.registerReceiver(bR, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        aCA.registerReceiver(bR, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }


    public void bluetoothConnectDevice(){

    }




    /**
     * Active or desactive the BT
     * @param state The current state of the BT
     */
    public void bluetoothEnable(boolean state){
        if(state){
            bA.enable();
            Toast.makeText(context,"on",Toast.LENGTH_LONG).show();
        }else {
            bA.disable();
            Toast.makeText(context,"off",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Get the state of BT
     * @return true if BT is active or false if BT is inactive
     */
    public boolean bluetoothIsActive(){
        return bA.isEnabled();
    }

    public BluetoothAdapter bluetoothIsAvailable(){
        return bA;
    }


    protected void onDestroy(){
        bA.cancelDiscovery();
        aCA.unregisterReceiver(bR);
    }

    public void connectDevices(int index) throws IOException {
        socket = Singleton.getInstance().devices.get(index).createRfcommSocketToServiceRecord(MY_UUID);
        socket.connect();
    }



    //AsyncTask to receive a single line of data and post
    private class SendDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            InputStream in = null;
            OutputStream out = null;
            try {
                //Send your data
                out = socket.getOutputStream();
                out.write(params[0].getBytes());
                //Receive the other's data


                in = socket.getInputStream();
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
          //  if(callback != null)
              //  callback.onReceiveData(result);
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
                    //callback.onBluetoothConnection(BLUETOOTH_CONNECTED_ERROR);
                    break;
                }
            }
        }

    }


}
