package ru.mrlargha.stemobile.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.transition.Slide
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback
import com.google.android.material.snackbar.Snackbar
import ru.mrlargha.stemobile.R
import ru.mrlargha.stemobile.data.model.Substitution
import ru.mrlargha.stemobile.databinding.ActivityMainBinding
import ru.mrlargha.stemobile.ui.substitutionadd.SubstitutionAddActivity
import ru.mrlargha.stemobile.ui.users.UsersActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mViewModel: MainViewModel? = null
    private var mBinding: ActivityMainBinding? = null
    private var mAdapter: SubstitutionAdapter? = null
    private var mActionMode: ActionMode? = null
    private var mSelectionTracker: SelectionTracker<*>? = null

    companion object {
        const val CODE_NORMAL = 0
        const val CODE_LOGOUT = 1
        const val REQUEST_CODE = 100
        private const val TAG = "stemobile"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.exitTransition = Explode()
            window.enterTransition = Slide()
        }
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        setSupportActionBar(mBinding!!.toolbar)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mBinding!!.fab.setOnClickListener {
            val i = Intent(this@MainActivity, SubstitutionAddActivity::class.java)
            startActivity(i)
        }

        mAdapter = SubstitutionAdapter()
        mBinding!!.content.substitutionsRecylcler.layoutManager = LinearLayoutManager(this)
        mBinding!!.content.substitutionsRecylcler.adapter = mAdapter

        val substitutionKeyProvider = SubstitutionKeyProvider(1, mAdapter!!)

        mViewModel!!.substitutionsList.observe(this, Observer { list: List<Substitution>
            ->
            mAdapter!!.setElements(list as ArrayList<Substitution>)
        })

        mSelectionTracker = SelectionTracker.Builder(
                "my-selection-id",
                mBinding!!.content.substitutionsRecylcler,
                substitutionKeyProvider,
                SubstitutionLookup(mBinding!!.content.substitutionsRecylcler),
                StorageStrategy.createLongStorage()
        ).withOnDragInitiatedListener { true }.build()

        if (savedInstanceState != null) {
            mSelectionTracker?.onRestoreInstanceState(savedInstanceState)
        }

        mSelectionTracker?.addObserver(object : SelectionTracker.SelectionObserver<Any?>() {
            override fun onSelectionChanged() {
                if (mSelectionTracker?.hasSelection()!! && mActionMode == null) {
                    Log.d(TAG, "onSelectionChanged: " +
                            (mSelectionTracker?.selection?.size() ?: "NULL"))
                    mActionMode = startSupportActionMode(SubstitutionActionModeCallback(
                            this@MainActivity, mSelectionTracker as SelectionTracker<Long?>))
                    setTheme(R.style.STEToolbarTheme)
                } else if (!mSelectionTracker?.hasSelection()!! && mActionMode != null) {
                    mActionMode!!.finish()
                    mActionMode = null
                    setTheme(R.style.AppTheme)
                }
            }
        })
        mAdapter!!.setSelectionTracker(mSelectionTracker as SelectionTracker<Long>?)
        mViewModel!!.undoString.observe(this, Observer { s: String ->
            if (!s.isEmpty()) {
                Snackbar.make(mBinding!!.root,
                        s, Snackbar.LENGTH_LONG).setAction("Отменить"
                ) { v: View? -> mViewModel!!.undoLocalDeletion() }
                        .addCallback(object : BaseCallback<Snackbar?>() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                mViewModel!!.clearDeletionCache()
                            }
                        }).show()
            }
        })
        mViewModel!!.syncProgress.observe(this, Observer { progress: Int ->
            if (progress == -1) {
                mBinding!!.content.progressBar.visibility = View.GONE
            } else if (progress == 0) {
                mBinding!!.content.progressBar.isIndeterminate = true
                mBinding!!.content.progressBar.visibility = View.VISIBLE
            } else {
                mBinding!!.content.progressBar.isIndeterminate = false
                mBinding!!.content.progressBar.visibility = View.VISIBLE
                mBinding!!.content.progressBar.progress = progress
            }
        })
        mViewModel!!.statusString.observe(this, Observer { status: String? ->
            val snack = Snackbar.make(mBinding!!.root, status!!, Snackbar.LENGTH_SHORT)
            snack.animationMode = Snackbar.ANIMATION_MODE_FADE
            snack.anchorView = mBinding!!.fab
            snack.show()
        })
        mViewModel!!.errorString.observe(this, Observer { error: String? -> Toast.makeText(this, error, Toast.LENGTH_LONG).show() })
    }

    fun deleteSubstitutions(ids: ArrayList<Long?>) {
        mViewModel!!.deleteSubstitutions(ids)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mSelectionTracker!!.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sync -> mViewModel!!.syncSubstitutions(true)
            R.id.logout -> {
                setResult(CODE_LOGOUT)
                finish()
            }
            R.id.users -> {
                startActivity(Intent(this, UsersActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.force_sync -> mViewModel!!.forceSync()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        setResult(CODE_NORMAL)
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}