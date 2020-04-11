package com.jaysonm.meetm.model.contacts;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {

    @SerializedName("sender")
    public String sender;

    @SerializedName("message")
    public String message;

    @SerializedName("time")
    public String time;

    @SerializedName("senderPhotoUri")
    public String senderPhotoUri;

    public ChatMessage(String sender, String message, String time, String senderPhotoUri) {
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.senderPhotoUri = senderPhotoUri;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getSenderPhotoUri() {
        return senderPhotoUri;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSenderPhotoUri(String senderPhotoUri) {
        this.senderPhotoUri = senderPhotoUri;
    }
}
