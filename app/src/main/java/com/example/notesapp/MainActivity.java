package com.example.notesapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notesapp.adapter.NotesAdapter;
import com.example.notesapp.model.Note;
import com.example.notesapp.viewmodel.NotesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private NotesViewModel notesViewModel;
    private NotesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotesAdapter(new ArrayList<>(), new NotesAdapter.OnNoteLongClickListener() {
            @Override
            public void onNoteLongClick(Note note) {
                showDeleteDialog(note);
            }
        });
        recyclerView.setAdapter(adapter);

        // Swipe to Delete
        // Swipe to Delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Note note = adapter.getNoteAt(position);
                    notesViewModel.delete(note);
                    com.google.android.material.snackbar.Snackbar
                            .make(recyclerView, "Note deleted",
                                    com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> notesViewModel.insert(note)).show();
                }
            }
        }).attachToRecyclerView(recyclerView);

        // Initialize ViewModel
        notesViewModel = new ViewModelProvider(this).get(NotesViewModel.class);

        // Observe Data (Single Source of Truth)
        notesViewModel.getNotes().observe(this, notes -> {
            adapter.updateNotes(notes);
        });

        // Setup Search
        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                notesViewModel.setSearchQuery(newText);
                return true;
            }
        });

        // Floating Action Button to Add Note
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> showAddNoteDialog());
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Note");

        // Custom Layout for Input
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null);
        final EditText inputTitle = view.findViewById(R.id.inputTitle);
        final EditText inputContent = view.findViewById(R.id.inputContent);

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = inputTitle.getText().toString();
            String content = inputContent.getText().toString();
            String timestamp = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(new Date());

            if (!title.isEmpty()) {
                Note note = new Note(title, content, timestamp);
                notesViewModel.insert(note);
                // No need to manually update adapter, LiveData will do it
                ((RecyclerView) findViewById(R.id.recyclerView)).scrollToPosition(0);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showDeleteDialog(Note note) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    notesViewModel.delete(note);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
