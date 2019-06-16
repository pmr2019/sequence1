package com.example.sujet_sequence_1.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ItemToDo implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("label")
    private String description;
    @SerializedName("checked")
    private int fait;
    @SerializedName("url")
    private String url;


    public String getDescription() {
        return this.description;
    }

    public boolean isFait() {
        return (fait == 1);
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    @NonNull
    @Override
    public String toString() {
        return "{\"description\" : \"" + description + "\"" +
                ", \"fait\" : \"" + (fait == 1) +
                "\" }";
    }
}
