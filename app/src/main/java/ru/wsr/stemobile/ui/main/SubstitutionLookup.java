package ru.wsr.stemobile.ui.main;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;


public class SubstitutionLookup extends ItemDetailsLookup {

    private final RecyclerView recyclerView;

    public SubstitutionLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (viewHolder instanceof SubstitutionAdapter.SubstitutionViewHolder) {
                return ((SubstitutionAdapter.SubstitutionViewHolder) viewHolder).getItemDetails();
            }
        }
        return null;
    }
}
