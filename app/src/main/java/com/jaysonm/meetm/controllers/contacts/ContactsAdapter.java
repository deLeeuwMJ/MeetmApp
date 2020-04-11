package com.jaysonm.meetm.controllers.contacts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.model.home.User;
import com.jaysonm.meetm.ui.main.ProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ImageViewHolder> {

    private static final String TAG = ContactsAdapter.class.getSimpleName();

    private ArrayList<User> dataSet;
    private Activity activity;

    public ContactsAdapter(ArrayList<User> dataSet, Activity activity) {
        this.dataSet = dataSet;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ContactsAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        ContactsAdapter.ImageViewHolder imageViewHolder = new ContactsAdapter.ImageViewHolder(view);

        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ImageViewHolder viewHolder, int i) {

        viewHolder.name.setText(dataSet.get(i).getName());
        Picasso.get().load(Uri.parse(dataSet.get(i).getPhotoUri())).into(viewHolder.image);

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private MaterialTextView name;

        ImageViewHolder(View itemview) {
            super(itemview);

            image = itemview.findViewById(R.id.contact_image);
            name = itemview.findViewById(R.id.contact_name);

            itemview.setOnClickListener(v -> {
                Log.d(TAG, String.valueOf(dataSet.size()));
                User userSelected = dataSet.get(ContactsAdapter.ImageViewHolder.super.getAdapterPosition());

                Intent intent = new Intent(activity, ProfileActivity.class);
                intent.putExtra("selectedUser", userSelected);
                activity.startActivity(intent);
            });
        }
    }
}
