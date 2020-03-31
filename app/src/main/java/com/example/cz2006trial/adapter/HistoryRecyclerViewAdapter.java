package com.example.cz2006trial.adapter;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cz2006trial.R;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter < HistoryRecyclerViewAdapter.ViewHolder > {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList <Pair< String, Date>> mDataset;
    private ArrayList <Pair< String, Date>> checkedItems;

    private OnItemListener mOnItemListener;
    private boolean checkAll = false;
    private boolean showAll = false;

    private void debugOutput(String msg) {
        Log.d(TAG, msg);
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        TextView textName;
        CheckBox checkItem;
        LinearLayout parentLayout;
        OnItemListener onItemListener;
        boolean checked;
        boolean showed;

        ViewHolder(View itemView, OnItemListener onItemListener) {
            super(itemView);
            checked = false;
            showed = false;

            textName = itemView.findViewById(R.id.text_name);

            checkItem = itemView.findViewById(R.id.checkBox);
            checkItem.setOnCheckedChangeListener(this);
            checkItem.setVisibility(View.INVISIBLE);

            parentLayout = itemView.findViewById(R.id.parent_layout);

            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);

        }

        void setCheckedItem(boolean check) {
            //checked = check;
            checkItem.setChecked(check);
        }

        void setShowCheckBox(boolean show) {
            showed = show;
            if (show)
                checkItem.setVisibility(View.VISIBLE);
            else
                checkItem.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                //todo
                checked = true;
                debugOutput("value of member" + checked);
                if (textName!= null) {
                    if (checkedItems != null) {
                        debugOutput(String.valueOf(textName.getText()));
                        checkedItems.add(mDataset.get(getAdapterPosition()));
                        debugOutput("added position " + getAdapterPosition());
                    }
                }

            }
            else {
                //todo
                checked = false;
                debugOutput("value of member"+checked);
                if (textName!= null) {
                    if (checkedItems != null) {
                        debugOutput(String.valueOf(textName.getText()));
                        checkedItems.remove(mDataset.get(getAdapterPosition()));
                        debugOutput("removed position " + getAdapterPosition());
                    }
                }
            }
        }
    }

    public HistoryRecyclerViewAdapter(Context context, ArrayList <Pair< String, Date>>  dataset, OnItemListener onItemListener) {
        mDataset = dataset;
        mOnItemListener = onItemListener;
        checkedItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item, parent, false);
        return new ViewHolder(view, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        debugOutput("onBindViewHolder: called for position = " + position);

        String pattern = "EEEE, MMMM dd - HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date timestamp = mDataset.get(position).second;
        viewHolder.textName.setText((position + 1) + ". " + simpleDateFormat.format(timestamp));

        viewHolder.setCheckedItem(checkAll);
        viewHolder.setShowCheckBox(showAll);

        if (viewHolder.checked) {
            checkedItems.add(mDataset.get(position));
        }
        else {
            checkedItems.remove(mDataset.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setChecked (boolean check) {
        checkAll = check;
        notifyDataSetChanged();
    }

    public void showCheckBox (boolean show) {
        showAll = show;
        if (!show) checkAll = show;
        notifyDataSetChanged();
    }


    public ArrayList <Pair< String, Date>>  removeAllChecked() {
        mDataset.removeAll(checkedItems);
        if (checkedItems != null)
            return checkedItems;
        return null;
    }
}
