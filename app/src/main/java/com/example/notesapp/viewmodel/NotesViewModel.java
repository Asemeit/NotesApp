package com.example.notesapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.notesapp.data.repository.NoteRepository;
import com.example.notesapp.model.Note;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class NotesViewModel extends AndroidViewModel {

    private NoteRepository repository;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final LiveData<List<Note>> notes;

    public NotesViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);

        // Dynamic switching: If query is empty -> show all, else -> search
        notes = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.isEmpty()) {
                return repository.getAllNotes();
            } else {
                return repository.searchNotes(query);
            }
        });
    }

    public LiveData<List<Note>> getNotes() {
        return notes;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    // Proxy methods
    public LiveData<List<Note>> getAllNotes() {
        return notes;
    } // Deprecated but keeping for compatibility if needed

    public void insert(Note note) {
        repository.insert(note);
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public void update(Note note) {
        repository.update(note);
    }
}
