package ru.wsr.stemobile.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.wsr.stemobile.data.model.Substitution;
import ru.wsr.stemobile.databinding.SubstitutionDateDividerBinding;
import ru.wsr.stemobile.databinding.SubstitutionRecViewBinding;
import ru.wsr.stemobile.tools.DateFormatter;
import ru.wsr.stemobile.tools.SubstitutionsSort;

class SubstitutionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SUBSTITUTION = 1;
    private static final int TYPE_DIVIDER = 2;

    private ArrayList<Object> elements;

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
            Substitution substitution = (Substitution) elements.get(position);
            substitutionViewHolder.binding.pair.setText(String.valueOf(substitution.getPair()));
            substitutionViewHolder.binding.cabinet.setText("к. " + substitution.getCabinet());
            substitutionViewHolder.binding.group.setText(String.valueOf(substitution.getGroup()));
            substitutionViewHolder.binding.subject.setText(substitution.getSubject());
            substitutionViewHolder.binding.teacher.setText(substitution.getTeacher());
        } else {
            DividerViewHolder dividerViewHolder = (DividerViewHolder) holder;
            Calendar c = Calendar.getInstance();
            c.setTime((Date) elements.get(position));
            if (c.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                dividerViewHolder.binding.date.setText("Сегодня");
            } else {
                dividerViewHolder.binding.date.setText(DateFormatter.dateToString((Date) elements.get(position)));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (elements != null) {
            return elements.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return elements.get(position) instanceof Substitution ? TYPE_SUBSTITUTION : TYPE_DIVIDER;
    }

    void setElements(ArrayList<Substitution> elements) {
        if (!elements.isEmpty()) {
//            SubstitutionsSort.sortSubstitutions(elements);
            elements = SubstitutionsSort.moveOldDatesToEnd(elements, new Date());

            ArrayList<Object> resultSet = new ArrayList<>();

            Date date = elements.get(0).getSubstitutionDate();
            resultSet.add(elements.get(0).getSubstitutionDate());
            for (int i = 0; i < elements.size(); i++) {
                if (!elements.get(i).getSubstitutionDate().equals(date)) {
                    date = elements.get(i).getSubstitutionDate();
                    resultSet.add(date);
                }
                resultSet.add(elements.get(i));
            }

            this.elements = resultSet;
            notifyDataSetChanged();
        }
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

        DividerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SubstitutionDateDividerBinding.bind(itemView);
        }
    }
}
