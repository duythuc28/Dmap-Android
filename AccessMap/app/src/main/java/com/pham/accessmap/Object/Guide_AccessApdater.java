package com.pham.accessmap.Object;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pham.accessmap.R;


import java.util.ArrayList;

/**
 * Created by mc976 on 1/10/15.
 */
public class Guide_AccessApdater extends ArrayAdapter<AccessType> {
private final Context context;
private final ArrayList<AccessType> accessTypes;


public  Guide_AccessApdater (Context context , ArrayList<AccessType> accessTypes  )
        {
        super(context, R.layout.access_guidecell, accessTypes );
        this.context = context;
        this.accessTypes = accessTypes;
        }

@Override
public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.access_guidecell, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.guide_accessName);
        TextView tView1 = (TextView)rowView.findViewById(R.id.guide_accessDes);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.guide_accessImage);
        SharedPreferences preferences = context.getSharedPreferences("MyPref",context.MODE_PRIVATE);
        boolean language = preferences.getBoolean("language", false);
        if (language == true)
        {
            textView.setText(accessTypes.get(position).accessName_en);
        }
        else
        {
            textView.setText(accessTypes.get(position).accessName);
        }

        tView1.setText(accessTypes.get(position).accessDes);
        byte[] bytes = Base64.decode(accessTypes.get(position).Image, Base64.DEFAULT);
        Bitmap bmp = getBitmap(bytes);
        imageView.setImageBitmap(bmp);

        return rowView;
        }

public Bitmap getBitmap(byte[] bitmap) {
        return BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
        }
}

