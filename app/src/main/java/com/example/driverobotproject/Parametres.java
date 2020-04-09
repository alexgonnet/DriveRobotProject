package com.example.driverobotproject;

/**
 *Manage the "Parametres" menu
 * @author Alex GONNET
 * @version 2
 */

/*************** Add a scroll to the  devices display ***********************/

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

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

        pairDevice.setBackgroundColor(rgb(238,238,238));
        if(Singleton.getInstance().bluetooth.bluetoothIsAvailable() != null) {
            enableBT.setChecked(Singleton.getInstance().bluetooth.bluetoothIsActive());
            displayConnectedDevices();
        }else {
            enableBT.setEnabled(false);
        }

        if(!initLumSensor()){
            cLum.setEnabled(false);
        }

        if (Settings.System.getInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)==1){
            cLum.setChecked(true);
        }


        getPermission();
        seekBar.setMax(255);
        seekBar.setProgress(getBrightness());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && success){
                    setBrightness(progress);
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

            }
        });


    }

    public void switchBluetooth(View view){
        Singleton.getInstance().bluetooth.bluetoothEnable(enableBT.isChecked());
        if(enableBT.isChecked()){
            displayConnectedDevices();
            //Refresh the screen
        }else {
            removeDevices();
        }
    }

    private void displayConnectedDevices(){
        if(Singleton.getInstance().bluetooth.bluetoothIsAvailable() != null && Singleton.getInstance().bluetooth.bluetoothIsActive()) {
            //Display all the devices
            ArrayList<BluetoothDevice> bD = Singleton.getInstance().bluetooth.bluetoothListDevices();
            //BluetoothListAdapter bluetoothListAdapter = new BluetoothListAdapter(this, bD);
            ArrayAdapter bluetoothListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, Singleton.getInstance().list);
            //list.setAdapter(bluetoothListAdapter);
        }
    }

    private void displayPairDevices(){

        if(Singleton.getInstance().bluetooth.bluetoothIsAvailable() != null) {
            Singleton.getInstance().bluetooth.bluetoothSearchDevices();
            //Display all the devices
            BluetoothListAdapter bluetoothListAdapter = new BluetoothListAdapter(this, Singleton.getInstance().devices);
            list.setAdapter(bluetoothListAdapter);
        }
    }

    private void removeDevices(){
        list.removeAllViews();
    }

    public void switchLumAuto(View v){
        enableAutoLum(cLum.isChecked());
    }

    public void paramConnectedDevices(View view){
        connectedDevices.setBackgroundColor(rgb(255,255,255));
        pairDevice.setBackgroundColor(rgb(238,238,238));
        if(Singleton.getInstance().bluetooth.bluetoothIsActive()){
            displayConnectedDevices();
        }
    }

    public void paramPairDevice(View view){
        pairDevice.setBackgroundColor(rgb(255,255,255));
        connectedDevices.setBackgroundColor(rgb(238,238,238));
        if(Singleton.getInstance().bluetooth.bluetoothIsActive()){
            Singleton.getInstance().bluetooth.bluetoothSearchDevices();
            displayPairDevices();
        }
    }



    /**********************************************/
    /***************Luminosity********************/

    private boolean initLumSensor(){
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor photometre = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        //Sensor exist
        if(photometre != null){
            return true;
        }else {
            return false;
        }
    }

    private void setBrightness(int brightness){
        if(brightness < 0){
            brightness = 0;
        }else if (brightness > 255){
            brightness = 255;
        }

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
    }


    private int getBrightness(){
        int brightness = 100;
        try {
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
        }
        return brightness;
    }


    private void getPermission(){
        boolean value;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            value = Settings.System.canWrite(getApplicationContext());
            if(value){
                success = true;
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData((Uri.parse("package:" + getApplicationContext().getPackageName())));
                startActivityForResult(intent, 10000);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1000){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                boolean value = Settings.System.canWrite(getApplicationContext());
                if(value){
                    success = true;
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void enableAutoLum(boolean state){
        if (state){
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        } else {
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
    }
}

