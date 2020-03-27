package com.example.cz2006trial.historyPage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cz2006trial.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter < RecyclerViewAdapter.ViewHolder > {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList < String > mDataset;
    private Context mContext;
    private OnNoteListener mOnNoteListener;

    private void debugOutput(String msg) {
        Log.d(TAG, msg);
//        System.out.println(TAG + " === " + msg);
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textName;
        RelativeLayout parentLayout;
        OnNoteListener onNoteListener;

        public ViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public RecyclerViewAdapter(Context context, ArrayList < String > dataset, OnNoteListener onNoteListener) {
        mDataset = dataset;
        mContext = context;
        mOnNoteListener = onNoteListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view, mOnNoteListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        debugOutput("onBindViewHolder: called for position = " + position);
        viewHolder.textName.setText(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
