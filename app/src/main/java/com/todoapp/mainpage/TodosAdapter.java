package com.todoapp.mainpage;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.todoapp.R;
import com.todoapp.models.Todo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TodosAdapter extends ArrayAdapter<Todo> {

    public TodosAdapter(Context context, ArrayList<Todo> todos) {
        super(context, 0, todos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Todo todo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }

        TextView ivText = (TextView) convertView.findViewById(R.id.ivTodoText);
        TextView ivPriority = (TextView) convertView.findViewById(R.id.ivTodoPriority);
        TextView ivDueDate = (TextView) convertView.findViewById(R.id.ivTodoDueDate);

        ivText.setText(todo.text);
        ivPriority.setText(todo.priority);

        Resources resources = getContext().getResources();

        if(todo.priority.equals(resources.getString(R.string.high)))
            ivPriority.setTextColor(ResourcesCompat.getColor(resources, R.color.colorHigh, null));

        if(todo.priority.equals(resources.getString(R.string.medium)))
            ivPriority.setTextColor(ResourcesCompat.getColor(resources, R.color.colorMedium, null));

        if(todo.priority.equals(resources.getString(R.string.low)))
            ivPriority.setTextColor(ResourcesCompat.getColor(resources, R.color.colorLow, null));

        Calendar dueDate = todo.dueDate;
//        String month = String.valueOf(dueDate.get(dueDate.MONTH));
//        String day = String.valueOf(dueDate.get(dueDate.DAY_OF_MONTH));
//        String hour = String.valueOf(dueDate.get(dueDate.HOUR));
//        String minute = String.valueOf(dueDate.get(dueDate.MINUTE));
//
//        String amPm = (dueDate.get(dueDate.AM_PM) == 0) ? "AM" : "PM";

//        ivDueDate.setText(month + " " + day + ", " + hour + ":" + minute + " " + amPm);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        dateFormat.setTimeZone(dueDate.getTimeZone());
        ivDueDate.setText(dateFormat.format(dueDate.getTime()));

        return convertView;
    }
}