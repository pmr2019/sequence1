package com.example.sujet_sequence_1.bdd;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

    @Dao
    public interface ProfilListeToDoDao {
        @Query("SELECT * FROM BddProfilListeToDo")
        List<BddProfilListeToDo> getAll();

        @Query("SELECT * FROM BddProfilListeToDo WHERE login LIKE :first ")
        BddProfilListeToDo findByLogin(String first);

        @Insert
        void insertAll(BddProfilListeToDo... BddProfilListeToDo);

        @Delete
        void delete(BddProfilListeToDo BddProfilListeToDo);
}
