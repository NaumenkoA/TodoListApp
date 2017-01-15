package com.example.alex.todolistapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ToDoItem implements Parcelable {
    private String mName;
    private String mCategory;
    private boolean mHighPriority;
    private int mUserPosition;
    private long mDatabaseId;
    private boolean mIsCompleted;

    public String getDueDate() {
        return mDueDate;
    }

    public void setDueDate(String dueDate) {
        mDueDate = dueDate;
    }

    public String []  getDueDateArray () {
        return mDueDate.split("-");
       }

    public ToDoItem (String name, String category, String dueDate, boolean isCompleted, boolean highPriority, int userPosition, long id) {
        mName = name;
        mCategory = category;
        mDueDate = dueDate;
        mIsCompleted = isCompleted;
        mHighPriority = highPriority;
        mUserPosition = userPosition;
        mDatabaseId = id;
    }

    private String mDueDate;

    public ToDoItem(String name, String category, String dueDate, int completion, int priority, int userPosition, long id) {
        mName = name;
        mCategory = category;
        mDueDate = dueDate;
        mIsCompleted =  getBooleanFromInt(completion);
        mHighPriority = getBooleanFromInt(priority);
        mUserPosition = userPosition;
        mDatabaseId = id;
    }

    private boolean getBooleanFromInt(int integer) {
        switch (integer) {
            case 0: return false;
            case 1: return true;
        }
        return false;
    }

      public int getPriorityAsInt () {
        if (mHighPriority) { return 1;}
        return 0;
    }

    public boolean getIsCompleted () {
        return mIsCompleted;
    }

    public String getCategoryWithPrefix() {
        return "Category: " + mCategory;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public int getUserPosition() {
        return mUserPosition;
    }

    public void setUserPosition(int userPosition) {
        mUserPosition = userPosition;
    }

    public long getDatabaseId() {
        return mDatabaseId;
    }

    public void setDatabaseId(long databaseId) {
        mDatabaseId = databaseId;
    }

    public static Creator<ToDoItem> getCREATOR() {
        return CREATOR;
    }

    public boolean isCompleted() {
        return mIsCompleted;
    }

    public void setCompleted(boolean completed) {
        mIsCompleted = completed;
    }

    public boolean isHighPriority() {
        return mHighPriority;
    }

    public void setHighPriority(boolean highPriority) {
        mHighPriority = highPriority;
    }


    public String getName() {

        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public ToDoItem() {
    }

    public String getDueDateAsString() {
        DateFormat formatter = new SimpleDateFormat("dd/MM");
        return formatter.format(mDueDate);
    }

    public int getItemImageResource() {
        if (mHighPriority) {
            return android.R.drawable.star_big_on;
        } else {
            return android.R.drawable.star_big_off;
        }
    }

    public int getCompletionAsInt() {
        if (mIsCompleted) {
            return 1;
        }
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mCategory);
        dest.writeByte(this.mHighPriority ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mUserPosition);
        dest.writeLong(this.mDatabaseId);
        dest.writeByte(this.mIsCompleted ? (byte) 1 : (byte) 0);
        dest.writeString(this.mDueDate);
    }

    protected ToDoItem(Parcel in) {
        this.mName = in.readString();
        this.mCategory = in.readString();
        this.mHighPriority = in.readByte() != 0;
        this.mUserPosition = in.readInt();
        this.mDatabaseId = in.readLong();
        this.mIsCompleted = in.readByte() != 0;
        this.mDueDate = in.readString();
    }

    public static final Creator<ToDoItem> CREATOR = new Creator<ToDoItem>() {
        @Override
        public ToDoItem createFromParcel(Parcel source) {
            return new ToDoItem(source);
        }

        @Override
        public ToDoItem[] newArray(int size) {
            return new ToDoItem[size];
        }
    };
}
