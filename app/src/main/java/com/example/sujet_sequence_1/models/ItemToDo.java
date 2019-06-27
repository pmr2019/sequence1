package com.example.sujet_sequence_1.models;

import androidx.annotation.NonNull;

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

    private boolean modified = false;

    private boolean notOnline = false;

    public ItemToDo(int id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isFait() {
        return (fait == 1);
    }

    public boolean isNotOnline() {
        return notOnline;
    }

    public  int getFait(){
        return fait;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public boolean isModified() {
        return modified;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFait(int fait) {
        this.fait = fait;
    }
    public void setFait(boolean fait) {
        this.fait = fait ? 1:0;
    }

    public void setNotOnline(boolean notOnline) {
        this.notOnline = notOnline;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    @NonNull
    @Override
    public String toString() {
        return "{\"description\" : \"" + description + "\"" +
                ", \"fait\" : \"" + (fait == 1) +
                "\" }";
    }
}
