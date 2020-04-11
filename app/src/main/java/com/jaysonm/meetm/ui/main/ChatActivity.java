package com.jaysonm.meetm.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.controllers.contacts.ChatMessagesAdapter;
import com.jaysonm.meetm.model.contacts.ChatMessage;
import com.jaysonm.meetm.model.home.CurrentLoggedUserFetcher;
import com.jaysonm.meetm.model.home.User;
import com.jaysonm.meetm.util.FontUtil;
import com.scaledrone.lib.HistoryRoomListener;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Message;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;
import com.scaledrone.lib.SubscribeOptions;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final String CHANNEL_ID = "k043LQ3U5O6EFM8x";

    private Room currentRoom;
    private String roomId;
    private ChatMessagesAdapter chatMessagesAdapter;
    private RecyclerView recyclerView;
    private MaterialButton sendButton;
    private AppCompatEditText messageText;

    //todo refactor code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        MaterialTextView toolbarTitle = findViewById(R.id.custom_tool_bar_title);
        toolbarTitle.setTypeface(FontUtil.getCustomTypeFace(ChatActivity.this, FontUtil.CUSTOM_FONTS.LEMON));

        AppCompatImageButton toolbarButton = findViewById(R.id.custom_tool_bar_button);
        toolbarButton.setImageResource(R.drawable.ic_close_custom_24dp);

        toolbarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        String selectedUserId = getIntent().getStringExtra("selectedUserId");
        FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();
        CurrentLoggedUserFetcher currentLoggedUserFetcher = CurrentLoggedUserFetcher.getInstance();
        currentLoggedUserFetcher.startFetching();

        //get current room key
        DatabaseReference userChatRef = FirebaseDatabase.getInstance().getReference("members/" + userFirebase.getUid() + "/chats/" + selectedUserId);
        userChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roomId = (String) dataSnapshot.getValue();
                Log.d(TAG, roomId);
                setupScaleDrone();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendButton = findViewById(R.id.chat_send_button);
        messageText = findViewById(R.id.chat_message_input_text);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                User user = currentLoggedUserFetcher.getUser();
                String message = messageText.getText().toString();
                if (!message.isEmpty()) {
                    ChatMessage tempChatMessage = new ChatMessage(
                            user.getName(),
                            message,
                            LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm")),
                            user.getPhotoUri());

                    Gson gson = new Gson();
                    currentRoom.publish(gson.toJson(tempChatMessage));

                    messageText.getText().clear();
                }
            }
        });

        this.recyclerView = findViewById(R.id.chat_messages_recycler_view);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

    }

    private void setupScaleDrone() {
        final Scaledrone scaledrone = new Scaledrone(CHANNEL_ID);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                Log.d(TAG, "Connected");

                currentRoom = scaledrone.subscribe(roomId, new RoomListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onOpen(Room room) {
                        Log.d(TAG, "Room opened: " + room.getName());
                    }

                    @Override
                    public void onOpenFailure(Room room, Exception ex) {
                        Log.e(TAG, room.getName() + ", Ex: " + ex.getMessage());
                    }

                    @Override
                    public void onMessage(Room room, Message message) {
                        Log.d(TAG, "Received: " + message.getData().asText());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = new Gson();
                                ChatMessage chatMessage = gson.fromJson(message.getData().asText(), ChatMessage.class);
                                chatMessagesAdapter.addMessage(chatMessage);
                            }
                        });
                    }
                }, new SubscribeOptions(50));

                currentRoom.listenToHistoryEvents(new HistoryRoomListener() {
                    @Override
                    public void onHistoryMessage(Room room, Message message) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG,message.getData().asText());
                                Gson gson = new Gson();
                                ChatMessage chatMessage = gson.fromJson(message.getData().asText(), ChatMessage.class);
                                chatMessagesAdapter.addMessage(chatMessage);
                            }
                        });
                    }
                });
            }

            @Override
            public void onOpenFailure(Exception ex) {
                Log.d(TAG, "Failed to subscribe to room: " + ex.getMessage());
            }

            @Override
            public void onFailure(Exception ex) {
                Log.d(TAG, "Failed to subscribe to room: " + ex.getMessage());
            }

            @Override
            public void onClosed(String reason) {
                Log.d(TAG, "Message: " + reason);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        this.chatMessagesAdapter = new ChatMessagesAdapter(chatMessages, ChatActivity.this);
        this.recyclerView.setAdapter(this.chatMessagesAdapter);
    }
}
