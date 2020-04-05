package com.example.driverobotproject;

/**
 *
 * @author Alex GONNET
 * @version 2
 */


import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private Bluetooth bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
