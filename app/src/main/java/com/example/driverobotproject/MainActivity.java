package com.example.driverobotproject;

/**
 *
 * @author Alex GONNET
 * @version 2
 */


import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements BluetoothCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager commuMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = commuMan.getActiveNetworkInfo();
        if(networkInfo == null)
        {
            Toast.makeText(MainActivity.this,
                    "The device is not connected",
                    Toast.LENGTH_LONG).show();
        }

        Singleton.getInstance().lumSensor = new LumSensor();
        BluetoothManager.getInstance().initializeBluetooth(this,"00001101-0000-1000-8000-00805F9B34FB","HEALTH_MODULE_1");
    }

    public void onButtonAProposClicked(View v) {
        Log.i("Bouton", "A propos");
        Intent intent = new Intent(
                MainActivity.this,
                APropos.class
        );
        startActivity(intent);
    }

    public void onButtonParametresClicked(View v) {
        Log.i("Bouton", "Parametres");
        Intent intent = new Intent(
                MainActivity.this,
                Parametres.class
        );
        startActivity(intent);
    }

    public void onButtonStartClicked(View v) {

        Log.i("Mouvement", "Start");
        BluetoothManager.getInstance().senReceiveMsg(" ");
    }

    public void onButtonUpClicked(View v) {

        Log.i("Direction", "Up");
        BluetoothManager.getInstance().senReceiveMsg("z");
    }

    public void onButtonDownClicked(View v) {

        Log.i("Direction", "Down");
        BluetoothManager.getInstance().senReceiveMsg("s");
    }

    public void onButtonLeftClicked(View v) {

        Log.i("Direction", "Left");
        BluetoothManager.getInstance().senReceiveMsg("q");
    }

    public void onButtonRightClicked(View v) {

        Log.i("Direction", "Right");
        BluetoothManager.getInstance().senReceiveMsg("d");
    }

    @Override
    public void onBluetoothConnection(int returnCode) {

    }

    @Override
    public void onBluetoothDiscovery(int returnCode) {

    }

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
}
