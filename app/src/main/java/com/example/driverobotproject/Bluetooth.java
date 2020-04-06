package com.example.driverobotproject;

/**
 * Manage all the bluetooth part
 * @author Benjamin BOURG
 * @version 1
 */

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Bluetooth {

    private BluetoothAdapter bA;
    private BroadcastReceiver bR;
    private Context context;
    private AppCompatActivity aCA;

    /**
     * Constructor
     * @param context
     * @param aCA
     */
    public Bluetooth(Context context, AppCompatActivity aCA) {
        this.context = context;
        this.aCA = aCA;
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
    public ArrayList<BluetoothDeviceCaracteristics> bluetoothListDevices(){
        Set<BluetoothDevice> periphAppaires = bA.getBondedDevices();
        ArrayList<BluetoothDeviceCaracteristics> devices = new ArrayList();
        for(BluetoothDevice bD : periphAppaires){
            devices.add(new BluetoothDeviceCaracteristics(bD.getName(), bD.getAddress()));
        }
        return devices;
    }

/*
    public void bluetoothSearchDevices(){
        bR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                    BluetoothDevice bD = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    tV.append(bD.getName() + " : " + bD.getAddress() + "\n\n");
                }

                if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                    tV.append("Recherche finie\n\n");
                }
            }
        };

        bA.startDiscovery();
        registerReceiver(bR, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(bR, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }


    public void bluetoothConnectDevice(){

    }


*/

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

/*
    protected void onDestroy(){
        super.onDestroy();
        bA.cancelDiscovery();
        unregisterReceiver(bR);
    }*/
}
