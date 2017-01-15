package com.example.alex.todolistapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.example.alex.todolistapp.model.ToDoItem;

import java.util.ArrayList;

public class TodoListDataSource {
    private TodoSQLiteHelper mTodoSQLiteHelper;

    public TodoListDataSource (Context context) {
        mTodoSQLiteHelper = new TodoSQLiteHelper(context);
       }


    public SQLiteDatabase open () {
        return mTodoSQLiteHelper.getWritableDatabase();
    }

    private void close (SQLiteDatabase database) {
        database.close();
    }


    public long addItem (ToDoItem item) {
        SQLiteDatabase database = open();
        database.beginTransaction();

        ContentValues todoItemValue = new ContentValues();
        todoItemValue.put(TodoSQLiteHelper.COLUMN_TODO_ITEM_NAME, item.getName());
        todoItemValue.put(TodoSQLiteHelper.COLUMN_CATEGORY, item.getCategory());
        todoItemValue.put(TodoSQLiteHelper.COLUMN_DUE_DATE, item.getDueDate());
        todoItemValue.put(TodoSQLiteHelper.COLUMN_COMPLETION, item.getCompletionAsInt());
        todoItemValue.put(TodoSQLiteHelper.COLUMN_PRIORITY, item.getPriorityAsInt());
        todoItemValue.put(TodoSQLiteHelper.COLUMN_USER_POSITION, item.getUserPosition());
        long id = database.insert(TodoSQLiteHelper.TODO_LIST_TABLE, null, todoItemValue);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
        return id;
        }

    public ArrayList<ToDoItem> readItems() {
        SQLiteDatabase database = open();
        Cursor cursor = database.query(
                TodoSQLiteHelper.TODO_LIST_TABLE,
                new String[]{BaseColumns._ID, TodoSQLiteHelper.COLUMN_TODO_ITEM_NAME,
                        TodoSQLiteHelper.COLUMN_CATEGORY, TodoSQLiteHelper.COLUMN_DUE_DATE,
                        TodoSQLiteHelper.COLUMN_COMPLETION,
                        TodoSQLiteHelper.COLUMN_PRIORITY,
                        TodoSQLiteHelper.COLUMN_USER_POSITION}, //columns to return
                null, //selection
                null, //selection args
                null, //group by
                null, //having
                TodoSQLiteHelper.COLUMN_USER_POSITION + " ASC");

        ArrayList<ToDoItem> items = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                String name = getStringFromColumnName(cursor, TodoSQLiteHelper.COLUMN_TODO_ITEM_NAME);
                String category = getStringFromColumnName(cursor, TodoSQLiteHelper.COLUMN_CATEGORY);
                String dueDate = getStringFromColumnName(cursor, TodoSQLiteHelper.COLUMN_DUE_DATE);
                int completion = getIntFromColumnName(cursor, TodoSQLiteHelper.COLUMN_COMPLETION);
                int priority = getIntFromColumnName(cursor, TodoSQLiteHelper.COLUMN_PRIORITY);
                int position = getIntFromColumnName(cursor, TodoSQLiteHelper.COLUMN_USER_POSITION);
                int id = getIntFromColumnName(cursor, BaseColumns._ID);
                ToDoItem item = new ToDoItem(name, category, dueDate, completion, priority, position, id);
                items.add(item);
            }
            while(cursor.moveToNext());
        }

        cursor.close();
        close(database);
        if (items.size() == 0) return null;
        return items;
    }

    public ArrayList<ToDoItem> sortItems(int sortSpinnerPosition, int ascDescSpinnerPosition) {
        SQLiteDatabase database = open();
        ArrayList<ToDoItem> items = new ArrayList<>();
        String columnName = "";
        String sortOrder = "";

        switch (sortSpinnerPosition) {
            case 1:
            columnName = TodoSQLiteHelper.COLUMN_CATEGORY;
                break;
            case 2:
            columnName = TodoSQLiteHelper.COLUMN_COMPLETION;
                break;
            case 3:
            columnName = TodoSQLiteHelper.COLUMN_DUE_DATE;
                break;
            case 4:
            columnName = TodoSQLiteHelper.COLUMN_TODO_ITEM_NAME;
                break;
            case 5:
            columnName = TodoSQLiteHelper.COLUMN_PRIORITY;
        }

        switch (ascDescSpinnerPosition) {
            case 0:
                sortOrder = " ASC";
                break;
            case 1:
                sortOrder = " DESC";
                break;
    }
        Cursor cursor = database.query(
                TodoSQLiteHelper.TODO_LIST_TABLE,
                null, //columns to return
                null, //selection
                null, //selection args
                null, //group by
                null, //having
                columnName + sortOrder);

        if(cursor.moveToFirst()) {
            do {
                String name = getStringFromColumnName(cursor, TodoSQLiteHelper.COLUMN_TODO_ITEM_NAME);
                String category = getStringFromColumnName(cursor, TodoSQLiteHelper.COLUMN_CATEGORY);
                String dueDate = getStringFromColumnName(cursor, TodoSQLiteHelper.COLUMN_DUE_DATE);
                int completion = getIntFromColumnName(cursor, TodoSQLiteHelper.COLUMN_COMPLETION);
                int priority = getIntFromColumnName(cursor, TodoSQLiteHelper.COLUMN_PRIORITY);
                int position = getIntFromColumnName(cursor, TodoSQLiteHelper.COLUMN_USER_POSITION);
                int id = getIntFromColumnName(cursor, BaseColumns._ID);
                ToDoItem item = new ToDoItem(name, category, dueDate, completion, priority, position, id);
                items.add(item);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return items;
    }

    public void deleteItem(ToDoItem item) {
        SQLiteDatabase database = open();
        database.beginTransaction();
        database.delete(TodoSQLiteHelper.TODO_LIST_TABLE,
                String.format("%s=%d", BaseColumns._ID, item.getDatabaseId()),
                null);
        database.setTransactionSuccessful();
        database.endTransaction();
         }

    private void deleteAll() {
        SQLiteDatabase database = open();
        database.beginTransaction();
        database.delete(TodoSQLiteHelper.TODO_LIST_TABLE,
                null,
                null);
        database.setTransactionSuccessful();
        database.endTransaction();
    }


    private int getIntFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    private String getStringFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

    public void updateItem(ToDoItem updatedItem) {
        SQLiteDatabase database = open();
        database.beginTransaction();

        ContentValues todoItemValue = new ContentValues();
        todoItemValue.put(TodoSQLiteHelper.COLUMN_TODO_ITEM_NAME, updatedItem.getName());
        todoItemValue.put(TodoSQLiteHelper.COLUMN_CATEGORY, updatedItem.getCategory());
        todoItemValue.put(TodoSQLiteHelper.COLUMN_DUE_DATE, updatedItem.getDueDate());
        todoItemValue.put(TodoSQLiteHelper.COLUMN_COMPLETION, updatedItem.getCompletionAsInt());
        todoItemValue.put(TodoSQLiteHelper.COLUMN_PRIORITY, updatedItem.getPriorityAsInt());
        database.update(TodoSQLiteHelper.TODO_LIST_TABLE,
                todoItemValue,
                String.format("%s=%d", BaseColumns._ID, updatedItem.getDatabaseId()), null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
        }


     public void updateItems(ArrayList<ToDoItem> toDoList) {
        deleteAll();
        for (ToDoItem item : toDoList) {
            addItem(item);
        }
    }
}

