package com.example.notesapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.data.Note;
import com.example.notesapp.databinding.ItemNoteBinding;

import org.jetbrains.annotations.NotNull;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteViewHolder> {

    private OnLongClickNote onLongClickNote;
    private OnCheckNote onCheckNote;

    // DiffUtil: optimiza la actualización del RecyclerView comparando elementos y contenido
    public static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {

        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.equals(newItem);
        }
    };

    // Constructor del Adapter
    public NoteAdapter(OnLongClickNote onLongClickNote, OnCheckNote onCheckNote) {
        super(DIFF_CALLBACK);
        this.onLongClickNote = onLongClickNote;
        this.onCheckNote = onCheckNote;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteBinding binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = getItem(position);
        holder.bind(note);

        // LongClick para editar nota
        holder.itemView.setOnLongClickListener((v) -> {
            onLongClickNote.onLongClickItem(note);
            return true;
        });

        // Evita ejecuciones innecesarias al reciclar el ViewHolder
        holder.binding.isCompleted.setOnCheckedChangeListener(null);
        holder.binding.isCompleted.setChecked(note.isCompleted());
        holder.binding.isCompleted.setOnCheckedChangeListener((btnView, isChecked) -> {
            onCheckNote.onCheckedItem(note, isChecked);
        });
    }

    // ViewHolder
    class NoteViewHolder extends RecyclerView.ViewHolder {

        private final ItemNoteBinding binding;

        // Constructor del ViewHolder
        public NoteViewHolder(@NotNull ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Note note) {

            String title = note.getTitle();
            String content = note.getContent();
            String date = note.getDate();

            binding.tvTitle.setText(title);
            binding.tvContent.setText(content);
            binding.tvDate.setText(date);
        }
    }

    // Callback para detectar un click prolongado en el item
    public interface OnLongClickNote {
        void onLongClickItem(Note note);
    }

    // Callback para detectar cambio en el estado switch
    public interface OnCheckNote {
        void onCheckedItem(Note note, boolean isChecked);
    }
}
