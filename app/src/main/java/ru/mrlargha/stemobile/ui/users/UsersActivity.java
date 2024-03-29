package ru.mrlargha.stemobile.ui.users;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.mrlargha.stemobile.R;
import ru.mrlargha.stemobile.data.model.User;
import ru.mrlargha.stemobile.databinding.ActivityUsersBinding;
import ru.mrlargha.stemobile.databinding.UserViewBinding;

public class UsersActivity extends AppCompatActivity {

    private ActivityUsersBinding mBinding;
    private UsersViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setSupportActionBar(mBinding.toolbar);

        mViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        UsersAdapter adapter = new UsersAdapter();

        mBinding.usersRecycler.setAdapter(adapter);
        mBinding.usersRecycler.setLayoutManager(new LinearLayoutManager(this));

        mViewModel.getHasNetworkOperationInProgress()
                .observe(this, inProgress ->
                        mBinding.progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE));

        mViewModel.getUsersLiveData().observe(this, adapter::setData);
        mViewModel.getErrorString().observe(this, error -> {
            if (!error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

        private boolean bindMutex = false;
        private ArrayList<User> users = new ArrayList<>();

        @NonNull
        @Override
        public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = UserViewBinding.inflate(getLayoutInflater(), parent, false).getRoot();
            return new UsersViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
            holder.bind(users.get(position));
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        public void setData(List<User> newUsers) {
            this.users = new ArrayList<>(newUsers);
            notifyDataSetChanged();
        }

        private class UsersViewHolder extends RecyclerView.ViewHolder {

            private UserViewBinding mUserViewBinding;

            UsersViewHolder(@NonNull View itemView) {
                super(itemView);
                mUserViewBinding = UserViewBinding.bind(itemView);
            }

            void bind(User user) {
                bindMutex = false;
                mUserViewBinding.userName.setText(user.getName());
                if (user.getGroup() == -1) {
                    mUserViewBinding.groupText.setText("нет");
                } else {
                    mUserViewBinding.groupText.setText("C" + user.getGroup());
                }
                if (user.getPermissions().equals("admin")) {
                    mUserViewBinding.userToggle.setEnabled(false);
                    mUserViewBinding.moderToggle.setEnabled(false);
                    mUserViewBinding.adminText.setVisibility(View.VISIBLE);
                } else {
                    mUserViewBinding.adminText.setVisibility(View.GONE);
                    if (user.getPermissions().equals("regular")) {
                        mUserViewBinding.pairToggleGroup.check(R.id.userToggle);
                    } else {
                        mUserViewBinding.pairToggleGroup.check(R.id.moderToggle);
                    }
                }
                bindMutex = true;
                mUserViewBinding.pairToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                    if (isChecked && bindMutex) {
                        if (checkedId == R.id.userToggle) {
                            user.setPermissions("regular");
                        } else {
                            user.setPermissions("moderator");
                        }
                        mViewModel.setUserPermission(user);
                    }
                });
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
