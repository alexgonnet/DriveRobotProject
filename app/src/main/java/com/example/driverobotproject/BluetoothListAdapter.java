package com.example.driverobotproject;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BluetoothListAdapter extends ArrayAdapter<BluetoothDevice> {

    public BluetoothListAdapter(Context context, ArrayList<BluetoothDevice> items) {

        super(context, 0, items);

    }



    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        BluetoothDevice item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_display_model, parent, false);

        }

        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);

        // Populate the data into the template view using the data object

        if(item.getName() == null) {
            tvName.setText(item.getAddress());
        }else {
            tvName.setText(item.getName());
        }

        // Return the completed view to render on screen

        return convertView;

    }
}
