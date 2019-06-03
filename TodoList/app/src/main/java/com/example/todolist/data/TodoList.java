package com.example.todolist.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TodoList {

    private String mTitle;
    private List<Item> mItems;

    public TodoList() {
        mTitle = "";
        mItems = new ArrayList<>();
    }

    public TodoList(String title) {
        mTitle = title;
        mItems = new ArrayList<>();
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setItems(List<Item> items) {
        mItems = items;
    }

    public List<Item> getItems() {
        return mItems;
    }

    public boolean containsItem(String description) {
        boolean a = mItems.contains(new Item(description, true));
        boolean b = mItems.contains(new Item(description, false));
        return (a && b);
    }

    public void addItem(Item item) {
        mItems.add(item);
    }

    public void removeItem(Item item) {
        mItems.remove(item);
    }

    @NonNull
    @Override
    public String toString() {
        String msg = "[liste :" + mTitle;
        if(mItems.isEmpty()) {
            msg += " (etat: vide)";
        } else {
            msg += " (contient";
            for (Item item : mItems) {
                msg += " " + item.toString();
            }
            msg += ")";
        }
        msg += "]";

        return msg;
    }
}
