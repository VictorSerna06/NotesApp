package com.example.notesapp.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

// Entidad Room que representa una nota dentro de la aplicación
@Entity(tableName = "notes")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private String date;
    private boolean isCompleted;

    // Constructor vacío (Requerido por Room)
    public Note() {
    }


    // Constructor principal
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        this.isCompleted = false;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    // Equals
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Note note = (Note) object;
        return isCompleted == note.isCompleted &&
                Objects.equals(title, note.title) &&
                Objects.equals(content, note.content) &&
                Objects.equals(date, note.date);
    }

    // HashCode
    @Override
    public int hashCode() {
        return Objects.hash(title, content, isCompleted, date);
    }
}
