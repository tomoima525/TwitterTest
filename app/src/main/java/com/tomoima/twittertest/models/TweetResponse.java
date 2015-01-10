package com.tomoima.twittertest.models;

import io.realm.RealmObject;

/**
 * Created by tomoaki on 2015/01/10.
 */
public class TweetResponse extends RealmObject {

    private int id;
    private String message;
    private String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
