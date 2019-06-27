package com.example.sujet_sequence_1.bdd;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {BddListeToDo.class, BddItemToDo.class, BddProfilListeToDo.class}, version = 2)
    public abstract class AppDatabase extends RoomDatabase {
        public abstract ItemToDoDao ItemToDoDao();
        public abstract ListeToDoDao ListeToDoDao();
        public abstract ProfilListeToDoDao ProfilListeToDoDao();
}
