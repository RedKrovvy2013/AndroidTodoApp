package com.todo.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.todo.R;
import com.todo.utils.TimeUntilDuePeriods;
import com.todo.models.Todo;

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
//        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, hh:mm aaa");
//        dateFormat.setTimeZone(dueDate.getTimeZone());
//        ivDueDate.setText(dateFormat.format(dueDate.getTime()));

        Calendar now = Calendar.getInstance();
        long nowMs = now.getTimeInMillis();
        long dueDateMs = dueDate.getTimeInMillis();
        long duration = dueDateMs - nowMs;

        if(duration <= 0) {
            ivDueDate.setText("(" + resources.getString(R.string.past_due) + ")");
        } else {
            TimeUntilDuePeriods timeUntilDuePeriods = TimeUntilDuePeriods.getInstance(getContext());
            ArrayList<TimeUntilDuePeriods.TimeUntilDuePeriod> periods =
                    timeUntilDuePeriods.periods;

            for( TimeUntilDuePeriods.TimeUntilDuePeriod period : periods ) {

                if(duration >= period.min && duration <= period.max) {
                    ivDueDate.setText("(" + period.message + ")");
                }
            }
        }

        return convertView;
    }
}