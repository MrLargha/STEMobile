package ru.wsr.stemobile.ui.substitutionadd;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.wsr.stemobile.R;
import ru.wsr.stemobile.databinding.ActivitySubstitutionAddBinding;
import ru.wsr.stemobile.tools.STEDateValidator;


public class SubstitutionAddActivity extends AppCompatActivity {

    private ActivitySubstitutionAddBinding mBinding;
    private SubstitutionAddViewModel mViewModel;

    private final String TAG = "ru.mrlargha.stemobile";

    private MaterialDatePicker<Long> buildDatePicker() {
        CalendarConstraints.Builder calendarConstraintsBuilder = new CalendarConstraints.Builder();
        calendarConstraintsBuilder.setValidator(new STEDateValidator(Calendar.getInstance().getTimeInMillis()))
                .setStart(Calendar.getInstance().getTimeInMillis())
                .setOpenAt(Calendar.getInstance().getTimeInMillis());

        MaterialDatePicker.Builder<Long> datePickerBuilder = MaterialDatePicker.Builder.datePicker();
        datePickerBuilder.setCalendarConstraints(calendarConstraintsBuilder.build())
                .setTitleText("Выберите дату замещения")
                .setSelection(Calendar.getInstance().getTimeInMillis());
        return datePickerBuilder.build();
    }

    @SuppressLint({"SetTextI18n", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySubstitutionAddBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.toolbar2);

        mViewModel = ViewModelProviders.of(this).get(SubstitutionAddViewModel.class);

        // Setup error updates
        mViewModel.getFormState().observe(this, formState -> {
            mBinding.dateInput.setError(formState.getDateError());
            mBinding.cabEdit.setError(formState.getCabinetError());
            mBinding.groupEdit.setError(formState.getGroupError());
            mBinding.substitutingTeacher.setError(formState.getTeacherError());
            mBinding.substitutingSubject.setError(formState.getSubjectError());
            if (formState.hasErrors() && formState.getCustomError() != null) {
                Snackbar.make(mBinding.coordinator, formState.getCustomError(),
                        Snackbar.LENGTH_SHORT).setTextColor(getResources().getColor(R.color.colorAccent)).show();
            } else if (!formState.hasErrors()) {
                Snackbar.make(mBinding.coordinator, "Замещение добавлено в локальное хранилище",
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        mViewModel.getTeachersList().observe(this, teachersList -> {
            mBinding.substitutingTeacherEdit.setAdapter(new ArrayAdapter<>(this,
                    R.layout.support_simple_spinner_dropdown_item, teachersList));
        });

        mViewModel.getSubjectsList().observe(this, subjectsList -> {
            mBinding.substitutingSubjectEdit.setAdapter(new ArrayAdapter<>(this,
                    R.layout.support_simple_spinner_dropdown_item, subjectsList));
        });

        mBinding.dateInput.setEndIconOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = buildDatePicker();
            datePicker.show(getSupportFragmentManager(), "ru.mrlargha.stemobile.datepicker");
            datePicker.addOnPositiveButtonClickListener(date -> {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat
                        = new SimpleDateFormat("dd.MM.yy");
                mBinding.dateInputEdit.setText(simpleDateFormat.format(new Date(date)));
            });
        });

        mBinding.substitutingSubject.setStartIconOnClickListener(v -> {
            if (mBinding.substitutingSubjectEdit.getText().toString().isEmpty()) {
                mBinding.substitutingSubjectEdit.getEditableText().append("МДК ");
            } else if (!mBinding.substitutingSubjectEdit.getEditableText().toString().equals("МДК ")) {
                mBinding.substitutingSubjectEdit.setText("МДК " +
                        mBinding.substitutingSubjectEdit.getText().toString());
            }
        });

        mBinding.finishButton.setOnClickListener(v -> {
            mViewModel.submitSubstitution(mBinding.dateInputEdit.getEditableText().toString(),
                    String.valueOf(mBinding.pairToggleGroup.getCheckedButtonId()),
                    mBinding.groupEditEdit.getEditableText().toString(),
                    mBinding.cabEditEdit.getEditableText().toString(),
                    mBinding.substitutingTeacherEdit.getEditableText().toString(),
                    mBinding.substitutingSubjectEdit.getEditableText().toString());
        });

        mBinding.pairToggleGroup.check(1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
