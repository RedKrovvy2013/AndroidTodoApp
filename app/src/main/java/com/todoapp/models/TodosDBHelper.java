package com.todoapp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class TodosDBHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "todosDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_TODOS = "todos";
    private static final String TABLE_PRIORITIES = "priorities";

    private static final String KEY_TODO_ID = "id";
    private static final String KEY_TODO_PRIORITY_ID_FK = "priorityId";
    private static final String KEY_TODO_TEXT = "text";

    private static final String KEY_PRIORITY_ID = "id";
    private static final String KEY_PRIORITY_VALUE = "value";

    private static TodosDBHelper sInstance;

    public static synchronized TodosDBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TodosDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private TodosDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODOS_TABLE = "CREATE TABLE " + TABLE_TODOS +
                "(" +
                KEY_TODO_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_TODO_PRIORITY_ID_FK + " INTEGER REFERENCES " + TABLE_PRIORITIES + "," + // Define a foreign key
                KEY_TODO_TEXT + " TEXT" +
                ")";

        String CREATE_PRIORITIES_TABLE = "CREATE TABLE " + TABLE_PRIORITIES +
                "(" +
                KEY_PRIORITY_ID + " INTEGER PRIMARY KEY," +
                KEY_PRIORITY_VALUE + " TEXT" +
                ")";

        db.execSQL(CREATE_TODOS_TABLE);
        db.execSQL(CREATE_PRIORITIES_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRIORITIES);
            onCreate(db);
        }
    }

    public long addTodo(Todo todo) {

        SQLiteDatabase db = getWritableDatabase();
        long todoId = -1;

        db.beginTransaction();
        try {
            //TODO: change to getting Priority as opposed to priority value (String)
            long priorityId = getPriorityId(todo.priority);

            ContentValues values = new ContentValues();
            values.put(KEY_TODO_PRIORITY_ID_FK, priorityId);
            values.put(KEY_TODO_TEXT, todo.text);

            todoId = db.insertOrThrow(TABLE_TODOS, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add todo");
        } finally {
            db.endTransaction();
        }
        return todoId;
    }

    //NOTE: would update if Priority had members other than |value|,
    //      but has 'do not add if priority exists already' fxality
    public long addPriorityIfNotExist(Priority priority) {

        SQLiteDatabase db = getWritableDatabase();
        long priorityId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_PRIORITY_VALUE, priority.value);

            // First try to update the priority in case the priority already exists in the database
            // This assumes priority values are unique
            int rows = db.update(TABLE_PRIORITIES, values, KEY_PRIORITY_VALUE + "= ?", new String[]{priority.value});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_PRIORITY_ID, TABLE_PRIORITIES, KEY_PRIORITY_VALUE);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(priority.value)});
                try {
                    if (cursor.moveToFirst()) {
                        priorityId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                priorityId = db.insertOrThrow(TABLE_PRIORITIES, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update priority");
        } finally {
            db.endTransaction();
        }
        return priorityId;
    }

    public long getPriorityId(String priorityValue) {
        SQLiteDatabase db = getReadableDatabase();

        String query = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                                      KEY_PRIORITY_ID, TABLE_PRIORITIES,
                                      KEY_PRIORITY_VALUE, priorityValue);
        Cursor cursor = db.rawQuery(query, null);

        long priorityId = -1;
        try {
            if (cursor.moveToFirst()) {
                priorityId = cursor.getLong(0);
                //NOTE: there should only be one record, as priority values are kept unique
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get priority id from db");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return priorityId;
    }

    public ArrayList<Todo> getTodos() {
        ArrayList<Todo> todos = new ArrayList<>();

        String SELECT_QUERY =
                String.format("SELECT * FROM %s JOIN %s ON %s.%s = %s.%s",
                        TABLE_TODOS,
                        TABLE_PRIORITIES,
                        TABLE_TODOS, KEY_TODO_PRIORITY_ID_FK,
                        TABLE_PRIORITIES, KEY_PRIORITY_ID);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Priority priority = new Priority(
                                       cursor.getString(cursor.getColumnIndex(KEY_PRIORITY_VALUE)));

                    Todo todo = new Todo(cursor.getString(cursor.getColumnIndex(KEY_TODO_TEXT)),
                                         priority.value);
                    todos.add(todo);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return todos;
    }

    public ArrayList<Priority> getPriorities() {
        ArrayList<Priority> priorities = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRIORITIES, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    priorities.add(
                        new Priority(cursor.getString(cursor.getColumnIndex(KEY_PRIORITY_VALUE)))
                    );
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get priorities from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return priorities;
    }

    public void saveTodos(ArrayList<Todo> todos) {

        //NOTE: just removing old todos in db and inserting new ones,
        //      as todo ordering is subject to change a bunch from
        //      user sorting priorities.

        //TODO: consider having a Todo orderIndex member to avoid above,
        //      or some other impl to avoid above,
        //      although performance cost is not much as user won't have
        //      many todos at one time

        deleteTodos();

        for (Todo todo : todos ) {
            addTodo(todo);
        }
    }

    public void deleteTodos() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + TABLE_TODOS);
    }
}
