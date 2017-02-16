package com.todo.utils;

//Usage: code-only strings, non-ui...

//Note: Would have stored these strings in strings.xml,
//      but cannot access resources from static context,
//      as seen with referencing string in EditDateDialogFragment.newInstance()

public class Constants {
    public static final String TODO_TEXT_KEY = "todoText";
    public static final String TODO_PRIORITY_KEY = "todoPriority";
    public static final String TODO_POS_KEY = "todoPos";
    public static final String TODO_DUEDATE_KEY = "todoDueDate";
    public static final String DATE_KEY = "date";
    public static final String EDIT_DATE_DIALOG_FRAGMENT_TAG_NAME = "fragment_edit_date";
    public static final String DUMMY_TODO_1 = "Clean kitchen.";
    public static final String DUMMY_TODO_2 = "Do taxes, file 1099s.";
    public static final String DUMMY_TODO_3 = "Take out the garbage.";
}
