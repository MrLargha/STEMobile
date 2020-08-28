package ru.mrlargha.stemobile.ui.substitutionadd;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import ru.mrlargha.stemobile.R;
import ru.mrlargha.stemobile.databinding.ActivitySubstitutionAddBinding;
import ru.mrlargha.stemobile.tools.DateFormatter;
import ru.mrlargha.stemobile.tools.STEDateValidator;


public class SubstitutionAddActivity extends AppCompatActivity {

    private ActivitySubstitutionAddBinding mBinding;
    private SubstitutionAddViewModel mViewModel;
    private boolean finishRequired = false;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySubstitutionAddBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.toolbar2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());
            getWindow().setEnterTransition(new Slide());
        }

        mViewModel = new ViewModelProvider(this).get(SubstitutionAddViewModel.class);
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
                if (finishRequired) {
                    super.finish();
                } else {
                    mBinding.cabEditEdit.setText("");
                    mBinding.substitutingSubjectEdit.setText("");
                    mBinding.substitutingTeacherEdit.setText("");
                    if (mBinding.switchMaterial.isChecked()) {
                        int pair = Integer.parseInt(((MaterialButton) findViewById(
                                mBinding.pairToggleGroup.getCheckedButtonId()))
                                .getText().toString());
                        switch (pair) {
                            case 1:
                                mBinding.pairToggleGroup.check(R.id.pair2);
                                break;
                            case 2:
                                mBinding.pairToggleGroup.check(R.id.pair3);
                                break;
                            case 3:
                                mBinding.pairToggleGroup.check(R.id.pair4);
                                break;
                            case 4:
                                mBinding.pairToggleGroup.check(R.id.pair5);
                            case 5:
                                mBinding.pairToggleGroup.clearChecked();
                                break;
                        }
                    } else {
                        mBinding.groupEditEdit.setText("");
                    }
                }
            }
        });

        mViewModel.getTeachersList().observe(this, teachersList -> mBinding.substitutingTeacherEdit.setAdapter(new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, teachersList)));

        mViewModel.getSubjectsList().observe(this, subjectsList -> mBinding.substitutingSubjectEdit.setAdapter(new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, subjectsList)));

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
            }
        });

        mBinding.finishButton.setOnClickListener(v -> {
            finishRequired = true;
            submitSubstitution();
        });

        mBinding.addMoreButton.setOnClickListener(v -> {
            finishRequired = false;
            submitSubstitution();
        });

        mViewModel.getLocalSubstitutionsLiveData().observe(this, list -> mViewModel.setLocalSubstitutions(list));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        mBinding.dateInputEdit.setText(DateFormatter.dateToString(calendar.getTime()));

        mBinding.cabEditEdit.setText("-");
    }

    private void submitSubstitution() {
        mViewModel.submitSubstitution(mBinding.dateInputEdit.getEditableText().toString(),
                ((MaterialButton) findViewById(mBinding.pairToggleGroup.getCheckedButtonId())).getText().toString(),
                mBinding.groupEditEdit.getEditableText().toString(),
                mBinding.cabEditEdit.getEditableText().toString(),
                mBinding.substitutingTeacherEdit.getEditableText().toString(),
                mBinding.substitutingSubjectEdit.getEditableText().toString());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void finish() {
        if (!Objects.requireNonNull(mBinding.substitutingTeacher
                                            .getEditText()).getText().toString().isEmpty() ||
                !Objects.requireNonNull(mBinding.substitutingSubject
                                                .getEditText()).getText().toString().isEmpty()) {
            new MaterialAlertDialogBuilder(this,
                                           R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                    .setTitle("Подтвердите действие")
                    .setMessage("Текущее замещение не будет сохранено. Выйти?")
                    .setPositiveButton("Отменить", (dialog, which) -> dialog.cancel())
                    .setNegativeButton("Выйти", (dialog, which) -> {
                        dialog.cancel();
                        super.finish();
                    }).show();
        } else {
            super.finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
