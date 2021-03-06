package com.example.driverobotproject;

/**
 * Created by markjoselli2015 on 4/14/15.
 */
public interface BluetoothCallback {

    //Callback to see how the connection between the devices has been
    abstract void onBluetoothConnection(int returnCode);
    //Callback in order to see how the Discovering is behaving
    abstract void onBluetoothDiscovery(int returnCode);
    //Callback to receive data between a BT connection
    abstract void onReceiveData(String data);
}
