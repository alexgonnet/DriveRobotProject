package com.example.driverobotproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
