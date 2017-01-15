package com.example.alex.todolistapp.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alex.todolistapp.R;
import com.example.alex.todolistapp.model.ToDoItem;
import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter <RecyclerViewAdapter.ToDoListViewHolder> {


    public void setToDoItemList(ArrayList<ToDoItem> toDoItemList) {
        mToDoItemList = toDoItemList;
    }

    private ArrayList<ToDoItem> mToDoItemList;


    public RecyclerViewAdapter() {
        mToDoItemList = new ArrayList<>();
    }



    @Override
    public RecyclerViewAdapter.ToDoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.todo_list_item, parent, false);
        return new ToDoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ToDoListViewHolder holder, int position) {
            holder.bindToDoItem(mToDoItemList.get(position));
        }

    @Override
    public int getItemCount() {
        if (mToDoItemList != null) {
            if (mToDoItemList.size() != 0) {
                return mToDoItemList.size();
            }
        }
        return 0;
    }



    class ToDoListViewHolder extends RecyclerView.ViewHolder {

        CheckBox todoItemCheckbox;
        ImageView priorityImageView;
        TextView itemCategory;
        TextView itemDueDate;

        private void bindToDoItem(ToDoItem item) {
            todoItemCheckbox.setText(item.getName());
            todoItemCheckbox.setChecked(item.isCompleted());
            itemCategory.setText(item.getCategoryWithPrefix());
            priorityImageView.setImageResource(item.getItemImageResource());
            itemDueDate.setText(item.getDueDateArray()[2] + "/" + item.getDueDateArray()[1]);
        }


        private ToDoListViewHolder(View itemView) {
            super(itemView);
            todoItemCheckbox = (CheckBox) itemView.findViewById(R.id.todoItemCheckBox);
            priorityImageView = (ImageView) itemView.findViewById(R.id.priorityImageView);
            itemCategory = (TextView) itemView.findViewById(R.id.categoryTextView);
            itemDueDate = (TextView) itemView.findViewById(R.id.dueDateTextView);

        }

    }
}