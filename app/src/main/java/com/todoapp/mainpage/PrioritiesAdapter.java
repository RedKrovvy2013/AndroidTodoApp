package com.todoapp.mainpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.todoapp.models.Priority;

import java.util.ArrayList;

public class PrioritiesAdapter extends ArrayAdapter<Priority> {

    public PrioritiesAdapter(Context context, ArrayList<Priority> priorities) {
        super(context, 0, priorities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Priority priority = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                                    .inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);

        textView.setText(priority.value);

        return convertView;
    }
}