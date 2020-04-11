package com.jaysonm.meetm.controllers.events;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.model.events.Event;
import com.jaysonm.meetm.ui.main.WebActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ImageViewHolder> {

    private List<Event> dataSet;
    private Context context;

    public EventAdapter(Context context) {
        this.dataSet = new LinkedList<>();
        this.context = context;
    }



    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_recycler_view_item, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(view);

        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder viewHolder, int i) {

        viewHolder.title.setText(dataSet.get(i).getName());
        viewHolder.date.setText(dataSet.get(i).getDate());
        viewHolder.location.setText(dataSet.get(i).getLocation());

        if (dataSet.get(i).getPrice() != null){
            viewHolder.price.setText(dataSet.get(i).getPrice());
        } else {
            viewHolder.price.setVisibility(View.INVISIBLE);
        }

        Picasso.get().load(dataSet.get(i).getImageUrl()).into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void submitList(List<Event> events) {
        this.dataSet = events;
    }


    class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private MaterialTextView title, date, location, price;

        ImageViewHolder(View itemview) {
            super(itemview);

            image = itemview.findViewById(R.id.event_image);
            title = itemview.findViewById(R.id.event_title);
            date = itemview.findViewById(R.id.event_date);
            location = itemview.findViewById(R.id.event_location);
            price = itemview.findViewById(R.id.event_price);

            itemview.setOnClickListener(v -> {
                Event event = dataSet.get(ImageViewHolder.super.getAdapterPosition());

                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("url", event.getTicketUrl());
                context.startActivity(intent);
            });
        }
    }

}