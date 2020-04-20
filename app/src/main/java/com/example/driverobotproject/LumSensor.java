package com.example.driverobotproject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LumSensor extends Parametres implements SensorEventListener {

    public boolean success = false;

    private AppCompatActivity aCA;

    private boolean sensorExist;
    private Sensor photometre;
    private int valueSensor;

    public void isLumSensorAvalaible(){
        getPermission();

        SensorManager sm = (SensorManager)Singleton.getInstance().aCAMainAct.getSystemService(Context.SENSOR_SERVICE);
        photometre = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        //Sensor exist
        if(photometre != null){
            sm.registerListener(this, photometre, sm.SENSOR_DELAY_NORMAL);
            sensorExist = true;
        }else {
            sensorExist = false;
        }
    }

    public boolean initLumSensor(AppCompatActivity aCA){
        this.aCA = aCA;
        return sensorExist;
    }

    public void setBrightness(int brightness){
        if(brightness < 0){
            brightness = 0;
        }else if (brightness > 255){
            brightness = 255;
        }

        ContentResolver contentResolver = aCA.getApplicationContext().getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
    }


    public int getBrightness(){
        int brightness = 100;
        try {
            ContentResolver contentResolver = aCA.getApplicationContext().getContentResolver();
            brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
        }
        return brightness;
    }

    public int getSensorValue(){
        if (sensorExist){
            return valueSensor;
        }
        return 0;
    }


    public void getPermission(){
        boolean value;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            value = Settings.System.canWrite(Singleton.getInstance().aCAMainAct.getApplicationContext());
            if(value){
                success = true;
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData((Uri.parse("package:" + Singleton.getInstance().aCAMainAct.getApplicationContext().getPackageName())));
                startActivityForResult(intent, 10000);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1000){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                boolean value = Settings.System.canWrite(aCA.getApplicationContext());
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
            ContentResolver contentResolver = aCA.getApplicationContext().getContentResolver();
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        } else {
            ContentResolver contentResolver = aCA.getApplicationContext().getContentResolver();
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        valueSensor = (int)event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
