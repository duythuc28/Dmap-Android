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
 * Created by mc976 on 1/10/15.
 */
public class Feedback_Adapter extends ArrayAdapter<Feedback> {

    private  final ArrayList<Feedback> feedbacks;
    private  final Context context;
    public Feedback_Adapter (Context context , ArrayList<Feedback> feedbacks)
    {
        super(context, R.layout.feedback_cell, feedbacks );
        this.context = context;
        this.feedbacks = feedbacks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.feedback_cell, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.feedback_name);
        TextView content = (TextView) rowView.findViewById(R.id.feedback_content);
        textView.setText(feedbacks.get(position).name);
        content.setText(feedbacks.get(position).content);
        return rowView;
    }
}
