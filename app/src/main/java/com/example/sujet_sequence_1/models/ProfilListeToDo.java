package com.example.sujet_sequence_1.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ProfilListeToDo implements Serializable {
    private String login;
    @SerializedName("lists")
    private ArrayList<ListeToDo> mesListeToDo;


    public ProfilListeToDo(String login) {
        this.mesListeToDo = new ArrayList<>();
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public ArrayList<ListeToDo> getMesListeToDo() {
        return mesListeToDo;
    }

    public void setMesListeToDo(ArrayList<ListeToDo> mesListeToDo) {
        this.mesListeToDo = mesListeToDo;
    }

    @NonNull
    @Override
    public String toString() {
        return "ProfilListeToDo{" +
                "login='" + login + '\'' +
                ", mesListeToDo=" + mesListeToDo +
                '}';
    }
}
