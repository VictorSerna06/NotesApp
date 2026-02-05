package com.example.notesapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Base de Datos Room
// Implementada como singleton para garantizar una única instancia
@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteDataBase extends RoomDatabase {

    public abstract NoteDao noteDao();

    private static NoteDataBase INSTANCE;

    public static synchronized NoteDataBase getInstance(Context context) {

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            NoteDataBase.class,
                            "notes_db"
                    ).fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}