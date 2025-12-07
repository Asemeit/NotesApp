package com.example.notesapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notesapp.R;
import com.example.notesapp.adapter.NotesAdapter;
import com.example.notesapp.db.DatabaseHelper;
import com.example.notesapp.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private NotesAdapter adapter;
    private List<Note> noteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Database
        db = new DatabaseHelper(this);

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Loading all notes on startup
        noteList.addAll(db.getAllNotes());

        adapter = new NotesAdapter(noteList, new NotesAdapter.OnNoteLongClickListener() {
            @Override
            public void onNoteLongClick(Note note) {
                showDeleteDialog(note);
            }
        });
        recyclerView.setAdapter(adapter);

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

        // Note: For simplicity, assuming dialog_add_note.xml exists or creating it
        // inline for this demo code isn't possible,
        // so I will use a standard alert dialog or just programmatically create the
        // view for this step if I could,
        // but since I am generating files, I will stick to the plan.
        // I'll create a simple View programmatically to avoid extra file complexity not
        // in the plan if needed,
        // but creating the layout file is cleaner. I will assume the layout exists or
        // use a standard text input.
        // Let's create the layout file dynamically in the next step to be safe.

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = inputTitle.getText().toString();
            String content = inputContent.getText().toString();
            String timestamp = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(new Date());

            if (!title.isEmpty()) {
                Note note = new Note(title, content, timestamp);
                // Persistence
                long id = db.addNote(note);
                note.setId((int) id);

                // Update UI
                noteList.add(0, note);
                adapter.notifyItemInserted(0);
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
                    // Delete from SQLite
                    db.deleteNote(note);
                    // Remove from List and Update UI
                    noteList.remove(note);
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
