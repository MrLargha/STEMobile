package ru.mrlargha.stemobile.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKScope;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;

import ru.mrlargha.stemobile.R;
import ru.mrlargha.stemobile.databinding.ActivityMainBinding;
import ru.mrlargha.stemobile.ui.substitutionadd.SubstitutionAddActivity;

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

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
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
        ).withOnDragInitiatedListener(e -> {
            Log.d("stemobile", "DRAG!!!");
            return true;
        }).build();

        if (savedInstanceState != null) {
            mSelectionTracker.onRestoreInstanceState(savedInstanceState);
        }

        mSelectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onSelectionChanged() {
                if (mSelectionTracker.hasSelection() && mActionMode == null) {
                    Log.d("stemobile", "onSelectionChanged: " +
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

        mViewModel.getVkAuthorizationRequired().observe(this, authorizationRequired -> {
            if (authorizationRequired) {
                Log.d(TAG, "onCreate: starting authorization threw VK");
                LinkedList<VKScope> scopes = new LinkedList<>();
                scopes.add(VKScope.OFFLINE);
                VK.login(this, scopes);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VK.onActivityResult(requestCode, resultCode, data, new VKAuthCallback() {
            @Override
            public void onLoginFailed(int i) {

            }

            @Override
            public void onLogin(@NotNull VKAccessToken vkAccessToken) {
                Log.d(TAG, "onLogin: token=" + vkAccessToken.getAccessToken());
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
