package com.example.driverobotproject;

import java.util.UUID;

/**
 * Class to define a bluetooth device
 * @author Benjamin BOURG
 * @version 1
 */


public class BluetoothDeviceCaracteristics {
    private String name;
    private String address;
    private UUID uuid;

    public BluetoothDeviceCaracteristics (String name, String address, UUID uuid){
        this.name = name;
        this.address = address;
        this.uuid = uuid;
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

    /**
     * Get the UUID of the device
     * @return uuid the UUID
     */
    public UUID getUuid(){ return uuid;}
}
