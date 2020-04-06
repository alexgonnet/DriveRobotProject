package com.example.driverobotproject;

/**
 * Class to define a bluetooth device
 * @author Benjamin BOURG
 * @version 1
 */


public class BluetoothDeviceCaracteristics {
    private String name;
    private String address;

    public BluetoothDeviceCaracteristics (String name, String address){
        this.name = name;
        this.address = address;
    }

    /**
     * Get the name of the device
     * @return name the name
     */
    public String getName(){
        return name;
    }

    /**
     * Get the address of the device
     * @return address the address
     */
    public String getAddress(){
        return address;
    }
}
