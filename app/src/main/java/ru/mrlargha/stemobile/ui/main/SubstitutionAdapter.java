package ru.mrlargha.stemobile.ui.main;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.mrlargha.stemobile.data.model.Substitution;
import ru.mrlargha.stemobile.databinding.SubstitutionDateDividerBinding;
import ru.mrlargha.stemobile.databinding.SubstitutionGroupDividerBinding;
import ru.mrlargha.stemobile.databinding.SubstitutionRecViewBinding;
import ru.mrlargha.stemobile.tools.DateFormatter;
import ru.mrlargha.stemobile.tools.SubstitutionsSort;


class SubstitutionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SUBSTITUTION = 1;
    private static final int TYPE_DATE_DIVIDER = 2;
    private static final int TYPE_GROUP_DIVIDER = 3;

    private SelectionTracker<Long> selectionTracker;

    private ArrayList<Object> elements;

    SubstitutionAdapter() {
        setHasStableIds(true);
    }

    ArrayList<Object> getElements() {
        return elements;
    }

    void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_SUBSTITUTION:
                View v = SubstitutionRecViewBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot();
                return new SubstitutionViewHolder(v);
            case TYPE_DATE_DIVIDER:
                View v1 = SubstitutionDateDividerBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false).getRoot();
                return new DateDividerViewHolder(v1);
            case TYPE_GROUP_DIVIDER:
            default:
                View v2 = SubstitutionGroupDividerBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false).getRoot();
                return new GroupDividerViewHolder(v2);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_SUBSTITUTION) {
            Substitution substitution = (Substitution) elements.get(position);
            ((SubstitutionAdapter.SubstitutionViewHolder) holder)
                    .bind(substitution, selectionTracker.isSelected((long) substitution.getID()));
        } else {
            DividerViewHolder groupDividerViewHolder = (DividerViewHolder) holder;
            groupDividerViewHolder.bind(((SubstitutionDivider)
                    elements.get(position)).getDividerText());
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
        if (elements.get(position) instanceof Substitution) {
            return TYPE_SUBSTITUTION;
        } else {
            SubstitutionDivider divider = (SubstitutionDivider) elements.get(position);
            return divider.getDividerType();
        }
    }

    int getPositionByKey(long key) {
        if (elements != null)
            for (Object element : elements) {
                if (element instanceof Substitution) {
                    if (((Substitution) element).getID() == key) {
                        return elements.indexOf(element);
                    }
                }
            }
        return -1;
    }

    void setElements(ArrayList<Substitution> elements) {
        new SortTask().execute(elements);
    }

    static abstract class DividerViewHolder extends RecyclerView.ViewHolder {

        DividerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bind(String dividerText);
    }

    static class GroupDividerViewHolder extends DividerViewHolder {

        SubstitutionGroupDividerBinding binding;

        GroupDividerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SubstitutionGroupDividerBinding.bind(itemView);
        }

        @Override
        void bind(String dividerText) {
            binding.dividerText.setText(dividerText);
        }
    }

    static class DateDividerViewHolder extends DividerViewHolder {

        SubstitutionDateDividerBinding binding;

        DateDividerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SubstitutionDateDividerBinding.bind(itemView);
        }

        @Override
        void bind(String dividerText) {
            binding.date.setText(dividerText);
        }
    }

    private class SortTask extends AsyncTask<ArrayList<Substitution>, ArrayList<Object>, ArrayList<Object>> {

        @Override
        protected ArrayList<Object> doInBackground(ArrayList<Substitution>... arrayLists) {
            ArrayList<Substitution> elements = arrayLists[0];
            ArrayList<Object> resultSet = new ArrayList<>();
            if (!elements.isEmpty()) {
                elements = SubstitutionsSort.moveOldDatesToEnd(elements, new Date());

                Date date = elements.get(0).getSubstitutionDate();
                int group = elements.get(0).getGroup();
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                if (c.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                    resultSet.add(new SubstitutionDivider("Сегодня", TYPE_DATE_DIVIDER));
                } else {
                    resultSet.add(new SubstitutionDivider(DateFormatter.dateToString(date), TYPE_DATE_DIVIDER));
                }
                resultSet.add(new SubstitutionDivider("C" + group, TYPE_GROUP_DIVIDER));
                for (int i = 0; i < elements.size(); i++) {
                    if (elements.get(i).getSubstitutionDate().getDay() != date.getDay()) {
                        date = elements.get(i).getSubstitutionDate();
                        resultSet.add(new SubstitutionDivider(DateFormatter.dateToString(date), TYPE_DATE_DIVIDER));
                    }
                    if (elements.get(i).getGroup() != group) {
                        group = elements.get(i).getGroup();
                        resultSet.add(new SubstitutionDivider("C" + group, TYPE_GROUP_DIVIDER));
                    }
                    resultSet.add(elements.get(i));
                }
            }
            return resultSet;
        }

        @Override
        protected void onPostExecute(ArrayList<Object> substitutions) {
            super.onPostExecute(substitutions);
            SubstitutionAdapter.this.elements = substitutions;
            notifyDataSetChanged();
        }
    }

    class SubstitutionViewHolder extends RecyclerView.ViewHolder {

        SubstitutionRecViewBinding binding;

        SubstitutionViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SubstitutionRecViewBinding.bind(itemView);
        }

        SubstitutionItemDetail getItemDetails() {
            return new SubstitutionItemDetail(getAdapterPosition(),
                    (long) ((Substitution) SubstitutionAdapter.this.elements
                            .get(getAdapterPosition())).getID());
        }

        void bind(Substitution substitution, boolean selected) {
            binding.pair.setText(String.valueOf(substitution.getPair()));
            binding.cabinet.setText("к. " + substitution.getCabinet());
            binding.subject.setText(substitution.getSubject());
            binding.teacher.setText(substitution.getTeacher());
            if (substitution.getStatus().equals(Substitution.STATUS_SYNCHRONIZED)) {
                binding.status.setText("\u2022 Синхронизировано");
                binding.status.setTextColor(Color.rgb(0, 91, 170));
            } else if (substitution.getStatus().equals(Substitution.STATUS_NOT_SYNCHRONIZED)) {
                binding.status.setText("\u2022 Не синхронизировано");
                binding.status.setTextColor(Color.rgb(255, 132, 0));
            } else {
                binding.status.setText("\u2022 Ошибка синхронизации");
                binding.status.setTextColor(Color.RED);
            }
            this.binding.name.setText(substitution.getAuthor());
            itemView.setActivated(selected);
        }
    }
}
