package ru.mrlargha.stemobile.ui.main

import androidx.recyclerview.selection.ItemDetailsLookup.ItemDetails

class SubstitutionItemDetail(val adapterPosition: Int, private val selectionKey: Long) : ItemDetails<Long?>() {
    override fun getPosition(): Int {
        return adapterPosition
    }

    override fun getSelectionKey(): Long? {
        return selectionKey
    }

}