package com.example.driverobotproject;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

/**
 *
 * @author Benjamin BOURG
 * @version 1
 */


public class Singleton {
    private static Singleton instance = new Singleton();
    private Singleton(){

    }
    static public Singleton getInstance(){
        return instance;
    }
    LumSensor lumSensor;
    ArrayList<BluetoothDevice> devices;
    ArrayList<String> list;
}
