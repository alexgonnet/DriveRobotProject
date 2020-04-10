package com.example.driverobotproject;

/**
 *Manage the "Parametres" menu
 * @author Alex GONNET
 * @author Benjamin BOURG
 * @version 3
 */

/*************** Add a scroll to the  devices display ***********************/

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

import java.util.EventListener;

import static android.graphics.Color.rgb;

public class Parametres extends AppCompatActivity implements EventListener, BluetoothCallback  {

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
                Toast.makeText(getApplicationContext(), "Connection", Toast.LENGTH_SHORT).show();
                BluetoothManager.getInstance().connect(Singleton.getInstance().devices.get(i));
                Toast.makeText(getApplicationContext(), "End connection", Toast.LENGTH_SHORT).show();
            }
        });

        enableBT.setChecked(BluetoothManager.getInstance().isBluetoothOn());
        enableBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    BluetoothManager.getInstance().turnOnBluetooth(Parametres.this);
                    while(!BluetoothManager.getInstance().isBluetoothOn());
                    displayConnectedDevices();
                }else{
                    BluetoothManager.getInstance().turnOffBluetooth();
                    removeDevices();
                }
            }
        });

        if (BluetoothManager.getInstance().isBluetoothOn()){
            displayConnectedDevices();
            Toast.makeText(getApplicationContext(), "Premier", Toast.LENGTH_SHORT).show();
        }

    }

    private void displayConnectedDevices(){
        BluetoothManager.getInstance().bluetoothListDevices();
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, Singleton.getInstance().list);
        list.setAdapter(adapter);
        Toast.makeText(getApplicationContext(), "Display", Toast.LENGTH_SHORT).show();
    }

    private void displayPairDevices(){

    }


    private void removeDevices(){
        list.setAdapter(null);
    }

    public void switchLumAuto(View v){
        Singleton.getInstance().lumSensor.enableAutoLum(cLum.isChecked());
    }

    public void paramConnectedDevices(View view){
        connectedDevices.setBackgroundColor(rgb(255,255,255));
        pairDevice.setBackgroundColor(rgb(238,238,238));
        if(BluetoothManager.getInstance().isBluetoothOn()){
            displayConnectedDevices();
        }
    }

    public void paramPairDevice(View view){
        pairDevice.setBackgroundColor(rgb(255,255,255));
        connectedDevices.setBackgroundColor(rgb(238,238,238));
        if(BluetoothManager.getInstance().isBluetoothOn()){
            //Singleton.getInstance().bluetooth.bluetoothSearchDevices();
            displayPairDevices();
        }
    }






    /***********************************************************/
    /***********************************************************/
    /*************************BLUETOOTH*************************/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int resultBluetooth = BluetoothManager.getInstance().CheckActivityResult(requestCode,
                resultCode);

        if(resultBluetooth == BluetoothManager.BLUETOOTH_ON){
            enableBT.setChecked(true);
        }else if(resultBluetooth == BluetoothManager.BLUETOOTH_OFF){
            enableBT.setChecked(false);
        }/*else if(resultBluetooth == BluetoothManager.BLUETOOTH_DISCOVERY_LISTEN){
            toggleButtonVisible.setEnabled(false);
            spinnerDiscovering.setVisibility(View.VISIBLE);
            buttonConnect.setVisibility(View.INVISIBLE);
        }else if(resultBluetooth == BluetoothManager.BLUETOOTH_DISCOVERY_CANCELED){
            toggleButtonVisible.setEnabled(true);
            toggleButtonVisible.setChecked(false);
            spinnerDiscovering.setVisibility(View.INVISIBLE);
            buttonConnect.setVisibility(View.VISIBLE);
        }*/

        super.onActivityResult(requestCode,resultCode,data);
    }


    //callback for when the device is connected or an error occurred. When the connection os ok,
    // it starts a new Activity for the message exchange
    @Override
    public void onBluetoothConnection(int returnCode) {
        Log.d("BT","onBluetoothConnection"+returnCode);
        if(returnCode == BluetoothManager.BLUETOOTH_CONNECTED){
            Toast.makeText(Parametres.this, "Connected",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);

            startActivity(intent);
        }else if(returnCode == BluetoothManager.BLUETOOTH_CONNECTED_ERROR){
            Toast.makeText(Parametres.this, "ConnectionError",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBluetoothDiscovery(int returnCode) {

    }

    @Override
    public void onReceiveData(String data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1001: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // --->
                    Log.d("BT", "permisionGranted");
                } else {
                    //TODO re-request
                    Log.d("BT", "permisionNOTGranted");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 1001);

                }
                break;
            }
        }


    }









}

