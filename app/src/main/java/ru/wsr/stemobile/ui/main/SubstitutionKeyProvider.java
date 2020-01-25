package ru.wsr.stemobile.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import java.util.ArrayList;

import ru.wsr.stemobile.data.model.Substitution;

public class SubstitutionKeyProvider extends ItemKeyProvider {

    public void setItemsList(ArrayList<Substitution> itemsList) {
        this.itemsList = itemsList;
    }

    private ArrayList<Substitution> itemsList;

    protected SubstitutionKeyProvider(int scope, ArrayList<Substitution> itemsList) {
        super(scope);
        this.itemsList = itemsList;
    }

    @Nullable
    @Override
    public Object getKey(int position) {
        return itemsList.get(position);
    }

    @Override
    public int getPosition(@NonNull Object key) {
        //noinspection SuspiciousMethodCalls
        return itemsList.indexOf(key);
    }
}
