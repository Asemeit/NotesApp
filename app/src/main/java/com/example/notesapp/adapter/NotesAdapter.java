package com.example.notesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notesapp.R;
import com.example.notesapp.model.Note;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;
    // Callback interface for Long Press
    private final OnNoteLongClickListener longClickListener;

    public interface OnNoteLongClickListener {
        void onNoteLongClick(Note note);
    }

    public NotesAdapter(List<Note> notes, OnNoteLongClickListener longClickListener) {
        this.notes = notes;
        this.longClickListener = longClickListener;
    }

    public void updateNotes(List<Note> newNotes) {
        this.notes = newNotes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.tvTitle.setText(note.getTitle());
        holder.tvContent.setText(note.getContent());
        holder.tvTimestamp.setText(note.getTimestamp());

        // Event Handling: Long Press Delete
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onNoteLongClick(note);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    // ViewHolder Structure
    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTimestamp;

        NoteViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}
