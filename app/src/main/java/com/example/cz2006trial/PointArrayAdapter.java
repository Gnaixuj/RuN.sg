package com.example.cz2006trial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cz2006trial.model.Point;

import java.util.ArrayList;
import java.util.List;

public class PointArrayAdapter extends RecyclerView.Adapter<PointArrayAdapter.ViewHolder> implements Filterable {

    private int layout;
    private ArrayList<Point> pointList;
    private ArrayList<Point> pointListFiltered;
    private PointsAdapterListener listener;

    // Constructor of the class
    public PointArrayAdapter(int layoutId, ArrayList<Point> pointList, PointsAdapterListener listener) {
        layout = layoutId;
        this.pointList = pointList;
        this.listener = listener;
        this.pointListFiltered = pointList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView image = holder.image;
        TextView item = holder.item;
        item.setText(pointListFiltered.get(position).getName());

        if (pointListFiltered.get(position).getType().equals("access"))
            image.setImageResource(R.drawable.ic_access);
        else
            image.setImageResource(R.drawable.ic_park);
    }

    @Override
    public int getItemCount() {
        return pointListFiltered == null ? 0 : pointListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    pointListFiltered = pointList;
                } else {
                    ArrayList<Point> filteredList = new ArrayList<>();
                    for (Point row : pointList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    pointListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = pointListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults (CharSequence charSequence, FilterResults
                    filterResults){
                pointListFiltered = (ArrayList<Point>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item;
        public ImageView image;

        public ViewHolder(View itemView) {

            super(itemView);
            item = itemView.findViewById(R.id.list_name);
            image = itemView.findViewById(R.id.list_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onPointSelected(pointListFiltered.get(getAdapterPosition()));
                }
            });

        }
    }

    public interface PointsAdapterListener {
        void onPointSelected(Point point);
    }
}
