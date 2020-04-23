package com.example.driverobotproject;

import android.bluetooth.BluetoothDevice;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 *  Class Singleton
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

    /**
     * LumSensor object
     */
    LumSensor lumSensor;

    /**
     * List of Bluetooth devices
     */
    ArrayList<BluetoothDevice> devices;

    /**
     * The parametres activity activity
     */
    AppCompatActivity aCA;

    /**
     * The main activity activity
     */
    AppCompatActivity aCAMainAct;

    /**
     * Personalized listview adapter
     */
    BluetoothListAdapter adapter;

    /**
     * The token
     */
    String token;
}
