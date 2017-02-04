package com.todoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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


        ivText.setText(todo.text);
        ivPriority.setText(todo.priority);

        return convertView;
    }
}