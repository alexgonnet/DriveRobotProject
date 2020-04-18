package com.example.driverobotproject;

import java.sql.Timestamp;
import java.util.Date;

public class Data {

    public String action;
    public String luminosity;
    public static String idProject = "30";
    public Timestamp timestamp;

    public Data(String action, String luminosity){
        this.action = action;
        this.luminosity = luminosity;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
}
