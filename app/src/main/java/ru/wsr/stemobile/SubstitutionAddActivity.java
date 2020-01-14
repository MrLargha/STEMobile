package ru.wsr.stemobile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import ru.wsr.stemobile.databinding.ActivitySubstitutionAddBinding;
import ru.wsr.stemobile.viewmodels.SubstitutionAddViewModel;


public class SubstitutionAddActivity extends AppCompatActivity {

    private ActivitySubstitutionAddBinding mBinding;
    private SubstitutionAddViewModel mViewModel;

    private final String TAG = "ru.mrlargha.stemobile";

    private void setupUIListeners() {
        mBinding.dateInput.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarConstraints.Builder calendarConstraintsBuilder = new CalendarConstraints.Builder();
                calendarConstraintsBuilder.setValidator(new STEDateValidator(Calendar.getInstance().getTimeInMillis()))
                        .setStart(Calendar.getInstance().getTimeInMillis())
                        .setOpenAt(Calendar.getInstance().getTimeInMillis());

                MaterialDatePicker.Builder<Long> datePickerBuilder = MaterialDatePicker.Builder.datePicker();
                datePickerBuilder.setCalendarConstraints(calendarConstraintsBuilder.build())
                        .setTitleText("Выберите дату замещения")
                        .setSelection(Calendar.getInstance().getTimeInMillis());

                MaterialDatePicker<Long> datePicker = datePickerBuilder.build();
                datePicker.show(getSupportFragmentManager(), datePicker.toString());
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat =
                                new SimpleDateFormat("dd.MM.yy");
                        Objects.requireNonNull(mBinding.dateInput.getEditText())
                                .setText(simpleDateFormat.format(selection));
                    }
                });
            }
        });

        Objects.requireNonNull(mBinding.dateInput.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.setDate(mBinding.dateInputEdit.getEditableText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setupObservers() {
        mViewModel.getTeachersList().observe(this, teachersList -> {
            mBinding.substitutingTeacherEdit.setAdapter(new ArrayAdapter<>(this,
                    R.layout.support_simple_spinner_dropdown_item, teachersList));
            Log.d(TAG, "setupObservers: ");
        });

        mViewModel.getSubjectsList().observe(this, subjectsList -> {
            mBinding.substitutableSubjectEdit.setAdapter(new ArrayAdapter<>(this,
                    R.layout.support_simple_spinner_dropdown_item, subjectsList));
        });

        mViewModel.getDateError().observe(this, dateError -> {
            if (dateError.isEmpty()) {
                mBinding.dateInput.setErrorEnabled(false);
            } else {
                mBinding.dateInput.setError(dateError);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySubstitutionAddBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mViewModel = ViewModelProviders.of(this).get(SubstitutionAddViewModel.class);
        mViewModel.init();

        setupUIListeners();
        setupObservers();
    }


}
