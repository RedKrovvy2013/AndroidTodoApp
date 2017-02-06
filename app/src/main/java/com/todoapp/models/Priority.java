package com.todoapp.models;

public class Priority {
    public String value;

    public Priority(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
