package com.pham.accessmap.Object;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.pham.accessmap.R;

import java.util.ArrayList;

/**
 * Created by mc976 on 1/11/15.
 */
public class LocationType_Adapter extends ArrayAdapter<LocationType> {

    private final ArrayList<LocationType> locationTypes;
    private final Context context;
    public ArrayList<Integer> checkedList;
    static class ViewHolder {
//        TextView text;
//        TextView timestamp;
//        ImageView icon;
//        ProgressBar progress;
//        int position;
        TextView locationTitleTextView;
        ImageView locationImageView;
        Switch locationSwitch;
    }

    public LocationType_Adapter (Context context , ArrayList<LocationType>locationTypes)
    {
        super (context , R.layout.locationtype_cell, locationTypes);
        this.context = context;
        this.locationTypes = locationTypes;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.locationtype_cell, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.locationTitleTextView = (TextView) convertView.findViewById(R.id.locationType_name);
            viewHolder.locationImageView = (ImageView) convertView.findViewById(R.id.locationType_Image);
            viewHolder.locationSwitch = (Switch) convertView.findViewById(R.id.switch_locationType);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        if (LanguageHelper.getInstance().getAppLanguage(context).equals(LanguageHelper.ENGLISH)) {
            viewHolder.locationTitleTextView.setText(locationTypes.get(position).locationName_en);
        } else {
            viewHolder.locationTitleTextView.setText(locationTypes.get(position).locationName);
        }

        viewHolder.locationSwitch.setChecked((locationTypes.get(position).isCheck == 1 ));
        checkedList = new ArrayList<>();

        for (LocationType locType : this.locationTypes)
        {
            if (locType.isCheck == 1)
            {
                checkedList.add(locType.locationType_ID);
                Log.v("check list....", String.valueOf(locType.locationType_ID));
            }
        }

        //final ArrayList<LocationType> finalData = data;
        viewHolder.locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    checkedList.add(locationTypes.get(position).locationType_ID);
                    //Log.v("cell turn on",String.valueOf(locationTypes.get(position).locationName_en));

                }
                else
                {
                    for (int i : checkedList)
                    {
                        if (i == locationTypes.get(position).locationType_ID)
                        {
                            checkedList.remove(checkedList.indexOf(i));
                            break;
                        }
                    }
                }
            }
        });
        //tView1.setText(accessTypes.get(position).accessDes);
        byte[] bytes = Base64.decode(locationTypes.get(position).locationImage, Base64.DEFAULT);
        Bitmap bmp = getBitmap(bytes);
        viewHolder.locationImageView.setImageBitmap(bmp);

        return convertView;
    }

    public Bitmap getBitmap(byte[] bitmap) {
        return BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
    }
}

