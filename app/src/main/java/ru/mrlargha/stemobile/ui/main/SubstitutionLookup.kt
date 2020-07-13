package ru.mrlargha.stemobile.ui.main

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import ru.mrlargha.stemobile.ui.main.SubstitutionAdapter.SubstitutionViewHolder

class SubstitutionLookup internal constructor(private val recyclerView: RecyclerView
) : ItemDetailsLookup<Long?>() {
    override fun getItemDetails(e: MotionEvent): SubstitutionItemDetail? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        if (view != null) {
            val viewHolder = recyclerView.getChildViewHolder(view)
            if (viewHolder is SubstitutionViewHolder) {
                return viewHolder.itemDetails
            }
        }
        return null
    }

}