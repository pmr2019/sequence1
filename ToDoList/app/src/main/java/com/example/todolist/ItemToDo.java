package com.example.todolist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ItemToDo {
    private String description;
    private boolean fait;

    public ItemToDo(){
        this.description="vide";
        this.fait=false;
    }

    public ItemToDo(String description){
        this();
        this.description = description;
    }

    public ItemToDo(String description, boolean fait){
        this(description);
        this.fait = fait;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFait() {
        return fait;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFait(boolean fait) {
        this.fait = fait;
    }

    @Override
    public String toString() {
        return "ItemToDo{" +
                "description='" + description + '\'' +
                ", fait=" + fait +
                '}';
    }

    public static void main(String[] args) {
        ItemToDo i1 = new ItemToDo("Description", true);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        System.out.println(gson.toJson(i1));

    }
}
