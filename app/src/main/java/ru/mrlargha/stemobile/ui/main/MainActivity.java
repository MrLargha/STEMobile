package ru.mrlargha.stemobile.ui.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import ru.mrlargha.stemobile.R;
import ru.mrlargha.stemobile.databinding.ActivityMainBinding;
import ru.mrlargha.stemobile.ui.substitutionadd.SubstitutionAddActivity;
import ru.mrlargha.stemobile.ui.users.UsersActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "stemobile";
    private MainViewModel mViewModel;
    private ActivityMainBinding mBinding;
    private SubstitutionAdapter mAdapter;
    private ActionMode mActionMode;
    private SelectionTracker mSelectionTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());
            getWindow().setEnterTransition(new Slide());
        }

        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.toolbar);

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mBinding.fab.setOnClickListener(view -> {
            Bundle options = ActivityOptionsCompat.makeScaleUpAnimation(
                    mBinding.fab, 0, 0, mBinding.fab.getWidth(),
                    mBinding.fab.getHeight()).toBundle();
            Intent i = new Intent(MainActivity.this, SubstitutionAddActivity.class);
            startActivity(i, options);
        });


        mAdapter = new SubstitutionAdapter();
        mBinding.content.substitutionsRecylcler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.content.substitutionsRecylcler.setAdapter(mAdapter);

        SubstitutionKeyProvider substitutionKeyProvider =
                new SubstitutionKeyProvider(1, mAdapter);
        mViewModel.getSubstitutionsList().observe(this, list ->
                mAdapter.setElements(new ArrayList<>(list)));

        mSelectionTracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                mBinding.content.substitutionsRecylcler,
                substitutionKeyProvider,
                new SubstitutionLookup(mBinding.content.substitutionsRecylcler),
                StorageStrategy.createLongStorage()
        ).withOnDragInitiatedListener(e -> true).build();

        if (savedInstanceState != null) {
            mSelectionTracker.onRestoreInstanceState(savedInstanceState);
        }

        mSelectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onSelectionChanged() {
                if (mSelectionTracker.hasSelection() && mActionMode == null) {
                    Log.d(TAG, "onSelectionChanged: " +
                            mSelectionTracker.getSelection().size());
                    mActionMode = startSupportActionMode(new SubstitutionActionModeCallback(
                            MainActivity.this, mSelectionTracker));
                } else if (!mSelectionTracker.hasSelection() && mActionMode != null) {
                    mActionMode.finish();
                    mActionMode = null;
                }
            }
        });

        mAdapter.setSelectionTracker(mSelectionTracker);

        mViewModel.getUndoString().observe(this, s -> {
            if (!s.isEmpty()) {
                Snackbar.make(mBinding.getRoot(),
                              s, Snackbar.LENGTH_LONG).setAction("Отменить",
                                                                 v -> mViewModel.undoLocalDeletion())
                        .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                mViewModel.clearDeletionCache();
                            }
                        }).show();
            }
        });

        mViewModel.getSyncProgress().observe(this, progress -> {
            if (progress == -1) {
                mBinding.content.progressBar.setVisibility(View.GONE);
            } else if (progress == 0) {
                mBinding.content.progressBar.setIndeterminate(true);
                mBinding.content.progressBar.setVisibility(View.VISIBLE);
            } else {
                mBinding.content.progressBar.setIndeterminate(false);
                mBinding.content.progressBar.setVisibility(View.VISIBLE);
                mBinding.content.progressBar.setProgress(progress);
            }
        });
    }

    public void deleteSubstitutions(ArrayList<Long> ids) {
        mViewModel.deleteSubstitutions(ids);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectionTracker.onSaveInstanceState(outState);
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
        } else if (id == R.id.sync) {
            mViewModel.syncSubstitutions();
        } else if (id == R.id.logout) {
            mViewModel.logout();
            // TODO: Set result to Auth required and implement handling of result in login activity
            finish();
        } else if (id == R.id.users) {
            startActivity(new Intent(this, UsersActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
