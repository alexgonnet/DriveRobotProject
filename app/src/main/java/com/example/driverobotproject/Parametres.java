package com.example.driverobotproject;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.EventListener;

import static android.graphics.Color.rgb;

/**
 * Manage the "Paramètres" menu
 * @author Alex GONNET
 * @author Benjamin BOURG
 * @version 3
 */

public class Parametres extends AppCompatActivity implements EventListener  {

    /**
     * Button to activate or deactivate the BT
     */
    private Switch enableBT;

    /**
     * List all the BT
     */
    private ListView list;

    /**
     * Checkbox to activate/deactivate the automatic luminosity
     */
    private CheckBox cLum;

    /**
     * Seekbar to adjust manually the luminosity
     */
    private SeekBar seekBar;

    /**
     * Text Unpaired device
     */
    private TextView unpairDevice;

    /**
     * Text : Connected devices
     */
    private TextView connectedDevices;

    /**
     * To know which type of devices are displayed
     */
    private boolean pairDisplay = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametres);

        //Get all the items from the xml file
        enableBT = findViewById(R.id.switchBluetooth);
        list = findViewById(R.id.listViewDevices);
        cLum = findViewById(R.id.checkBoxLuminosite);
        seekBar = findViewById(R.id.seekBar);
        unpairDevice = findViewById(R.id.textViewPairDevice);
        connectedDevices = findViewById(R.id.textViewConnectedDevices);

        //Get the activity
        Singleton.getInstance().aCA = this;

        unpairDevice.setBackgroundColor(rgb(238,238,238));

        //Disable the checkbox if luminosity sensor not available
        if(!Singleton.getInstance().lumSensor.initLumSensor(this)){
            cLum.setEnabled(false);
        }

        //Checked the checkbox if automatic luminosity is active
        if (Settings.System.getInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)==1){
            cLum.setChecked(true);
        }

        //Initialization of the seekbar
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
                if (!Singleton.getInstance().lumSensor.success){
                    Toast.makeText(getApplicationContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Initialisation of the listener on the ListView
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Connect to the device on click
             * @param adapterView
             * @param view
             * @param i
             * @param l
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int i, long l) {
                BluetoothManager.getInstance().connect(Singleton.getInstance().devices.get(i));
            }
        });

        //Initialization of the BT switch
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


    /**
     * Display paired devices
     */
    private void displayPairedDevices(){
        BluetoothManager.getInstance().bluetoothListDevices();
        BluetoothListAdapter adapter = new BluetoothListAdapter(getApplicationContext(), Singleton.getInstance().devices);
        list.setAdapter(adapter);
    }


    /**
     * Display unpaired devices
     */
    private void displaySearchDevices(){
        Singleton.getInstance().devices = new ArrayList<BluetoothDevice>();
        Singleton.getInstance().adapter = new BluetoothListAdapter(getApplicationContext(), Singleton.getInstance().devices);
        BluetoothManager.getInstance().bluetoothSearchDevices();
        list.setAdapter(Singleton.getInstance().adapter);
    }

    /**
     *Remove devices from the ListView
     */
    private void removeDevices(){
        list.setAdapter(null);
    }

    /**
     * Get the action on the checkbox
     * @param v
     */
    public void switchLumAuto(View v){
        Singleton.getInstance().lumSensor.enableAutoLum(cLum.isChecked());
    }

    /**
     * Display paired devices on click
     * @param view
     */
    public void paramPairedDevices(View view){
        connectedDevices.setBackgroundColor(rgb(255,255,255));
        unpairDevice.setBackgroundColor(rgb(238,238,238));
        if(BluetoothManager.getInstance().isBluetoothOn()){
            pairDisplay = true;
            displayPairedDevices();
        }
    }

    /**
     * Display unpaired devices on click
     * @param view
     */
    public void paramSearchDevice(View view){
        unpairDevice.setBackgroundColor(rgb(255,255,255));
        connectedDevices.setBackgroundColor(rgb(238,238,238));
        if(BluetoothManager.getInstance().isBluetoothOn()){
            pairDisplay = false;
            displaySearchDevices();
        }
    }


    /**
     * Destructor
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Singleton.getInstance().aCA.unregisterReceiver(BluetoothManager.getInstance().myReceiver);
    }

    /**
     * Refresh the devices display
     * @param v
     */
    public void refresh(View v){
        if(BluetoothManager.getInstance().isBluetoothOn()){
            if (pairDisplay){
                displayPairedDevices();
            }else {
                displaySearchDevices();
            }
        }
    }


}

