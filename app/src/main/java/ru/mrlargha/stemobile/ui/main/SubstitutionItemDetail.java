package ru.mrlargha.stemobile.ui.main;

import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;

public class SubstitutionItemDetail extends ItemDetailsLookup.ItemDetails<Long> {

    private final int adapterPosition;
    private final Long selectionKey;

    public SubstitutionItemDetail(int adapterPosition, Long selectionKey) {
        this.adapterPosition = adapterPosition;
        this.selectionKey = selectionKey;
    }

    @Override
    public int getPosition() {
        return adapterPosition;
    }

    @Nullable
    @Override
    public Long getSelectionKey() {
        return selectionKey;
    }
}
