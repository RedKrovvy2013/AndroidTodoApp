package com.todoapp.models;

public class Todo implements Comparable<Todo> {
    public String text;
    public String priority;
    private Integer priorityIndex;

    public Todo(String text, String priority) {
        this.text = text;
        this.priority = priority;
        if(priority.equals("HIGH"))
            this.priorityIndex = 1;
        else if(priority.equals("MEDIUM"))
            this.priorityIndex = 2;
        else if(priority.equals("LOW"))
            this.priorityIndex = 3;
        else
            this.priorityIndex = 1;
    }

    public int compareTo(Todo todo) {

        if(!priorityIndex.equals(todo.priorityIndex)) {
            return priorityIndex - todo.priorityIndex;
        }
        return text.compareTo(todo.text);
    }
}