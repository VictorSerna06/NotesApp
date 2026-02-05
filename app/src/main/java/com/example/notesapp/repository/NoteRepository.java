package com.example.notesapp.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.notesapp.data.Note;
import com.example.notesapp.data.NoteDao;
import com.example.notesapp.data.NoteDataBase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Repository que actúa como intermediario entre la capa de datos
// Maneja la ejecución de operaciones en segundo plano
public class NoteRepository {

    private final NoteDataBase noteDataBase;
    private final NoteDao noteDao;
    private final ExecutorService executorService;

    // Constructor
    public NoteRepository(Context context) {
        noteDataBase = NoteDataBase.getInstance(context);
        noteDao = noteDataBase.noteDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Note>> getAllNotes() {
        return noteDao.getAllNotes();
    }

    public LiveData<List<Note>> getNoteByTitle(String title) {
        return noteDao.getNoteByTitle(title);
    }

    public void saveNote(Note note) {
        executorService.execute(() -> noteDao.saveNote(note));
    }

    public void updateNote(Note note) {
        executorService.execute(() -> noteDao.updateNote(note));
    }

    public void deleteNote(Note note) {
        executorService.execute(() -> noteDao.deleteNote(note));
    }

    public void updateCompleted(int noteId, boolean isCompleted) {
        executorService.execute(() -> noteDao.updateCompleted(noteId, isCompleted));
    }
}
