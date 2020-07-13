package ru.mrlargha.stemobile.ui.users

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import ru.mrlargha.stemobile.R
import ru.mrlargha.stemobile.data.model.User
import ru.mrlargha.stemobile.databinding.ActivityUsersBinding
import ru.mrlargha.stemobile.databinding.UserViewBinding
import ru.mrlargha.stemobile.ui.users.UsersActivity.UsersAdapter.UsersViewHolder
import java.util.*

class UsersActivity : AppCompatActivity() {
    private var mBinding: ActivityUsersBinding? = null
    private var mViewModel: UsersViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        setSupportActionBar(mBinding!!.toolbar)
        mViewModel = ViewModelProvider(this).get(UsersViewModel::class.java)
        val adapter = UsersAdapter()
        mBinding!!.usersRecycler.adapter = adapter
        mBinding!!.usersRecycler.layoutManager = LinearLayoutManager(this)
        mViewModel!!.hasNetworkOperationInProgress
                .observe(this, Observer { inProgress: Boolean -> mBinding!!.progressBar.visibility = if (inProgress) View.VISIBLE else View.GONE })
        mViewModel!!.usersLiveData.observe(this, Observer { newUsers: List<User>? -> adapter.setData(newUsers) })
        mViewModel!!.errorString.observe(this, Observer { error: String ->
            if (!error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private inner class UsersAdapter : RecyclerView.Adapter<UsersViewHolder>() {
        private var bindMutex = false
        private var users = ArrayList<User>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
            val v: View = UserViewBinding.inflate(layoutInflater, parent, false).root
            return UsersViewHolder(v)
        }

        override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
            holder.bind(users[position])
        }

        override fun getItemCount(): Int {
            return users.size
        }

        fun setData(newUsers: List<User>?) {
            users = ArrayList(newUsers!!)
            notifyDataSetChanged()
        }

        private inner class UsersViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val mUserViewBinding: UserViewBinding
            fun bind(user: User) {
                bindMutex = false
                mUserViewBinding.userName.text = user.name
                if (user.group == -1) {
                    mUserViewBinding.groupText.text = "нет"
                } else {
                    mUserViewBinding.groupText.text = "C" + user.group
                }
                if (user.permissions == "admin") {
                    mUserViewBinding.userToggle.isEnabled = false
                    mUserViewBinding.moderToggle.isEnabled = false
                    mUserViewBinding.adminText.visibility = View.VISIBLE
                } else {
                    mUserViewBinding.adminText.visibility = View.GONE
                    if (user.permissions == "regular") {
                        mUserViewBinding.pairToggleGroup.check(R.id.userToggle)
                    } else {
                        mUserViewBinding.pairToggleGroup.check(R.id.moderToggle)
                    }
                }
                bindMutex = true
                mUserViewBinding.pairToggleGroup.addOnButtonCheckedListener { group: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
                    if (isChecked && bindMutex) {
                        if (checkedId == R.id.userToggle) {
                            user.permissions = "regular"
                        } else {
                            user.permissions = "moderator"
                        }
                        mViewModel!!.setUserPermission(user)
                    }
                }
            }

            init {
                mUserViewBinding = UserViewBinding.bind(itemView)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}