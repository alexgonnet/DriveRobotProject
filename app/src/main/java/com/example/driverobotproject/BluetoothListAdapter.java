package com.example.driverobotproject;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Personalized listView adapter for Bluetooth devices
 * @author Benjamin BOURG
 * @version 1
 */

public class BluetoothListAdapter extends ArrayAdapter<BluetoothDevice> {

    /**
     * Constructor
     * @param context The activity context
     * @param items list of BT devices
     */
    public BluetoothListAdapter(Context context, ArrayList<BluetoothDevice> items) {
        super(context, 0, items);
    }


    /**
     * Create the display of the BT device information according to the template created in the XML file
     * @param position of the item in the list
     * @param convertView
     * @param parent
     * @return convertView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Get the BT item in the list
        BluetoothDevice item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_display_model, parent, false);
        }

        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);

        // Populate the data into the template view using the data object
        //If the device has no name
        if(item.getName() == null) {
            //Get the address
            tvName.setText(item.getAddress());
        }else {
            //Else get the name
            tvName.setText(item.getName());
        }

        // Return the completed view to render on screen
        return convertView;

    }
}
