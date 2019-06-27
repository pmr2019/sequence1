package com.example.sujet_sequence_1.bdd;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BddItemToDo {
    @PrimaryKey
    public int id;
    @ColumnInfo(name ="description")
    public String description;
    @ColumnInfo(name ="checked")
    public int fait;
    @ColumnInfo(name ="url")
    public String url;
    @ColumnInfo(name = "idList")
    public int idList;

    public BddItemToDo(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BddItemToDo{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", fait=" + fait +
                ", url='" + url + '\'' +
                ", idList=" + idList +
                '}';
    }
}
