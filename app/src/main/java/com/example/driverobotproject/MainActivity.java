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

public class MainActivity extends AppCompatActivity {

    private Bluetooth bluetooth;

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

        bluetooth = new Bluetooth(getApplicationContext(), this);
        Singleton.getInstance().bluetooth = bluetooth;
    }

    public void  onButtonAProposClicked(View v)
    {
        Log.i("Bouton", "A propos");
        Intent intent = new Intent(
                MainActivity.this,
                APropos.class
        );
        startActivity(intent);
    }

    public void onButtonParametresClicked(View v)
    {
        Log.i("Bouton", "Parametres");
        Intent intent = new Intent(
                MainActivity.this,
                Parametres.class
        );
        startActivity(intent);
    }

    public void onButtonStartClicked(View v)
    {
        Log.i("Mouvement", "Start");
    }

    public void onButtonUpClicked(View v)
    {
        Log.i("Direction", "Up");
    }

    public void onButtonDownClicked(View v)
    {
        Log.i("Direction", "Down");
    }

    public void onButtonLeftClicked(View v)
    {
        Log.i("Direction", "Left");
    }

    public void onButtonRightClicked(View v)
    {
        Log.i("Direction", "Right");
    }
}
