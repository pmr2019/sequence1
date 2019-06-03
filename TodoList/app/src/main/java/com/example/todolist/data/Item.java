package com.example.todolist.data;

import androidx.annotation.NonNull;

public class Item {
    private String mDescription;
    private boolean mDone;

    public Item() {
        mDescription = "";
        mDone = false;
    }

    public Item(String description) {
        mDescription = description;
        mDone = false;
    }

    public Item(String description, boolean done) {
        mDescription = description;
        mDone = done;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean getDone() {
        return mDone;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setDone(boolean done) {
        mDone = done;
    }

    @NonNull
    @Override
    public String toString() {
        String msg;
        if (mDone) { msg = " (état : fait)"; }
        else { msg = " (état : non fait)"; }

        return "[item :" + mDescription + msg + "]";
    }
}
