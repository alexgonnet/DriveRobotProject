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

/**
 * Class LumSensor : Manage the luminosity
 * @author Benjamin BOURG
 * @version  1.1
 */

public class LumSensor extends Parametres implements SensorEventListener {

    /**
     * Store the permission value
     * Initialize with false = no permission
     */
    public boolean success = false;

    /**
     * The main activity
     */
    private AppCompatActivity aCA;

    /**
     * Store the value true if the device has a luminosity sensor otherwise false
     */
    private boolean sensorExist;

    /**
     * Luminosity sensor
     */
    private Sensor photometre;

    /**
     * The value measured by the sensor
     */
    private int valueSensor;


    /**
     * Check if the device has a luminosity sensor
     */
    public void isLumSensorAvailable(){
        getPermission();

        SensorManager sm = (SensorManager)Singleton.getInstance().aCAMainAct.getSystemService(Context.SENSOR_SERVICE);
        //Get the luminosity sensor
        photometre = sm.getDefaultSensor(Sensor.TYPE_LIGHT);

        //Sensor exist
        if(photometre != null){
            //Set the measure frequency
            sm.registerListener(this, photometre, sm.SENSOR_DELAY_NORMAL);
            sensorExist = true;
        }else {
            sensorExist = false;
        }
    }

    /**
     * Return the existence or not of the luminosity sensor
     * @param aCA the activity from an other activity
     * @return sensorExist if the device has a luminosity sensor, return true otherwise false
     */
    public boolean initLumSensor(AppCompatActivity aCA){
        this.aCA = aCA;
        return sensorExist;
    }

    /**
     * Set the brightness of the device
     * @param brightness The new brightness value
     */
    public void setBrightness(int brightness){
        if(brightness < 0){
            brightness = 0;
        }else if (brightness > 255){
            brightness = 255;
        }

        ContentResolver contentResolver = aCA.getApplicationContext().getContentResolver();
        //Set the brightness in the settings
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
    }


    /**
     * Get the brightness of the device
     * @return brightness the device brightness
     */
    public int getBrightness(){
        int brightness = 100;
        try {
            ContentResolver contentResolver = aCA.getApplicationContext().getContentResolver();
            //Get the screen brightness from the settings
            brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
        }
        return brightness;
    }

    /**
     * Get the luminosity sensor value
     * @return valueSensor the luminosity measured | 0 if there is no sensor
     */
    public int getSensorValue(){
        if (sensorExist){
            return valueSensor;
        }
        return 0;
    }


    /**
     * Ask the permission to modify luminosity settings
     */
    public void getPermission(){
        boolean value;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //Enable the permission to write into the device settings
            value = Settings.System.canWrite(Singleton.getInstance().aCAMainAct.getApplicationContext());
            if(value){
                //The permission is activated
                success = true;
            } else {
                //Ask the user the permission to write into the settings
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData((Uri.parse("package:" + Singleton.getInstance().aCAMainAct.getApplicationContext().getPackageName())));
                Singleton.getInstance().aCAMainAct.startActivityForResult(intent, 10000);
            }
        }
    }


    /**
     * Get the permission result
     * @param requestCode
     * @param resultCode
     * @param data
     */
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

    /**
     * Activate/Deactivate automatic device luminosity
     * @param state true if activate | false if deactivate
     */
    public void enableAutoLum(boolean state){
        if (state){
            ContentResolver contentResolver = aCA.getApplicationContext().getContentResolver();
            //Activate automatic luminosity mode
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        } else {
            ContentResolver contentResolver = aCA.getApplicationContext().getContentResolver();
            //Activate manual luminosity mode
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
    }

    /**
     * Get from the sensor the luminosity
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        valueSensor = (int)event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
