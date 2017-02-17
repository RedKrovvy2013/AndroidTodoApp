package com.todo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.todo.R;
import com.todo.models.Priority;

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
                                    .inflate(R.layout.spinner_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);

        textView.setText(priority.value);

        return convertView;
    }
}