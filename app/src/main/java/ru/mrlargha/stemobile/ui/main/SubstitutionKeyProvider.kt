package ru.mrlargha.stemobile.ui.main

import androidx.recyclerview.selection.ItemKeyProvider
import ru.mrlargha.stemobile.data.model.Substitution

class SubstitutionKeyProvider internal constructor(
        scope: Int,
        private val mAdapter: SubstitutionAdapter
) : ItemKeyProvider<Long?>(scope) {
    override fun getKey(position: Int): Long? {
        return (mAdapter.elements!![position] as Substitution).iD.toLong()
    }

    override fun getPosition(key: Long): Int {
        return mAdapter.getPositionByKey(key)
    }

}