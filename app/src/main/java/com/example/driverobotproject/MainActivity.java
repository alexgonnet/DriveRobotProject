package com.example.driverobotproject;

/**
 * Main Activity class
 * @author Alex GONNET
 * @version 2
 */


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.sql.Timestamp;


public class MainActivity extends AppCompatActivity implements BluetoothCallback {

    /**
     * Id project
     */
    public static String idProject = "30";

    /**
     *
     */
    public Timestamp timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check if the device is connected to internet
        ConnectivityManager commuMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = commuMan.getActiveNetworkInfo();
        if(networkInfo == null)
        {
            Toast.makeText(MainActivity.this, "The device is not connected", Toast.LENGTH_LONG).show();
        }

        //Initialization of the luminosity sensor
        Singleton.getInstance().lumSensor = new LumSensor();
        Singleton.getInstance().aCAMainAct = this;
        //Check if sensor available
        Singleton.getInstance().lumSensor.isLumSensorAvalaible();
        //Initialization of the Bluetooth
        BluetoothManager.getInstance().initializeBluetooth("00001101-0000-1000-8000-00805F9B34FB");

        //Initialization of the notifications
        Notification.getInstance().initNotification();
    }

    /**
     * Manage the action for the info button
     * @param v
     */
    public void onButtonAProposClicked(View v) {
        Log.i("Bouton", "A propos");
        Intent intent = new Intent(
                MainActivity.this,
                APropos.class
        );
        startActivity(intent);
    }

    /**
     * Manage the action for the button parametres
     * @param v
     */
    public void onButtonParametresClicked(View v) {
        Log.i("Bouton", "Parametres");
        Intent intent = new Intent(
                MainActivity.this,
                Parametres.class
        );
        startActivity(intent);
    }

    /**
     * Manage the action for the button start
     * @param v
     */
    public void onButtonStartClicked(View v) {
        Log.i("Mouvement", "Start");
        //Do the actions only if the device is connected to the Bluetooth
        if (BluetoothManager.getInstance().connected) {
            BluetoothManager.getInstance().senReceiveMsg(" ");

            sendData("Start");

        }
    }

    /**
     * Manage the action for the button up arrow
     * @param v
     */
    public void onButtonUpClicked(View v) {
        Log.i("Direction", "Up");
        //Do the actions only if the device is connected to the Bluetooth
        if (BluetoothManager.getInstance().connected) {
            BluetoothManager.getInstance().senReceiveMsg("z");

            sendData("Straight");
        }
    }

    /**
     * Manage the action for the button down arrow
     * @param v
     */
    public void onButtonDownClicked(View v) {
        Log.i("Direction", "Down");
        //Do the actions only if the device is connected to the Bluetooth
        if (BluetoothManager.getInstance().connected) {
            BluetoothManager.getInstance().senReceiveMsg("s");

            sendData("Back");
        }
    }

    /**
     * Manage the action for the button left arrow
     * @param v
     */
    public void onButtonLeftClicked(View v) {
        Log.i("Direction", "Left");
        //Do the actions only if the device is connected to the Bluetooth
        if (BluetoothManager.getInstance().connected) {
            BluetoothManager.getInstance().senReceiveMsg("q");

            sendData("Back");
        }
    }

    /**
     * Manage the action for the button right arrow
     * @param v
     */
    public void onButtonRightClicked(View v) {
        Log.i("Direction", "Right");
        //Do the actions only if the device is connected to the Bluetooth
        if (BluetoothManager.getInstance().connected) {
            BluetoothManager.getInstance().senReceiveMsg("d");

            sendData("Right");
        }
    }

    @Override
    public void onBluetoothConnection(int returnCode) {

    }

    @Override
    public void onBluetoothDiscovery(int returnCode) {

    }

    /**
     * Call when the app receives a message via Bluetooth
     * @param data the received String
     */
    @Override
    public void onReceiveData(String data) {

        final String finalData = data;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.d("DATA_RECEIVED_0", finalData);
                Toast.makeText(MainActivity.this,finalData,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        BluetoothManager.getInstance().closeBluetooth(this);
        super.onDestroy();
    }


    /**
     * Check the user answer from the BT enable
     * @author Benjamin BOURG
     * @param requestCode the expected code
     * @param resultCode the result code
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Get the result of the Bluetooth request authorization
        //If authorization canceled
        if(resultCode == RESULT_CANCELED && requestCode == BluetoothManager.getInstance().REQUEST_ENABLE_BLUETOOTH){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Info");
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setMessage("Too use the app, Bluetooth is required");
            AlertDialog aD = builder.create();
            aD.show();
        }
    }

    private void sendData(String action){
        this.timestamp = new Timestamp(System.currentTimeMillis());
        (new Connectivity()).execute("http://cabani.free.fr/ise/adddata.php?idproject="+idProject+"&lux="+Singleton.getInstance().lumSensor.getSensorValue()+"&timestamp="+(timestamp.getTime()/1000)+"&action="+action);
    }
}
