package com.example.alex.todolistapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.todolistapp.R;
import com.example.alex.todolistapp.model.ToDoItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ToDoActivity extends AppCompatActivity {
    public static final String RESULT = "result";
    @BindView(R.id.categorySpinner) Spinner mCategorySpinner;
    @BindView (R.id.itemPriorityImageView) ImageView mPriorityImageView;
    @BindView(R.id.titleEditText) EditText mItemTitle;
    @BindView(R.id.datePicker) DatePicker mDatePicker;
    @BindView(R.id.otherCategoryEditText) EditText mOtherCategoryEditText;
    @BindView(R.id.toDoHeaderTextView) TextView mHeaderTextView;
    @BindView(R.id.itemCompletedCheckBox) CheckBox mItemCompletedCheckbox;

    private boolean mIsHighPriority;
    private int mUserPosition;
    private boolean mIsCompleted;
    private long mDatabaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String action = intent.getAction();
        setSpinnerAdapter(getResources().getStringArray(R.array.categories), mCategorySpinner);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               if (position == 3){
                   setOtherCategoryField(true);
               } else {
                   setOtherCategoryField(false);
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            setOtherCategoryField(false);
            }
        });

        mPriorityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsHighPriority) {
                    mPriorityImageView.setImageResource(android.R.drawable.star_big_on);
                } else {
                    mPriorityImageView.setImageResource(android.R.drawable.star_big_off);
                }
                mIsHighPriority = (!mIsHighPriority);
            }
        });

        if (action.equals(MainActivity.CREATE_NEW_ITEM)) {
            mIsHighPriority = false;
            mUserPosition = 0;
            mDatabaseId = 0;
            mIsCompleted = false;
            mOtherCategoryEditText.setEnabled(false);
        }

        if (action.equals(MainActivity.UPDATE_ITEM)) {
            ToDoItem item = intent.getParcelableExtra(MainActivity.TO_DO_ITEM);
            mHeaderTextView.setText(R.string.edit_header);
            mItemTitle.setText(item.getName());
            mIsHighPriority = item.isHighPriority();
            mUserPosition = item.getUserPosition();
            mDatabaseId = item.getDatabaseId();
            mPriorityImageView.setImageResource(getImageId());
            int year = Integer.parseInt(item.getDueDateArray()[0]);
            int month = Integer.parseInt(item.getDueDateArray()[1])-1;
            int day = Integer.parseInt(item.getDueDateArray()[2]);
            mDatePicker.updateDate(year, month, day);
            mCategorySpinner.setSelection(getSpinnerPosition(item));
            mOtherCategoryEditText.setText(getOtherCategoryText(item));
            mOtherCategoryEditText.setEnabled(isOtherCategoryEnabled(item));
            mIsCompleted = item.isCompleted();
            mItemCompletedCheckbox.setChecked(mIsCompleted);
            }
    }

    private boolean isOtherCategoryEnabled(ToDoItem item) {
        if (getSpinnerPosition(item) == 3) {
            return true;
        }
        return false;
    }

    private String getOtherCategoryText(ToDoItem item) {
        if (getSpinnerPosition(item) == 3) {
            return item.getCategory();
        } else {
            return "";
        }
    }

    private int getSpinnerPosition(ToDoItem item) {
        String selection = item.getCategory();
        switch (selection) {
            case "Family":
            return 0;
            case "Business":
            return 1;
            case "Personal":
            return 2;
            default:
            return 3;
        }
    }

    private int getImageId() {
        if (mIsHighPriority) {
           return android.R.drawable.star_big_on;
        } else {
            return android.R.drawable.star_big_off;
        }
    }

    @OnClick (R.id.submitButton)
    public void onSubmitButtonPressed (View view) {
        String name = mItemTitle.getText().toString();
        String date = getDateFromDatePicker (mDatePicker);
        String category = mCategorySpinner.getSelectedItem().toString();
        if (mCategorySpinner.getSelectedItemPosition() == 3) {
            category = mOtherCategoryEditText.getText().toString();
             }
        if (!name.equals("") &&
            date != null &&
            !category.equals("")) {
            mIsCompleted = mItemCompletedCheckbox.isChecked();
            ToDoItem item = new ToDoItem(name, category, date, mIsCompleted, mIsHighPriority, mUserPosition, mDatabaseId);
            Intent returnIntent = new Intent();
            returnIntent.putExtra(RESULT, item);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            Toast.makeText(this, "Please fill in all the data", Toast.LENGTH_LONG).show();
        }
    }


    private void setOtherCategoryField(boolean enabled) {
        mOtherCategoryEditText.setEnabled(enabled);
        if (enabled) {
            mOtherCategoryEditText.setHint(R.string.other_category_hint);
        } else {
            mOtherCategoryEditText.setHint(R.string.empty_stirng);
        }
    }

    public String getDateFromDatePicker(DatePicker datePicker){

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Locale locale = Locale.getDefault();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", locale);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        Date date = calendar.getTime();

        String strDate = dateFormatter.format(date);

        return strDate;
    }

    public void setSpinnerAdapter(String[] array, Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.category_spinner_item,
                array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
