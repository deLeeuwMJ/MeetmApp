package com.jaysonm.meetm.model.events;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "event_table")
public class Event implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String imageUrl;
    private String date;
    private String location;
    private String price;
    private String ticketUrl;

    public Event(String name, String imageUrl, String date, String location, String price, String ticketUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.date = date;
        this.location = location;
        this.price = price;
        this.ticketUrl = ticketUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }
}
