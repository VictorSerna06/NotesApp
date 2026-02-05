package com.example.notesapp.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import com.example.notesapp.R;
import com.example.notesapp.databinding.ActivityMainBinding;
import com.example.notesapp.ui.fragment.HomeFragment;
import com.example.notesapp.viewmodel.NoteViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.color.MaterialColors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NoteViewModel viewModel;
    private MaterialToolbar toolbar;
    private FragmentContainerView fragmentContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // SplashScreen
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initComponents();

        // Carga el fragment inicial
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        settingToolbar();

    }

    // Inicializa los componentes del layout
    private void initComponents() {
        toolbar = binding.toolbar;
        fragmentContainerView = binding.fragmentContainerView;

        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
    }

    // Reemplaza el fragment actual por el recibido
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(fragmentContainerView.getId(), fragment)
                .commit();
    }

    // Configura la Toolbar
    private void settingToolbar() {
        // Color del icono del menu
        MenuItem itemMenu = toolbar.getMenu().getItem(0);
        itemMenu.getIcon().setTint(
                MaterialColors.getColor(
                        toolbar,
                        com.google.android.material.R.attr.colorOnPrimary
                )
        );

        // Listener de clics del menu
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                settingIconSearch();
                return true;
            }
            return false;
        });

    }

    // Configura la apariencia y funcionalidad del SearchView
    private void settingIconSearch() {
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        int color = MaterialColors.getColor(binding.getRoot(), com.google.android.material.R.attr.colorOnPrimary);

        searchEditText.setTextColor(color);
        searchEditText.setHintTextColor(color);

        searchEditText.setHint(ContextCompat.getString(this, R.string.search_note));

        ImageView closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);

        if (closeButton != null) {
            closeButton.setColorFilter(color);
        }

        // Filtrado en tiempo real
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isBlank()) {
                    viewModel.getAllNotes().observe(MainActivity.this, notes -> {
                        HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
                        if (fragment != null) {
                            fragment.searchNotes(notes);
                        }
                    });
                } else {
                    viewModel.getNoteByTitle(newText).observe(MainActivity.this, notes -> {
                        HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
                        if (fragment != null) {
                            fragment.searchNotes(notes);
                        }
                    });
                }
                return false;
            }
        });
    }
}