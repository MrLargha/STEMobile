package ru.wsr.stemobile.ui.substitutionadd;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

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

    private void setupUIListeners() {
        mBinding.dateInput.setEndIconOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = buildDatePicker();
            datePicker.show(getSupportFragmentManager(), datePicker.toString());
            datePicker.addOnPositiveButtonClickListener(selection -> {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("dd.MM.yy");
                Objects.requireNonNull(mBinding.dateInput.getEditText())
                        .setText(simpleDateFormat.format(selection));
            });
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

        mBinding.groupEditEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                mViewModel.setGroup(mBinding.groupEditEdit.getEditableText().toString());
            }
        });

        mBinding.pairEditEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                mViewModel.setPair(Integer.parseInt(mBinding.pairEditEdit.getEditableText().toString()));
            }
        });
    }

    private void setupObservers() {
        mViewModel.getTeachersList().observe(this, teachersList -> {
            mBinding.substitutingTeacherEdit.setAdapter(new ArrayAdapter<>(this,
                                                                           R.layout.support_simple_spinner_dropdown_item, teachersList));
        });

        mViewModel.getSubjectsList().observe(this, subjectsList -> {
            mBinding.substitutableSubjectEdit.setAdapter(new ArrayAdapter<>(this,
                                                                            R.layout.support_simple_spinner_dropdown_item, subjectsList));
        });

        setupErrorObserver(mViewModel.getPairError(), mBinding.pairEdit);
        setupErrorObserver(mViewModel.getCabError(), mBinding.cabEdit);
        setupErrorObserver(mViewModel.getGroupError(), mBinding.groupEdit);
        setupErrorObserver(mViewModel.getDateError(), mBinding.dateInput);


    }

    private void setupErrorObserver(LiveData liveData, TextInputLayout inputLayout) {
        liveData.observe(this, o -> {
            String errorString = o.toString();
            if (errorString.isEmpty()) {
                inputLayout.setErrorEnabled(false);
            } else {
                inputLayout.setError(errorString);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySubstitutionAddBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.toolbar2);

        mViewModel = ViewModelProviders.of(this).get(SubstitutionAddViewModel.class);
        mViewModel.init();

        setupUIListeners();
        setupObservers();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
