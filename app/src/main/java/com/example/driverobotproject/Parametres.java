package com.example.driverobotproject;

/**
 *
 * @author Alex GONNET
 * @version 2
 */

/*************** Add a scroll to the  devices display ***********************/

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Parametres extends AppCompatActivity {

    private Bluetooth bluetooth;
    private Switch enableBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametres);
        bluetooth = Singleton.getInstance().bluetooth;
        enableBT = findViewById(R.id.switchBluetooth);
        enableBT.setChecked(bluetooth.bluetoothIsActive());
        displayDevices();
    }

    public void switchBluetooth(View view){
        bluetooth.bluetoothEnable(enableBT.isChecked());
    }

    private void displayDevices(){
        if(Singleton.getInstance().bluetooth.bluetoothIsActive()) {
            LinearLayout l = findViewById(R.id.linearLayoutDevices);
            final ArrayList<BluetoothDeviceCaracteristics> devices = Singleton.getInstance().bluetooth.bluetoothListDevices();
            //Display all the devices
            for (int i = 0; i < devices.size(); i++) {
                TextView tV = new TextView(this);
                tV.setText(devices.get(i).getName());
                tV.setId(i);
                tV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //connection
                        Toast.makeText(getApplicationContext(), "Address = " + devices.get(v.getId()).getAddress(), Toast.LENGTH_LONG).show();
                    }
                });

                l.addView(tV);
            }
            l.setVisibility(View.VISIBLE);
        }
    }
}
