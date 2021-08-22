package com.education4all;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.education4all.mathCoachAlg.tours.Tour;

import java.util.ArrayList;

public class TourAdapter
        extends RecyclerView.Adapter<TourAdapter.TourViewHolder> {

    private ArrayList<Tour> tours;
    private OnUserClickListener listener;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public interface OnUserClickListener {
        void onUserClick(int position);
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    public TourAdapter(ArrayList<Tour> users, Context context) {
        this.tours = users;
        this.context = context;
    }

    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tourblock, viewGroup, false);
        TourViewHolder viewHolder = new TourViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TourViewHolder tourViewHolder, int tourNumber) {
        Tour tour = tours.get(tourNumber);
        String info = tour.info();
        String datetime = tour.dateTime();

        if (tour.getRightTasks() == tour.getTotalTasks())
            info = context.getString(R.string.star) + " " + info;

        TextView tourdatetime = tourViewHolder.tourdatetime;
        tourdatetime.setId(tourNumber + 1);
        tourdatetime.setText(datetime);
        tourdatetime.setTag(tourNumber);

        TextView tourinfo = tourViewHolder.tourinfo;
        tourinfo.setId(tourNumber);
        tourinfo.setText(info);
        tourinfo.setTag(tourNumber);

        Button arrow = tourViewHolder.arrow;
        arrow.setTag(tourNumber);
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }

    public static class TourViewHolder extends RecyclerView.ViewHolder {

        TextView tourdatetime;
        TextView tourinfo;
        Button arrow;

        public TourViewHolder(@NonNull View itemView, final OnUserClickListener listener) {
            super(itemView);
            tourdatetime = itemView.findViewById(R.id.tourdatetime);
            tourinfo = itemView.findViewById(R.id.tourinfo);
            arrow = itemView.findViewById(R.id.arrow);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onUserClick(position);
                    }
                }
            });
        }
    }
}

