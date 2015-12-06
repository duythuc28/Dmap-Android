package com.pham.accessmap.Object;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pham.accessmap.R;

import java.util.ArrayList;

/**
 * Created by mc976 on 1/20/15.
 */
public class LocationTempAdapter extends ArrayAdapter<LocationTemp> {
    private final Context context;
    private final ArrayList<LocationTemp> locations;


    public  LocationTempAdapter (Context context , ArrayList<LocationTemp> locations  )
    {
        super(context, R.layout.location_list_cell, locations );
        this.context = context;
        this.locations = locations;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.location_list_cell, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.location_Name);
        TextView textView1 = (TextView) rowView.findViewById(R.id.location_Address);
        TextView textView2 = (TextView) rowView.findViewById(R.id.location_Phone);
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.accessImage);
        textView.setText(locations.get(position).locationName);
        textView1.setText(locations.get(position).locationAddress);
        textView2.setText(locations.get(position).locationPhone);
        //byte[] bytes = Base64.decode(accessTypes.get(position).Image, Base64.DEFAULT);
        //Bitmap bmp = getBitmap(bytes);
        //imageView.setImageBitmap(bmp);

        return rowView;
    }
}
