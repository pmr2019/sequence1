package com.example.sujet_sequence_1.bdd;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface ItemToDoDao {

    @Query("SELECT * FROM BddItemToDo WHERE idList = :idListe")
    List<BddItemToDo> loadAllByIdList(int idListe);

    @Insert(onConflict = REPLACE)
    void insertAll(ArrayList<BddItemToDo> bddItemToDo);

    @Update
    public void updateItem(BddItemToDo bddItemToDo);


}
