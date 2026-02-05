package com.example.notesapp.ui.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.R;
import com.example.notesapp.adapter.NoteAdapter;
import com.example.notesapp.data.Note;
import com.example.notesapp.databinding.AlertDialogNoteBinding;
import com.example.notesapp.databinding.FragmentHomeBinding;
import com.example.notesapp.viewmodel.NoteViewModel;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private NoteAdapter adapter;
    private NoteViewModel viewModel;
    private RecyclerView rvNotes;
    private LinearLayout emptyState;
    private MaterialTextView tvNoResults;
    private FloatingActionButton fabAddNote;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    public void onViewCreated(View view, Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);

        binding = FragmentHomeBinding.bind(view);

        initComponents();
        initInstance();
        settingRecyclerView();
        getAllNotes();
        addNote();
    }

    // Inicializa los elementos de la interfaz
    private void initComponents() {
        rvNotes = binding.rvNotes;
        emptyState = binding.emptyState;
        tvNoResults = binding.tvNoResults;
        fabAddNote = binding.fabAddNote;
    }

    // Inicializa ViewModel y Adapter con Callbacks
    private void initInstance() {
        adapter = new NoteAdapter((note) -> updateNote(note), (note, isChecked) -> isCompleted(note, isChecked));
        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
    }

    // Configura el RecyclerView
    private void settingRecyclerView() {
        rvNotes.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvNotes.setAdapter(adapter);
        swipeToDelete();
    }

    // Permite eliminar una nota deslizando a la derecha
    private void swipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note note = adapter.getCurrentList().get(position);

                viewModel.deleteNote(note);

                // Snackbar con acción de deshacer
                Snackbar snackbar = Snackbar.make(binding.getRoot(), ContextCompat.getString(requireContext(), R.string.message_delete), Snackbar.LENGTH_SHORT)
                        .setAction(ContextCompat.getString(requireContext(), R.string.undo), v -> viewModel.saveNote(note));

                // Personalización de colores de Snackbar
                View snackbarView = snackbar.getView();
                int backgroundColor = MaterialColors.getColor(requireView(), com.google.android.material.R.attr.colorSurface);
                int textColor = MaterialColors.getColor(requireView(), com.google.android.material.R.attr.colorSurface);
                int actionText = ContextCompat.getColor(requireContext(), R.color.info);

                snackbarView.setBackgroundColor(backgroundColor);

                TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                textView.setTextColor(textColor);

                snackbar.setActionTextColor(actionText);
                snackbar.show();
            }

            // Dibuja un fondo rojo y el icono de eliminar al hacer swipe
            @Override
            public void onChildDraw(@NonNull Canvas canvas,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;
                Paint paint = new Paint();

                // Color de fondo
                int color = MaterialColors.getColor(requireView(), com.google.android.material.R.attr.colorErrorContainer);
                paint.setColor(color);

                // Swipe a la derecha
                if (dX > 0) {
                    canvas.drawRect(
                            (float) itemView.getLeft(),
                            (float) itemView.getTop(),
                            dX,
                            (float) itemView.getBottom(),
                            paint
                    );
                }

                // Icono
                Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete);
                icon.setTint(ContextCompat.getColor(requireContext(), R.color.white));
                if (icon != null) {
                    int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + icon.getIntrinsicHeight();

                    if (dX > 0) {
                        int iconLeft = itemView.getLeft() + iconMargin;
                        int iconRight = iconLeft + icon.getIntrinsicWidth();
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    }

                    icon.draw(canvas);
                }

                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvNotes);
    }

    // Observa los cambios de las notas y actualiza la UI
    private void getAllNotes() {
        viewModel.getAllNotes().observe(getViewLifecycleOwner(), notes -> {
            adapter.submitList(notes);
            if (notes.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);
            } else {
                emptyState.setVisibility(View.GONE);
            }
        });
    }

    // Configura el botón flotante para agregar una nueva nota
    private void addNote() {
        fabAddNote.setOnClickListener(v -> {
            alertDialog(requireContext(), ContextCompat.getString(requireContext(), R.string.add_new_note), null);
        });
    }

    // Guardar una nueva nota
    private void saveNote(String title, String content) {

        if (!title.isBlank() && !content.isBlank()) {
            Note note = new Note(title, content);
            viewModel.saveNote(note);
        }
    }

    // Editar una nota existente
    private void updateNote(Note note) {
        alertDialog(requireContext(), ContextCompat.getString(requireContext(), R.string.update_note), note);
        viewModel.updateNote(note);
    }

    // Muestra un AlertDialog para crear o editar una nota
    private void alertDialog(Context context, String title, @Nullable Note note) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);

        AlertDialogNoteBinding binding = AlertDialogNoteBinding.inflate(LayoutInflater.from(context));

        TextInputEditText etTitleNote = binding.etTitleNote;
        TextInputEditText etContentNote = binding.etContentNote;

        if (note != null) {
            etTitleNote.setText(note.getTitle());
            etContentNote.setText(note.getContent());
        }

        AlertDialog dialog = ad
                .setTitle(title)
                .setView(binding.getRoot())
                .setPositiveButton(ContextCompat.getString(context, R.string.save), (d, which) -> {
                }).setNegativeButton(ContextCompat.getString(context, R.string.cancel), (d, which) -> {
                    d.dismiss();
                }).create();

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.alert_dialog_note);

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String titleNote = etTitleNote.getText().toString().trim();
            String contentNote = etContentNote.getText().toString().trim();

            if (titleNote.isEmpty()) {
                etTitleNote.setError(ContextCompat.getString(requireContext(), R.string.field_title_empty));
                etTitleNote.requestFocus();
                return;
            }

            if (contentNote.isEmpty()) {
                etContentNote.setError(ContextCompat.getString(requireContext(), R.string.field_content_empty));
                etContentNote.requestFocus();
                return;
            }

            if (note != null) {
                note.setTitle(titleNote);
                note.setContent(contentNote);
                viewModel.updateNote(note);
            } else {
                saveNote(titleNote, contentNote);
            }

            dialog.dismiss();
        });
    }

    // Actualiza el RecyclerView con los resultados de búsqueda
    public void searchNotes(List<Note> notes) {
        adapter.submitList(notes);
        tvNoResults.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // Cambia el estado completado de una nota y muestra un Toast
    public void isCompleted(Note note, boolean isChecked) {
        viewModel.updateCompleted(note.getId(), isChecked);
        if (isChecked) {
            Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.completed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.pending), Toast.LENGTH_SHORT).show();
        }
    }

    // Libera el recurso de ViewBinding
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}