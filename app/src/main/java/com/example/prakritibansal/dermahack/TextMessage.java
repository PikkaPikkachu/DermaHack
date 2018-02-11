package com.example.prakritibansal.dermahack;

import java.util.List;

/**
 * Created by prakritibansal on 2/11/18.
 */


public class TextMessage {
    private String message;
    private String from;
    private String timeStamp;
    private String photoUrl;
    private String imageUrl;


    public TextMessage(final String message, final String from, final String timeStamp) {
        this.message = message;
        this.from = from;
        this.timeStamp = timeStamp;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getImageUrl(){
        return imageUrl;
    }

}

