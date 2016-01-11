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

        if (LanguageHelper.getInstance().getAppLanguage(this.context).equals(LanguageHelper.ENGLISH))
        {
            textView.setText(accessTypes.get(position).accessName_en);
                String languageTranslated ;
                if (textView.getText().equals("Toilet")) {
                        languageTranslated = "Entrance > 80 cm\nSeat height 40 – 50 cm\nSink height from 40 to 80 cm.";
                } else if (textView.getText().equals("Elevator")) {
                        languageTranslated = "Door width > 90 cm\nDepth > 130 cm\nHeight of buttons from 90-120 cm.";
                } else if (textView.getText().equals("Corridor/Pathway")) {
                        languageTranslated = "Width > 120cm\nTurning area > 90 cm";
                } else if (textView.getText().equals("Building entrance")) {
                        languageTranslated = "Slopes < 15 degree\nSteps >= 1";
                } else {
                        languageTranslated = "Width > 90 cm\nHandle height: 80-100 cm\nWashbasin height: 40 – 80 cm.";
                }
                tView1.setText(languageTranslated);
        }
        else
        {
            textView.setText(accessTypes.get(position).accessName);
                tView1.setText(accessTypes.get(position).accessDes);
        }

        byte[] bytes = Base64.decode(accessTypes.get(position).Image, Base64.DEFAULT);
        Bitmap bmp = getBitmap(bytes);
        imageView.setImageBitmap(bmp);

        return rowView;
        }

public Bitmap getBitmap(byte[] bitmap) {
        return BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
        }
}

