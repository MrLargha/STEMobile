package ru.wsr.stemobile.ui.main;

import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;

import ru.wsr.stemobile.data.model.Substitution;

public class SubstitutionItemDetail extends ItemDetailsLookup.ItemDetails {

    private final int adapterPosition;
    private final Substitution selectionKey;

    public SubstitutionItemDetail(int adapterPosition, Substitution selectionKey) {
        this.adapterPosition = adapterPosition;
        this.selectionKey = selectionKey;
    }

    @Override
    public int getPosition() {
        return adapterPosition;
    }

    @Nullable
    @Override
    public Object getSelectionKey() {
        return selectionKey;
    }
}
