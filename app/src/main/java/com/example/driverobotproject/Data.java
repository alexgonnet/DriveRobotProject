package com.example.driverobotproject;

import java.sql.Timestamp;
import java.util.Date;

public class Data {

    public String action;
    public int luminosity;
    public Timestamp timestamp;

    public Data(String action, int luminosity){
        this.action = action;
        this.luminosity = luminosity;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
}
