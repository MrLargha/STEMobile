package ru.wsr.stemobile.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.wsr.stemobile.data.model.Substitution;
import ru.wsr.stemobile.databinding.SubstitutionDateDividerBinding;
import ru.wsr.stemobile.databinding.SubstitutionRecViewBinding;

class SubstitutionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SUBSTITUTION = 1;
    private static final int TYPE_DIVIDER = 2;

    private ArrayList<Substitution> substitutions;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SUBSTITUTION) {
            View v = SubstitutionRecViewBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot();
            return new SubstitutionViewHolder(v);
        } else {
            View v = SubstitutionDateDividerBinding.inflate(LayoutInflater.from(parent.getContext()),
                                                            parent, false).getRoot();
            return new DividerViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_SUBSTITUTION) {
            SubstitutionViewHolder substitutionViewHolder = (SubstitutionViewHolder) holder;
            Substitution substitution = substitutions.get(position);
            substitutionViewHolder.binding.pair.setText(String.valueOf(substitution.getPair()));
            substitutionViewHolder.binding.cabinet.setText("к. " + substitution.getCabinet());
            substitutionViewHolder.binding.group.setText(String.valueOf(substitution.getGroup()));
            substitutionViewHolder.binding.subject.setText(substitution.getSubject());
            substitutionViewHolder.binding.teacher.setText(substitution.getTeacher());
        } else {
            DividerViewHolder dividerViewHolder = (DividerViewHolder) holder;
        }
    }

    @Override
    public int getItemCount() {
        if (substitutions != null) {
            return substitutions.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    void setSubstitutions(ArrayList<Substitution> substitutions) {
        this.substitutions = substitutions;
        notifyDataSetChanged();
    }

    static class SubstitutionViewHolder extends RecyclerView.ViewHolder {

        SubstitutionRecViewBinding binding;

        SubstitutionViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SubstitutionRecViewBinding.bind(itemView);
        }
    }

    static class DividerViewHolder extends RecyclerView.ViewHolder {

        SubstitutionDateDividerBinding binding;

        public DividerViewHolder(@NonNull View itemView) {
            super(itemView);
            SubstitutionDateDividerBinding.bind(itemView);
        }
    }
}
