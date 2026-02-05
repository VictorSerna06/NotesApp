package com.example.notesapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// DAO encargado de definir las operaciones de acceso a los datos
@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY date DESC")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :title || '%' ORDER BY date DESC")
    LiveData<List<Note>> getNoteByTitle(String title);

    @Insert
    void saveNote(Note note);

    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Query("UPDATE notes SET isCompleted =:newState WHERE id =:noteId")
    void updateCompleted(int noteId, boolean newState);
}
