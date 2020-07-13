package ru.mrlargha.stemobile.ui.main

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker
import ru.mrlargha.stemobile.R
import java.util.*

class SubstitutionActionModeCallback internal constructor(private val context: MainActivity, private val selectionTracker: SelectionTracker<*>) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.selection_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        if (item.itemId != R.id.menu_delete) {
            selectionTracker.clearSelection()
        } else {
            val savedSubstitutions = ArrayList<Long?>()
            for (uid in selectionTracker.selection) {
                savedSubstitutions.add(uid as Long)
            }
            context.deleteSubstitutions(savedSubstitutions)
        }
        mode.finish()
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        selectionTracker.clearSelection()
        mode.finish()
    }

}