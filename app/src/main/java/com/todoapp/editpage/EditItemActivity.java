package com.todoapp.editpage;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.todoapp.R;

import java.util.Calendar;

public class EditItemActivity extends AppCompatActivity
        implements EditDateDialogFragment.EditDateDialogListener {

    private EditText et;
    private RadioGroup radioGroup;
    private TimePicker timePicker;
    private NumberPicker monthPicker;
    private NumberPicker datePicker;
    private NumberPicker yearPicker;
    private int todoPos;

    private Calendar dueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Resources res = getResources();

        String todoText = getIntent().getStringExtra("todoText");
        String todoPriority = getIntent().getStringExtra("todoPriority");
        todoPos = getIntent().getIntExtra("todoPos", 0);

        CalendarParcel dueDateParcel = (CalendarParcel) getIntent()
                                                    .getParcelableExtra("todoDueDate");
        dueDate = dueDateParcel.cal;

        et = (EditText) findViewById(R.id.eiEditText);
        et.setText(todoText);
        et.setSelection(et.getText().length());

        radioGroup = (RadioGroup) findViewById(R.id.eiRadioGroup);

        if(todoPriority.equals(res.getString(R.string.high)))
            radioGroup.check(R.id.rbHigh);
        else if(todoPriority.equals(res.getString(R.string.medium)))
            radioGroup.check(R.id.rbMedium);
        else if(todoPriority.equals(res.getString(R.string.low)))
            radioGroup.check(R.id.rbLow);
        else
            radioGroup.check(R.id.rbHigh);

        final String[] months = res.getStringArray(R.array.month_values);

        monthPicker = (NumberPicker) findViewById(R.id.eiMonthPicker);
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        monthPicker.setDisplayedValues(months);
        monthPicker.setValue(dueDate.get(dueDate.MONTH));

        timePicker = (TimePicker) findViewById(R.id.eiTimePicker);
        timePicker.setHour(dueDate.get(dueDate.HOUR_OF_DAY));
        timePicker.setMinute(dueDate.get(dueDate.MINUTE));

        datePicker = (NumberPicker) findViewById(R.id.eiDatePicker);
        datePicker.setMinValue(1);
        datePicker.setMaxValue(31);  //TODO: max date by month
        datePicker.setValue(dueDate.get(dueDate.DATE));

        yearPicker = (NumberPicker) findViewById(R.id.eiYearPicker);
        yearPicker.setMinValue(dueDate.get(dueDate.YEAR));
        Calendar nextYear = (Calendar)(dueDate.clone());
        nextYear.add(Calendar.YEAR, 1);
        yearPicker.setMaxValue(nextYear.get(nextYear.YEAR));

        hideSoftKeyboard();
    }

    public void onSaveEdit(View v) {

        int id= radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(id);
        int radioId = radioGroup.indexOfChild(radioButton);
        RadioButton radioBtn = (RadioButton) radioGroup.getChildAt(radioId);

        int minute = timePicker.getMinute();
        int hour = timePicker.getHour();
        int date = datePicker.getValue();
        int month = monthPicker.getValue();
        int year = yearPicker.getValue();

        dueDate.set(year, month, date, hour, minute);

        Intent data = new Intent();
        data.putExtra("todoText", et.getText().toString());
        data.putExtra("todoPriority", radioBtn.getText().toString());
        data.putExtra("todoPos", todoPos);

        CalendarParcel dueDateParcel = new CalendarParcel(dueDate);
        data.putExtra("todoDueDate", dueDateParcel);

        setResult(RESULT_OK, data);
        finish();
    }

    public void onEditDate(View v) {
        FragmentManager fm = getSupportFragmentManager();
        EditDateDialogFragment editDateDialogFragment =
                EditDateDialogFragment.newInstance(dueDate);
        editDateDialogFragment.show(fm, "fragment_edit_date");
    }

    public void onFinishEditDialog() {

    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
