package com.example.todolist.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {

    private String mName;
    private List<TodoList> mLists;

    public UserProfile() {
        mName = "";
        mLists = new ArrayList<>();
    }

    public UserProfile(String name) {
        mName = name;
        mLists = new ArrayList<>();
    }

    public UserProfile(List<TodoList> lists) {
        mName = "";
        mLists = lists;
    }

    public UserProfile(String name, List<TodoList> lists) {
        mName = name;
        mLists = lists;
    }

    public List<TodoList> getLists() {
        return mLists;
    }

    public TodoList getList(String listTitle) {
        for (TodoList todoList: mLists) {
            if(todoList.getTitle().matches(listTitle)) {
                return todoList;
            }
        }
        return new TodoList();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setLists(List<TodoList> lists) {
        mLists = lists;
    }

    public void addList(TodoList list) {
        mLists.add(list);
    }

    public void removeList(TodoList list) {
        mLists.remove(list);
    }

    @NonNull
    @Override
    public String toString() {
        String msg = "[utilisateur :" + mName;
        if(mLists.isEmpty()) {
            msg += " (etat: aucune liste)";
        } else {
            msg += " (dispose de ";
            for (TodoList item : mLists) {
                msg += " " + item.toString();
            }
            msg += ")";
        }
        msg += "]";

        return msg;
    }
}
