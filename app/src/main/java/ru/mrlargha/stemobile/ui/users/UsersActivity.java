package ru.mrlargha.stemobile.ui.users;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

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

        mViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        UsersAdapter adapter = new UsersAdapter();

        mBinding.usersRecycler.setAdapter(adapter);
        mBinding.usersRecycler.setLayoutManager(new LinearLayoutManager(this));

        mViewModel.getHasNetworkOperationInProgress().observe(this, inProgress -> {
            mBinding.progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        });

        mViewModel.getUsersLiveData().observe(this, adapter::setData);
    }

    private class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

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
                mUserViewBinding.userName.setText(user.getName());
                mUserViewBinding.groupText.setText("C" + user.getGroup());
                mUserViewBinding.isModeratorSwitch.setChecked(!user.getPermissions().equals("regular"));
                mUserViewBinding.isModeratorSwitch.setEnabled(!user.getPermissions().equals("admin"));
                mUserViewBinding.isModeratorSwitch.setText(user.getPermissions());
                mUserViewBinding.isModeratorSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    user.setPermissions(isChecked ? "moderator" : "regular");
                    mUserViewBinding.isModeratorSwitch.setText(user.getPermissions());
                    mViewModel.setUserPermission(user);
                });
            }
        }
    }
}