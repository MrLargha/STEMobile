package ru.wsr.stemobile.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import ru.wsr.stemobile.R;
import ru.wsr.stemobile.databinding.ActivityMainBinding;
import ru.wsr.stemobile.ui.substitutionadd.SubstitutionAddActivity;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mViewModel;
    private ActivityMainBinding mBinding;
    private SubstitutionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setSupportActionBar(mBinding.toolbar);

        mBinding.fab.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, SubstitutionAddActivity.class);
            startActivity(i);
        });

        mAdapter = new SubstitutionAdapter();

        mBinding.content.substitutionsRecylcler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.content.substitutionsRecylcler.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getSubstitutionsList().observe(this, list -> {
            mAdapter.setElements(new ArrayList<>(list));
        });

        SelectionTracker selectionTracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                mBinding.content.substitutionsRecylcler,
                new SubstitutionKeyProvider(1, new ArrayList<>()),
                new SubstitutionLookup(mBinding.content.substitutionsRecylcler),
                StorageStrategy.createLongStorage()
        ).withOnDragInitiatedListener(e -> {
            Log.d("stemobile", "DRAG!!!");
            return true;
        }).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
