package com.example.alex.todolistapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.v4.app.INotificationSideChannel;

public class TodoSQLiteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "todoList.db";
    public static final int DB_VERSION = 1;

    //ToDoList table
    public static final String TODO_LIST_TABLE = "TODO_LIST";
    public static final String COLUMN_TODO_ITEM_NAME = "ITEM_NAME";
    public static final String COLUMN_DUE_DATE = "DUE_DATE";
    public static final String COLUMN_PRIORITY = "PRIORITY";
    public static final String COLUMN_CATEGORY = "CATEGORY";
    public static final String COLUMN_COMPLETION = "COMPLETION";
    public static final String COLUMN_USER_POSITION = "POSITION";
    public static final String CREATE_TODO_LIST_TABLE =
        "CREATE TABLE " + TODO_LIST_TABLE + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TODO_ITEM_NAME + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_DUE_DATE + " TEXT, " +
                COLUMN_COMPLETION + " INTEGER, " +
                COLUMN_PRIORITY + " INTEGER, " +
                COLUMN_USER_POSITION + " INTEGER" + ")";

    public TodoSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //not used
    }
}
