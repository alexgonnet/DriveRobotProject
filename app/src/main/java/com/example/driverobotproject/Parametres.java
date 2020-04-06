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
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Parametres extends AppCompatActivity {

    private Bluetooth bluetooth;
    private Switch enableBT;
    private LinearLayout l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametres);
        bluetooth = Singleton.getInstance().bluetooth;
        enableBT = findViewById(R.id.switchBluetooth);
        l = findViewById(R.id.linearLayoutDevices);
        if(bluetooth.bluetoothIsAvailable() != null) {
            enableBT.setChecked(bluetooth.bluetoothIsActive());
            displayDevices();
        }else {
            enableBT.setEnabled(false);
        }
    }

    public void switchBluetooth(View view){
        bluetooth.bluetoothEnable(enableBT.isChecked());
        if(enableBT.isChecked()){
            displayDevices();
            //Refresh the screen
        }else {
            removeDevices();
        }
    }

    private void displayDevices(){
<<<<<<< HEAD
        if(bluetooth.bluetoothIsActive()) {
            ScrollView l = findViewById(R.id.scrollViewDevices);
        final ArrayList<BluetoothDeviceCaracteristics> devices = Singleton.getInstance().bluetooth.bluetoothListDevices();
=======
        if(bluetooth.bluetoothIsAvailable() != null) {
            final ArrayList<BluetoothDeviceCaracteristics> devices = Singleton.getInstance().bluetooth.bluetoothListDevices();
>>>>>>> 9ca8d1bd05e052a4f21a016cdb6926c8aa85951a
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

    private void removeDevices(){
        l.removeAllViews();
    }
}
