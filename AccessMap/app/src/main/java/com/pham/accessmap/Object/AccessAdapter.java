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
 * Created by Pham on 28/12/2014.
 */

public class AccessAdapter extends ArrayAdapter<AccessType> {
    private final Context context;
    private final ArrayList<AccessType> accessTypes;


    public  AccessAdapter (Context context , ArrayList<AccessType> accessTypes  )
    {
        super(context, R.layout.access_list_cell, accessTypes );
        this.context = context;
        this.accessTypes = accessTypes;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.access_list_cell, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.accessType);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.accessImage);
        if (LanguageHelper.getInstance().getAppLanguage(this.context).equals(LanguageHelper.ENGLISH))
        {
            textView.setText(accessTypes.get(position).accessName_en);
        }
        else
        {
            textView.setText(accessTypes.get(position).accessName);
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
