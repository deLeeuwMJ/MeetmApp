package com.jaysonm.meetm.controllers.contacts;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.model.contacts.ChatMessage;
import com.jaysonm.meetm.model.home.CurrentLoggedUserFetcher;
import com.jaysonm.meetm.model.home.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessagesAdapter extends RecyclerView.Adapter {

    private static final String TAG = ChatMessagesAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private ArrayList<ChatMessage> dataSet;
    private Activity activity;

    public ChatMessagesAdapter(ArrayList<ChatMessage> dataSet, Activity activity) {
        this.dataSet = dataSet;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        User userLogged = CurrentLoggedUserFetcher.getInstance().getUser();

        ChatMessage message = dataSet.get(position);

        Log.d(TAG, message.getSender());
        Log.d(TAG, userLogged.getName());

        if (message.getSender().equals(userLogged.getName())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_message_sent_item, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_message_received_item, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ChatMessage message = dataSet.get(i);

        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) viewHolder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) viewHolder).bind(message);
        }
    }

    public void addMessage(ChatMessage chatMessage) {
        dataSet.add(chatMessage);
        notifyDataSetChanged();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        private MaterialTextView message, time;

        SentMessageHolder(View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.chat_message);
            time = itemView.findViewById(R.id.chat_message_time);
        }

        void bind(ChatMessage message) {
            this.message.setText(message.getMessage());
            this.time.setText(message.getTime());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        private MaterialTextView sender, message, time;
        private CircleImageView userImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            sender = itemView.findViewById(R.id.chat_message_name);
            message = itemView.findViewById(R.id.chat_message);
            time = itemView.findViewById(R.id.chat_message_time);
            userImage = itemView.findViewById(R.id.chat_message_image);
        }

        void bind(ChatMessage message) {
            this.message.setText(message.getMessage());
            this.time.setText(message.getTime());
            this.sender.setText(message.getSender());

            Picasso.get().load(Uri.parse(message.getSenderPhotoUri())).into(this.userImage);
        }
    }
}
