package com.example.sujet_sequence_1.bdd;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class BddProfilListeToDo {

    @PrimaryKey @NonNull
    public String login;

}
