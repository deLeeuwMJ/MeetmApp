package com.jaysonm.meetm.model.home;

import android.annotation.SuppressLint;
import android.icu.util.LocaleData;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User implements Serializable {

    public String name;
    public String email;
    public String phoneNumber;
    public String aboutMe;
    public String birthDay;
    public String photoUri;
    public String location;
    public String job;
    private GeoLocation geoLocation;
    private String userId;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    @SuppressLint("NewApi")
    public User(@NonNull String name,@NonNull String email, String phoneNumber, String aboutMe, String birthDay,@NonNull String photoUri, String location, String job) {
        this.name = name;
        this.email = email;

        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        } else {
            this.phoneNumber = "Phone number isn't set yet";
        }

        if (aboutMe != null) {
            this.aboutMe = aboutMe;
        } else {
            this.aboutMe = "About me isn't updated yet";
        }

        if (birthDay != null) {
            this.birthDay = birthDay;
        } else {
            this.birthDay = LocalDate.of(1999, 1, 1).toString();
        }

        this.photoUri = photoUri;

        if (location != null) {
            this.location = location;
        } else {
            this.location = "Netherlands";
        }

        if (job != null) {
            this.job = job;
        } else {
            this.job = "Job not specified";
        }
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        result.put("phoneNumber", phoneNumber);
        result.put("aboutMe", aboutMe);
        result.put("birthDay", birthDay);
        result.put("photoUri", photoUri);
        result.put("location", location);
        result.put("job", job);
        return result;
    }

    @Exclude
    public String getName() {
        return name;
    }

    @Exclude
    public String getEmail() {
        return email;
    }

    @Exclude
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Exclude
    public String getAboutMe() {
        return aboutMe;
    }

    @Exclude
    public String getBirthDay() {
        return birthDay;
    }

    @Exclude
    public String getPhotoUri() {
        return photoUri;
    }

    @Exclude
    public String getLocation() {
        return location;
    }

    @Exclude
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    @Exclude
    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    @Exclude
    public String getUserId() {
        return userId;
    }

    @Exclude
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Exclude
    public String getJob() {
        return job;
    }
}
