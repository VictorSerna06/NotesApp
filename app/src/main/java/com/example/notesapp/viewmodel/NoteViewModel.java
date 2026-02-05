package com.example.notesapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.notesapp.data.Note;
import com.example.notesapp.repository.NoteRepository;

import java.util.List;

// ViewModel encargado de exponer los datos a la UI
public class NoteViewModel extends AndroidViewModel {

    private final NoteRepository repository;

    // Constructor del ViewModel
    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
    }

    public LiveData<List<Note>> getAllNotes() {
        return repository.getAllNotes();
    }

    public LiveData<List<Note>> getNoteByTitle(String title) {
        return repository.getNoteByTitle(title);
    }

    public void saveNote(Note note) {
        repository.saveNote(note);
    }

    public void updateNote(Note note) {
        repository.updateNote(note);
    }

    public void deleteNote(Note note) {
        repository.deleteNote(note);
    }

    public void updateCompleted(int noteId, boolean isCompleted) {
        repository.updateCompleted(noteId, isCompleted);
    }
}
