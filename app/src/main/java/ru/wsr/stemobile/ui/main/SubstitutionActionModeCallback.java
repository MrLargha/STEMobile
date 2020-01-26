package ru.wsr.stemobile.ui.main;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.selection.SelectionTracker;

import ru.wsr.stemobile.R;


public class SubstitutionActionModeCallback implements ActionMode.Callback {

    private final Context context;
    private final SelectionTracker selectionTracker;

    SubstitutionActionModeCallback(Context context, SelectionTracker selectionTracker) {
        this.context = context;
        this.selectionTracker = selectionTracker;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.selection_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() != R.id.menu_delete) {
            selectionTracker.clearSelection();
            mode.finish();
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        selectionTracker.clearSelection();
        mode.finish();
    }
}
