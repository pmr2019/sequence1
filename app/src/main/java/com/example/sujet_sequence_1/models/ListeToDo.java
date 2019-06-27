package com.example.sujet_sequence_1.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ListeToDo implements Serializable {
    @SerializedName("label")
    private String titreListeToDo;
    @SerializedName("items")
    private ArrayList<ItemToDo> lesItems;
    @SerializedName("id")
    private int id;

    public ListeToDo(String titre) {
        this.lesItems = new ArrayList<>();
        this.titreListeToDo = titre;
    }

    public ListeToDo() {
        this.lesItems = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getTitreListeToDo() {
        return titreListeToDo;
    }

    public ArrayList<ItemToDo> getLesItems() {
        if (lesItems==null){
            lesItems = new ArrayList<>();
        }
        return lesItems;
    }

    public void setLesItems(ArrayList<ItemToDo> listItem) {
        this.lesItems = listItem;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return
                "{ \"titreListeToDo\" : \"" + titreListeToDo + "\"" +
                        ",\"id\" : \"" + id + "\"" +
                        ", \"lesItems\" : " + getLesItems().toString() +
                        "}";
    }

}
