package ru.wsr.stemobile;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

import ru.wsr.stemobile.databinding.ActivitySubstitutionAddBinding;


public class SubstitutionAddActivity extends AppCompatActivity {

    private ActivitySubstitutionAddBinding mBinding;


    private void setupListeners() {
        mBinding.dateInput.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarConstraints.Builder calendarConstraintsBuilder = new CalendarConstraints.Builder();
                calendarConstraintsBuilder.setValidator(new STEDateValidator(Calendar.getInstance().getTimeInMillis()));
                calendarConstraintsBuilder.setStart(Calendar.getInstance().getTimeInMillis());
                calendarConstraintsBuilder.setOpenAt(Calendar.getInstance().getTimeInMillis());

                MaterialDatePicker.Builder<Long> datePickerBuilder = MaterialDatePicker.Builder.datePicker();
                datePickerBuilder.setCalendarConstraints(calendarConstraintsBuilder.build());
                datePickerBuilder.setTitleText("Выберите дату замещения");
                datePickerBuilder.setSelection(Calendar.getInstance().getTimeInMillis());

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

        mBinding.dateInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentInput = mBinding.dateInputEdit.getEditableText().toString();
                if (currentInput.length() > 0)
                    try {
                        if (currentInput.length() != 8) {
                            throw new ParseException("Fuck up!", 0);
                        }
                        @SuppressLint("SimpleDateFormat") Date parsedDate = new SimpleDateFormat("dd.MM.yy").parse(currentInput);
                        if (!new STEDateValidator(Calendar.getInstance().getTimeInMillis()).isValid(parsedDate.getTime())) {
                            mBinding.dateInput.setError("Невозможно установить дату " + currentInput + "!");
                        } else {
                            mBinding.dateInput.setErrorEnabled(false);
                        }
                    } catch (ParseException e) {
                        mBinding.dateInput.setError("Введите дату в формате ДД.ММ.ГГ");
                    } catch (NullPointerException e){
                        mBinding.dateInput.setError("Что за дичь вы сумели ввести?");
                    }
                else
                    mBinding.dateInput.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void validateDateInput(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySubstitutionAddBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setupListeners();
    }


}
