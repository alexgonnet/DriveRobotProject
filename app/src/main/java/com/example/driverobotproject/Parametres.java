package com.example.driverobotproject;

/**
 *Manage the "Parametres" menu
 * @author Alex GONNET
 * @author Benjamin BOURG
 * @version 3
 */


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.EventListener;

import static android.graphics.Color.rgb;

public class Parametres extends AppCompatActivity implements EventListener  {

    private Switch enableBT;
    private ListView list;
    private CheckBox cLum;
    private SeekBar seekBar;
    private TextView pairDevice;
    private TextView connectedDevices;
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametres);

        enableBT = findViewById(R.id.switchBluetooth);
        list = findViewById(R.id.listViewDevices);
        cLum = findViewById(R.id.checkBoxLuminosite);
        seekBar = findViewById(R.id.seekBar);
        pairDevice = findViewById(R.id.textViewPairDevice);
        connectedDevices = findViewById(R.id.textViewConnectedDevices);

        //Get the activity
        Singleton.getInstance().aCA = this;

        pairDevice.setBackgroundColor(rgb(238,238,238));

        if(!Singleton.getInstance().lumSensor.initLumSensor(this)){
            cLum.setEnabled(false);
        }

        if (Settings.System.getInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)==1){
            cLum.setChecked(true);
        }


        //Singleton.getInstance().lumSensor.getPermission();
        seekBar.setMax(255);
        seekBar.setProgress(Singleton.getInstance().lumSensor.getBrightness());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && Singleton.getInstance().lumSensor.success){
                    Singleton.getInstance().lumSensor.setBrightness(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!success){
                    Toast.makeText(getApplicationContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int i, long l) {
                BluetoothManager.getInstance().connect(Singleton.getInstance().devices.get(i));
            }
        });

        enableBT.setChecked(BluetoothManager.getInstance().isBluetoothOn());
        enableBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    BluetoothManager.getInstance().turnOnBluetooth(Parametres.this);
                    while(!BluetoothManager.getInstance().isBluetoothOn());
                    displayPairedDevices();
                }else{
                    BluetoothManager.getInstance().turnOffBluetooth();
                    removeDevices();
                }
            }
        });

        if (BluetoothManager.getInstance().isBluetoothOn()){
            displayPairedDevices();
        }

    }

    private void displayPairedDevices(){
        BluetoothManager.getInstance().bluetoothListDevices();
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, Singleton.getInstance().list);
        list.setAdapter(adapter);
    }

    private void displaySearchDevices(){
        Singleton.getInstance().list = new ArrayList<String>();
        Singleton.getInstance().adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, Singleton.getInstance().list);
        BluetoothManager.getInstance().bluetoothSearchDevices();
        list.setAdapter(Singleton.getInstance().adapter);
    }


    private void removeDevices(){
        list.setAdapter(null);
    }

    public void switchLumAuto(View v){
        Singleton.getInstance().lumSensor.enableAutoLum(cLum.isChecked());
    }

    public void paramPairedDevices(View view){
        connectedDevices.setBackgroundColor(rgb(255,255,255));
        pairDevice.setBackgroundColor(rgb(238,238,238));
        if(BluetoothManager.getInstance().isBluetoothOn()){
            displayPairedDevices();
        }
    }

    public void paramSearchDevice(View view){
        pairDevice.setBackgroundColor(rgb(255,255,255));
        connectedDevices.setBackgroundColor(rgb(238,238,238));
        if(BluetoothManager.getInstance().isBluetoothOn()){
            //Singleton.getInstance().bluetooth.bluetoothSearchDevices();
            displaySearchDevices();
        }
    }






    /***********************************************************/
    /***********************************************************/
    /*************************BLUETOOTH*************************/









}

