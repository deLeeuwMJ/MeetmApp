package com.jaysonm.meetm.controllers.meeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.model.meetings.Meet;

import java.util.List;

public class MeetAdapter extends RecyclerView.Adapter<MeetAdapter.ImageViewHolder> {

    private List<Meet> dataSet;
    private Context context;

    public MeetAdapter(List<Meet> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
    }

    public void removeMeeting(int position) {
        dataSet.remove(position);
    }

    public List<Meet> getDataSet() {
        return dataSet;
    }

    @NonNull
    @Override
    public MeetAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_item, parent, false);
        MeetAdapter.ImageViewHolder imageViewHolder = new MeetAdapter.ImageViewHolder(view);

        return imageViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder viewHolder, int position) {
        viewHolder.name.setText(dataSet.get(position).getName());
        viewHolder.time.setText(dataSet.get(position).getTime() + " - " + dataSet.get(position).getDate());
        viewHolder.location.setText(dataSet.get(position).getLocation());
    }

    @Override
    public int getItemCount() {
        if (dataSet == null)
            return 0;

        return dataSet.size();
    }


    class ImageViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView name, time, location;

        ImageViewHolder(View itemview) {
            super(itemview);

            name = itemview.findViewById(R.id.meet_item_name);
            time = itemview.findViewById(R.id.meet_item_time);
            location = itemview.findViewById(R.id.meet_item_location);
        }
    }

}