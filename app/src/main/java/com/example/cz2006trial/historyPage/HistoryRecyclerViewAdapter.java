package com.example.cz2006trial.historyPage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cz2006trial.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter < HistoryRecyclerViewAdapter.ViewHolder > {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList < String > mDataset;
    private ArrayList < String > checkedItems;
    private Context mContext;
    private OnItemListener mOnItemListener;
    private ViewHolder holder;
    boolean checkAll = false;
    boolean showAll = false;
    boolean removeAll = false;

    private void debugOutput(String msg) {
        Log.d(TAG, msg);
//        System.out.println(TAG + " === " + msg);
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

        public ViewHolder(View itemView, OnItemListener onItemListener) {
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

        public void setCheckedItem (boolean check) {
            checked = check;
            checkItem.setChecked(check);
        }

        public void setShowCheckBox (boolean show) {
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

/*        public void remove() {
            if (checked) {
                removeItem(getAdapterPosition());
            }
        }*/

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                //todo
                checked = true;
                debugOutput("value of member" + checked);
                if (textName!= null) {
                    if (checkedItems != null) {
                        debugOutput(String.valueOf(textName.getText()));
                        checkedItems.add(String.valueOf(textName.getText()));
                        debugOutput("position"+getAdapterPosition());
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
                        checkedItems.remove(String.valueOf(textName.getText()));
                        debugOutput("position"+getAdapterPosition());
                    }
                }
            }
        }
    }

    public HistoryRecyclerViewAdapter(Context context, ArrayList < String > dataset, OnItemListener onItemListener) {
        mDataset = dataset;
        mContext = context;
        mOnItemListener = onItemListener;
        checkedItems = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item, parent, false);
        holder = new ViewHolder(view, mOnItemListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        debugOutput("onBindViewHolder: called for position = " + position);
        viewHolder.textName.setText(mDataset.get(position));
        viewHolder.setCheckedItem(checkAll);
        viewHolder.setShowCheckBox(showAll);

        if (viewHolder.checked) {
            checkedItems.add(mDataset.get(position));
        }
/*        if (removeAll) {
            viewHolder.remove();
            removeAll= false;
        }*/
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


    public ArrayList<String> removeAllChecked() {
        mDataset.removeAll(checkedItems);
        if (checkedItems != null)
            return checkedItems;
        return null;
        //notifyDataSetChanged();
    }
}
