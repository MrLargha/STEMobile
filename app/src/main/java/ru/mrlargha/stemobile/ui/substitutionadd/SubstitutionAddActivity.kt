package ru.mrlargha.stemobile.ui.substitutionadd

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.transition.Slide
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import ru.mrlargha.stemobile.R
import ru.mrlargha.stemobile.data.model.Substitution
import ru.mrlargha.stemobile.databinding.ActivitySubstitutionAddBinding
import ru.mrlargha.stemobile.tools.DateFormatter
import ru.mrlargha.stemobile.tools.STEDateValidator
import java.text.SimpleDateFormat
import java.util.*

@Suppress("UNNECESSARY_SAFE_CALL")
class SubstitutionAddActivity : AppCompatActivity() {
    private var mBinding: ActivitySubstitutionAddBinding? = null
    private var mViewModel: SubstitutionAddViewModel? = null
    private var finishRequired = false
    private fun buildDatePicker(): MaterialDatePicker<Long> {
        val calendarConstraintsBuilder = CalendarConstraints.Builder()
        calendarConstraintsBuilder.setValidator(STEDateValidator(Calendar.getInstance().timeInMillis))
                .setStart(Calendar.getInstance().timeInMillis)
                .setOpenAt(Calendar.getInstance().timeInMillis)
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
        datePickerBuilder.setCalendarConstraints(calendarConstraintsBuilder.build())
                .setTitleText("Выберите дату замещения")
                .setSelection(Calendar.getInstance().timeInMillis)
        return datePickerBuilder.build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySubstitutionAddBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        setSupportActionBar(mBinding!!.toolbar2)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.exitTransition = Explode()
            window.enterTransition = Slide()
        }
        mViewModel = ViewModelProvider(this).get(SubstitutionAddViewModel::class.java)
        mViewModel!!.formState.observe(this, Observer { formState: SubstitutionAddFormState ->
            mBinding!!.dateInput.error = formState.dateError
            mBinding!!.cabEdit.error = formState.cabinetError
            mBinding!!.groupEdit.error = formState.groupError
            mBinding!!.substitutingTeacher.error = formState.teacherError
            mBinding!!.substitutingSubject.error = formState.subjectError
            if (formState.hasErrors() && formState.customError != null) {
                Snackbar.make(mBinding!!.coordinator, formState.customError!!,
                        Snackbar.LENGTH_SHORT).setTextColor(resources.getColor(R.color.colorAccent)).show()
            } else if (!formState.hasErrors()) {
                Snackbar.make(mBinding!!.coordinator, "Замещение добавлено в локальное хранилище",
                        Snackbar.LENGTH_SHORT).show()
                if (finishRequired) {
                    super.finish()
                } else {
                    mBinding!!.cabEditEdit.setText("")
                    mBinding!!.substitutingSubjectEdit.setText("")
                    mBinding!!.substitutingTeacherEdit.setText("")
                    if (mBinding!!.switchMaterial.isChecked) {
                        val pair = (findViewById<View>(
                                mBinding!!.pairToggleGroup.checkedButtonId) as MaterialButton)
                                .text.toString().toInt()
                        when (pair) {
                            1 -> mBinding!!.pairToggleGroup.check(R.id.pair2)
                            2 -> mBinding!!.pairToggleGroup.check(R.id.pair3)
                            3 -> mBinding!!.pairToggleGroup.check(R.id.pair4)
                            4 -> mBinding!!.pairToggleGroup.clearChecked()
                        }
                    } else {
                        mBinding!!.groupEditEdit.setText("")
                    }
                }
            }
        })
        mViewModel!!.teachersList.observe(this, Observer { teachersList: ArrayList<String>? ->
            mBinding!!.substitutingTeacherEdit.setAdapter(ArrayAdapter(this,
                    R.layout.support_simple_spinner_dropdown_item, teachersList!!))
        })
        mViewModel!!.subjectsList.observe(this, Observer { subjectsList: ArrayList<String>? ->
            mBinding!!.substitutingSubjectEdit.setAdapter(ArrayAdapter(this,
                    R.layout.support_simple_spinner_dropdown_item, subjectsList!!))
        })
        mBinding!!.dateInput.setEndIconOnClickListener { v: View? ->
            val datePicker = buildDatePicker()
            datePicker.show(supportFragmentManager, "ru.mrlargha.stemobile.datepicker")
            datePicker.addOnPositiveButtonClickListener { date: Long? ->
                @SuppressLint("SimpleDateFormat") val simpleDateFormat = SimpleDateFormat("dd.MM.yy")
                mBinding!!.dateInputEdit.setText(simpleDateFormat.format(Date(date!!)))
            }
        }
        mBinding!!.substitutingSubject.setStartIconOnClickListener { v: View? ->
            if (mBinding!!.substitutingSubjectEdit.text.toString().isEmpty()) {
                mBinding!!.substitutingSubjectEdit.editableText.append("МДК ")
            }
        }
        mBinding!!.finishButton.setOnClickListener { v: View? ->
            finishRequired = true
            submitSubstitution()
        }
        mBinding!!.addMoreButton.setOnClickListener { v: View? ->
            finishRequired = false
            submitSubstitution()
        }
        mViewModel!!.localSubstitutionsLiveData.observe(this, Observer { list: List<Substitution?>? -> mViewModel!!.localSubstitutions = list as List<Substitution> })
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        mBinding!!.dateInputEdit.setText(DateFormatter.dateToString(calendar.time))
        mBinding!!.cabEditEdit.setText("-")
    }

    private fun submitSubstitution() {
        mViewModel!!.submitSubstitution(mBinding!!.dateInputEdit.editableText.toString(),
                (findViewById<View>(mBinding!!.pairToggleGroup.checkedButtonId) as MaterialButton).text.toString(),
                mBinding!!.groupEditEdit.editableText.toString(),
                mBinding!!.cabEditEdit.editableText.toString(),
                mBinding!!.substitutingTeacherEdit.editableText.toString(),
                mBinding!!.substitutingSubjectEdit.editableText.toString())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun finish() {
        if (Objects.requireNonNull(mBinding!!.substitutingTeacher
                        .editText)?.text.toString().isNotEmpty() ||
                Objects.requireNonNull(mBinding!!.substitutingSubject
                        .editText)?.text.toString().isNotEmpty()) {
            MaterialAlertDialogBuilder(this,
                    R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                    .setTitle("Подтвердите действие")
                    .setMessage("Текущее замещение не будет сохранено. Выйти?")
                    .setPositiveButton("Отменить") { dialog: DialogInterface, which: Int -> dialog.cancel() }
                    .setNegativeButton("Выйти") { dialog: DialogInterface, which: Int ->
                        dialog.cancel()
                        super.finish()
                    }.show()
        } else {
            super.finish()
        }
    }
}