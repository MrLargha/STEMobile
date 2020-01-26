package ru.wsr.stemobile.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import ru.wsr.stemobile.data.model.Substitution;

public class SubstitutionKeyProvider extends ItemKeyProvider<Long> {

    private final SubstitutionAdapter mAdapter;

    SubstitutionKeyProvider(int scope, SubstitutionAdapter adapter) {
        super(scope);
        mAdapter = adapter;
    }

    @Nullable
    @Override
    public Long getKey(int position) {
        return (long) ((Substitution) mAdapter.getElements().get(position)).getID();
    }

    @Override
    public int getPosition(@NonNull Long key) {
        return mAdapter.getPositionByKey(key);
    }
}
