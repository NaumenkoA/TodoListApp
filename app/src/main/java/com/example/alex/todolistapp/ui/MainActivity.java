package com.example.alex.todolistapp.ui;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.todolistapp.R;
import com.example.alex.todolistapp.adapters.RecyclerItemClickListener;
import com.example.alex.todolistapp.adapters.RecyclerViewAdapter;
import com.example.alex.todolistapp.database.TodoListDataSource;
import com.example.alex.todolistapp.model.ToDoItem;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;


public class MainActivity extends AppCompatActivity {

    private static final int NEW_ITEM_REQUEST = 1;
    public static final String TO_DO_ITEM = "to_do_item";
    public static final String CREATE_NEW_ITEM = "create_new_item";
    public static final String UPDATE_ITEM = "update_item";
    private static final int UPDATE_ITEM_REQUEST = 2;

    private TodoListDataSource mDataSource;
    RecyclerViewAdapter mAdapter;
    private int mUpdatedItemPosition;
    private boolean mIsItemMovingActivated;
    private int mOldPosition;

    @BindView (R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView (R.id.emptyTextView) TextView mEmptyTextView;
    @BindView(R.id.sortOrderSpinner) Spinner mSortSpinner;
    @BindView(R.id.ascDescSpinner) Spinner mAscDescSpinner;
    @BindView(R.id.activity_main) RelativeLayout mMainActivity;
    private ArrayList <ToDoItem> mToDoItemArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSpinnerAdapter(getResources().getStringArray(R.array.sort_params), mSortSpinner);
        setSpinnerAdapter(getResources().getStringArray(R.array.sort_order), mAscDescSpinner);
        mEmptyTextView.setVisibility(View.VISIBLE);
        setListAdapterParams();
        mDataSource = new TodoListDataSource(this);
        mToDoItemArrayList = new ArrayList<>();
        ArrayList <ToDoItem> loadedList = mDataSource.readItems();
        mIsItemMovingActivated = false;
        mSortSpinner.setSelection(0);
        mAscDescSpinner.setSelection(0);
        mAscDescSpinner.setEnabled(false);
        if (loadedList != null) {
            mEmptyTextView.setVisibility(View.INVISIBLE);
            mToDoItemArrayList = loadedList;
            refreshScreen();
        }

        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    mAscDescSpinner.setEnabled(false);
                    mToDoItemArrayList = mDataSource.readItems();
                    refreshScreen();
                       } else {
                    mAscDescSpinner.setEnabled(true);
                    mToDoItemArrayList = mDataSource.sortItems(mSortSpinner.getSelectedItemPosition(),
                    mAscDescSpinner.getSelectedItemPosition());
                    refreshScreen();
                   }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mAscDescSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mSortSpinner.getSelectedItemPosition() != 0) {
                    mToDoItemArrayList = mDataSource.sortItems(mSortSpinner.getSelectedItemPosition(),
                            mAscDescSpinner.getSelectedItemPosition());
                    refreshScreen();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
       }

    private void refreshScreen() {
        mAdapter.setToDoItemList(mToDoItemArrayList);
        mAdapter.notifyDataSetChanged();
    }


    @OnClick (R.id.addButton)
    public void addTodoItemActivity() {
        Intent intent = new Intent(this, ToDoActivity.class);
        intent.setAction(CREATE_NEW_ITEM);
        startActivityForResult(intent, NEW_ITEM_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_ITEM_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                ToDoItem newItem = data.getParcelableExtra(ToDoActivity.RESULT);
                addItem(newItem);
               }
        }
        if (requestCode == UPDATE_ITEM_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                ToDoItem updatedItem = data.getParcelableExtra(ToDoActivity.RESULT);
                updateItem(updatedItem);
                }
        }
    }

    private void updateItem(ToDoItem updatedItem) {
        mDataSource.updateItem(updatedItem);
        mToDoItemArrayList.remove(mUpdatedItemPosition);
        mToDoItemArrayList.add(mUpdatedItemPosition, updatedItem);
        mRecyclerView.setItemAnimator(new FlipInTopXAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(500);
        refreshAsItemUpdated(mUpdatedItemPosition);
    }


    private void addItem(ToDoItem item) {
        if (mToDoItemArrayList == null) {
            mToDoItemArrayList = new ArrayList<>();
        }
        int position = mAdapter.getItemCount();
        item.setUserPosition(position);
        mRecyclerView.setItemAnimator(new FadeInAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(500);
        long dataBaseId = mDataSource.addItem(item);
        item.setDatabaseId(dataBaseId);
        mToDoItemArrayList.add(item);
        refreshAsItemAdded(position);
        if (mAdapter.getItemCount() == 1) {
            mEmptyTextView.setVisibility(View.INVISIBLE);
        }
        }

    private void refreshAsItemAdded(int position) {
        mAdapter.setToDoItemList(mToDoItemArrayList);
        mAdapter.notifyItemInserted(position);
    }


    private void refreshAsItemUpdated(int position) {
        mAdapter.setToDoItemList(mToDoItemArrayList);
        mAdapter.notifyItemRemoved(position);
        mAdapter.notifyItemInserted(position);
    }

    private void refreshAsItemRemoved(int position) {
        mAdapter.notifyItemRemoved(position);
        }


    private void removeItem(ToDoItem item, int position) {
        mRecyclerView.setItemAnimator(new SlideInRightAnimator());
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        mToDoItemArrayList.remove(position);
        mDataSource.deleteItem(item);
        refreshAsItemRemoved(position);
        updateItemPositionsInDatabase();
}

    private void updateItemPositionsInDatabase() {
        ArrayList <ToDoItem> loadedList = mDataSource.readItems();
        if (loadedList != null) {
            refreshItemPositions(loadedList);
            mDataSource.updateItems(loadedList);
            if (mSortSpinner.getSelectedItemPosition() == 0) {
                mToDoItemArrayList = mDataSource.readItems();
            } else {
                mToDoItemArrayList = mDataSource.sortItems(mSortSpinner.getSelectedItemPosition(),
                        mAscDescSpinner.getSelectedItemPosition());
            }
            mAdapter.setToDoItemList(mToDoItemArrayList);
        }
    }

    private void refreshItemPositions(ArrayList<ToDoItem> loadedList) {
        int i = 0;
        for (ToDoItem item : loadedList) {
            item.setUserPosition(i);
            i++;
        }
    }


    private void updateItemRequest(ToDoItem itemToUpdate) {
        Intent intent = new Intent(this, ToDoActivity.class);
        intent.setAction(UPDATE_ITEM);
        intent.putExtra(TO_DO_ITEM, itemToUpdate);
        startActivityForResult(intent, UPDATE_ITEM_REQUEST);
    }

    private void moveItem(int oldPosition, int newPosition) {
        if (oldPosition != newPosition) {
        ToDoItem movedItem = mToDoItemArrayList.get(oldPosition);
        mToDoItemArrayList.remove(oldPosition);
        mToDoItemArrayList.add(newPosition, movedItem);
        refreshItemPositions(mToDoItemArrayList);
        mDataSource.updateItems(mToDoItemArrayList);
        mAdapter.setToDoItemList(mToDoItemArrayList);
        mAdapter.notifyItemMoved(oldPosition, newPosition);
        }
        mIsItemMovingActivated = false;
    }

    private void setListAdapterParams() {
        mAdapter = new RecyclerViewAdapter();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {
                           setOnItemClickListener(view, mIsItemMovingActivated, position);
                        }

                    @Override public void onLongItemClick(View view, final int i) {
                        final PopupMenu popup = new PopupMenu(MainActivity.this, view);
                        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case (R.id.edit):
                                        updateItemRequest(mToDoItemArrayList.get(i));
                                        mUpdatedItemPosition = i;
                                        popup.dismiss();
                                        return true;
                                    case (R.id.move):
                                        if (mSortSpinner.getSelectedItemPosition() != 0) {
                                            Toast.makeText(MainActivity.this, "Moving items is not available when sorting is activated", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, "Tap on new position for the item", Toast.LENGTH_SHORT).show();
                                            mIsItemMovingActivated = true;
                                            mOldPosition = i;
                                        }
                                        return true;
                                    case (R.id.remove):
                                        removeItem(mToDoItemArrayList.get(i), i);
                                        popup.dismiss();
                                        return true;
                                }
                                return true;
                            }
                        });
                    }
                })
        );

    }

    private void setOnItemClickListener(View view, boolean isItemMovingActivated, int position) {
        if (!isItemMovingActivated){
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.todoItemCheckBox);
            checkBox.toggle();
            mToDoItemArrayList.get(position).setCompleted(checkBox.isChecked());
            mDataSource.updateItem(mToDoItemArrayList.get(position));
        } else {
            moveItem(mOldPosition, position);
        }
        }


    public void setSpinnerAdapter(String[] array, Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.sort_spinner_item,
                array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
