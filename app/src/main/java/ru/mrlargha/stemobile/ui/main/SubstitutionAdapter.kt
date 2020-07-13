package ru.mrlargha.stemobile.ui.main

import android.graphics.Color
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import ru.mrlargha.stemobile.data.model.Substitution
import ru.mrlargha.stemobile.databinding.SubstitutionDateDividerBinding
import ru.mrlargha.stemobile.databinding.SubstitutionGroupDividerBinding
import ru.mrlargha.stemobile.databinding.SubstitutionRecViewBinding
import ru.mrlargha.stemobile.tools.DateFormatter
import ru.mrlargha.stemobile.tools.SubstitutionsSort
import java.util.*

internal class SubstitutionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectionTracker: SelectionTracker<Long>? = null

    companion object {
        private const val TYPE_SUBSTITUTION = 1
        private const val TYPE_DATE_DIVIDER = 2
        private const val TYPE_GROUP_DIVIDER = 3
    }

    init {
        setHasStableIds(true)
    }

    var elements: ArrayList<Any>? = null
        private set

    fun setSelectionTracker(selectionTracker: SelectionTracker<Long>?) {
        this.selectionTracker = selectionTracker
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SUBSTITUTION -> {
                val v: View = SubstitutionRecViewBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false).root
                SubstitutionViewHolder(v)
            }
            TYPE_DATE_DIVIDER -> {
                val v1: View = SubstitutionDateDividerBinding.inflate(LayoutInflater.from(parent.context),
                        parent, false).root
                DateDividerViewHolder(v1)
            }
            TYPE_GROUP_DIVIDER -> {
                val v2: View = SubstitutionGroupDividerBinding.inflate(LayoutInflater.from(parent.context),
                        parent, false).root
                GroupDividerViewHolder(v2)
            }
            else -> {
                val v2: View = SubstitutionGroupDividerBinding.inflate(LayoutInflater.from(parent.context),
                        parent, false).root
                GroupDividerViewHolder(v2)
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_SUBSTITUTION) {
            val substitution = elements!![position] as Substitution
            (holder as SubstitutionViewHolder)
                    .bind(substitution, selectionTracker!!.isSelected(substitution.iD.toLong()))
        } else {
            val groupDividerViewHolder = holder as DividerViewHolder
            groupDividerViewHolder.bind((elements!![position] as SubstitutionDivider).dividerText)
        }
    }

    override fun getItemCount(): Int {
        return if (elements != null) {
            elements!!.size
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (elements!![position] is Substitution) {
            TYPE_SUBSTITUTION
        } else {
            val divider = elements!![position] as SubstitutionDivider
            divider.dividerType
        }
    }

    fun getPositionByKey(key: Long): Int {
        return elements?.indexOf(elements?.find { it is Substitution && it.iD.toLong() == key })
                ?: -1
    }

    fun setElements(elements: ArrayList<Substitution>) {
        SortTask().execute(elements)
    }

    private abstract class DividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(dividerText: String?)
    }

    private class GroupDividerViewHolder(itemView: View) : DividerViewHolder(itemView) {
        private val binding: SubstitutionGroupDividerBinding = SubstitutionGroupDividerBinding.bind(itemView)

        override fun bind(dividerText: String?) {
            binding.dividerText.text = dividerText
        }
    }

    private class DateDividerViewHolder(itemView: View) : DividerViewHolder(itemView) {
        val binding: SubstitutionDateDividerBinding = SubstitutionDateDividerBinding.bind(itemView)

        override fun bind(dividerText: String?) {
            binding.date.text = dividerText
        }
    }

    private inner class SortTask : AsyncTask<ArrayList<Substitution>, ArrayList<Any>, ArrayList<Any>>() {
        override fun doInBackground(vararg params: ArrayList<Substitution>): ArrayList<Any>? {
            var elements = params[0]
            val resultSet = ArrayList<Any>()
            if (elements.isNotEmpty()) {
                elements = SubstitutionsSort.moveOldDatesToEnd(elements, Date())
                var date = elements[0].substitutionDate
                var group = elements[0].group
                val c = Calendar.getInstance()
                c.time = date
                if (c[Calendar.DAY_OF_YEAR] == Calendar.getInstance()[Calendar.DAY_OF_YEAR]) {
                    resultSet.add(SubstitutionDivider("Сегодня", TYPE_DATE_DIVIDER))
                } else {
                    resultSet.add(SubstitutionDivider(DateFormatter.dateToString(date), TYPE_DATE_DIVIDER))
                }
                resultSet.add(SubstitutionDivider("C$group", TYPE_GROUP_DIVIDER))
                for (i in elements.indices) {
                    if (elements[i].substitutionDate.day != date.day) {
                        date = elements[i].substitutionDate
                        resultSet.add(SubstitutionDivider(DateFormatter.dateToString(date), TYPE_DATE_DIVIDER))
                    }
                    if (elements[i].group != group) {
                        group = elements[i].group
                        resultSet.add(SubstitutionDivider("C$group", TYPE_GROUP_DIVIDER))
                    }
                    resultSet.add(elements[i])
                }
            }
            return resultSet
        }

        override fun onPostExecute(substitutions: ArrayList<Any>) {
            super.onPostExecute(substitutions)
            elements = substitutions
            notifyDataSetChanged()
        }
    }

    internal inner class SubstitutionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var binding: SubstitutionRecViewBinding = SubstitutionRecViewBinding.bind(itemView)
        val itemDetails: SubstitutionItemDetail
            get() = SubstitutionItemDetail(adapterPosition,
                    (elements?.get(adapterPosition) as Substitution).iD.toLong())

        fun bind(substitution: Substitution, selected: Boolean) {
            binding.pair.text = substitution.pair.toString()
            binding.cabinet.text = "к. " + substitution.cabinet
            binding.subject.text = substitution.subject
            binding.teacher.text = substitution.teacher
            if (substitution.status == Substitution.STATUS_SYNCHRONIZED) {
                binding.status.text = "\u2022 Синхронизировано"
                binding.status.setTextColor(Color.rgb(0, 91, 170))
            } else if (substitution.status == Substitution.STATUS_NOT_SYNCHRONIZED) {
                binding.status.text = "\u2022 Не синхронизировано"
                binding.status.setTextColor(Color.rgb(255, 132, 0))
            } else {
                binding.status.text = "\u2022 Ошибка синхронизации"
                binding.status.setTextColor(Color.RED)
            }
            binding.name.text = substitution.author
            itemView.isActivated = selected
        }

    }
}