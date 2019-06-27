package com.example.sujet_sequence_1.bdd;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BddListeToDo {

    @PrimaryKey
    public int id;
    @ColumnInfo(name ="label")
    public String titreListeToDo;
    @ColumnInfo(name = "loginUser")
    public String login;

    public BddListeToDo(int id, String titreListeToDo, String login) {
        this.id = id;
        this.titreListeToDo = titreListeToDo;
        this.login = login;
    }



}
