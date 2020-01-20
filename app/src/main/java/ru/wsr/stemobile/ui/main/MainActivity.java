package ru.wsr.stemobile.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.wsr.stemobile.R;
import ru.wsr.stemobile.data.model.Substitution;
import ru.wsr.stemobile.databinding.ActivityMainBinding;
import ru.wsr.stemobile.databinding.SubstitutionRecViewBinding;
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

        mBinding.content.substitutionsRecylcler.setAdapter(mAdapter);
        mBinding.content.substitutionsRecylcler.setLayoutManager(new LinearLayoutManager(this));

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getSubstitutionsList().observe(this, list -> {
            mAdapter.setSubstitutions(new ArrayList<>(list));
        });
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

    private class SubstitutionAdapter extends RecyclerView.Adapter<SubstitutionAdapter.SubstitutionViewHolder> {

        private ArrayList<Substitution> substitutions;

        @NonNull
        @Override
        public SubstitutionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d("stemobile", "onCreateViewHolder: new created");
            View v = SubstitutionRecViewBinding
                    .inflate(getLayoutInflater(), parent, false).getRoot();
            return new SubstitutionViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SubstitutionViewHolder holder, int position) {
            Substitution substitution = substitutions.get(position);
            holder.binding.pair.setText(String.valueOf(substitution.getPair()));
            holder.binding.cabinet.setText("к. " + substitution.getCabinet());
            holder.binding.group.setText(String.valueOf(substitution.getGroup()));
            holder.binding.subject.setText(substitution.getSubject());
            holder.binding.teacher.setText(substitution.getTeacher());
        }

        @Override
        public int getItemCount() {
            if (substitutions != null) {
                return substitutions.size();
            } else {
                return 0;
            }
        }

        void setSubstitutions(ArrayList<Substitution> substitutions) {
            this.substitutions = substitutions;
            notifyDataSetChanged();
        }

        private class SubstitutionViewHolder extends RecyclerView.ViewHolder {

            SubstitutionRecViewBinding binding;

            SubstitutionViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = SubstitutionRecViewBinding.bind(itemView);
            }
        }
    }
}
