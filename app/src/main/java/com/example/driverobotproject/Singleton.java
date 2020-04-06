package com.example.driverobotproject;

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
    Bluetooth bluetooth;
}
