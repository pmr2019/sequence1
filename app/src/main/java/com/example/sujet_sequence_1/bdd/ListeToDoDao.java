package com.example.sujet_sequence_1.bdd;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ListeToDoDao {
    @Query("SELECT * FROM BddListeToDo")
    List<BddListeToDo> getAll();

    @Query("SELECT * FROM BddListeToDo WHERE loginUser IN (:login)")
    List<BddListeToDo> loadAllByUserLogin(String login);


    @Insert(onConflict = REPLACE)
    void insertAll(ArrayList<BddListeToDo> bddListeToDo);

    @Insert(onConflict = REPLACE)
    void insert(BddListeToDo bddListeToDo);
}

