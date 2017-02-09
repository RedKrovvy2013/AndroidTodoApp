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
import android.widget.TextView;
import android.widget.TimePicker;

import com.todoapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EditItemActivity extends AppCompatActivity
        implements EditDateDialogFragment.EditDateDialogListener {

    private EditText et;
    private RadioGroup radioGroup;
    private TimePicker timePicker;
    private NumberPicker monthPicker;
    private NumberPicker datePicker;
    private NumberPicker yearPicker;

    String todoText;
    String todoPriority;
    private int todoPos;

    private Calendar dueDate;

    public Calendar now;
    public TextView timeUntilDate;
    public TextView dateText;
    public SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        todoText = getIntent().getStringExtra("todoText");
        todoPriority = getIntent().getStringExtra("todoPriority");
        todoPos = getIntent().getIntExtra("todoPos", 0);

        CalendarParcel dueDateParcel = (CalendarParcel) getIntent()
                                                    .getParcelableExtra("todoDueDate");
        dueDate = dueDateParcel.cal;

        et = (EditText) findViewById(R.id.eiEditText);

        radioGroup = (RadioGroup) findViewById(R.id.eiRadioGroup);

        dateText = (TextView) findViewById(R.id.eiDateText);
        dateFormat = new SimpleDateFormat("EEE, MMM d, hh:mm aaa");
        dateFormat.setTimeZone(dueDate.getTimeZone());

        now = Calendar.getInstance();
        timeUntilDate = (TextView) findViewById(R.id.eiTimeUntilDate);

        //dependent upon above inits
        update();

        hideSoftKeyboard();
    }

    private void update() {
        Resources res = getResources();

        et.setText(todoText);
        et.setSelection(et.getText().length());

        if(todoPriority.equals(res.getString(R.string.high)))
            radioGroup.check(R.id.rbHigh);
        else if(todoPriority.equals(res.getString(R.string.medium)))
            radioGroup.check(R.id.rbMedium);
        else if(todoPriority.equals(res.getString(R.string.low)))
            radioGroup.check(R.id.rbLow);
        else
            radioGroup.check(R.id.rbHigh);

        updateDateRepresentations();
    }

    public void onSaveEdit(View v) {

        int id= radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(id);
        int radioId = radioGroup.indexOfChild(radioButton);
        RadioButton radioBtn = (RadioButton) radioGroup.getChildAt(radioId);

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

    public void onFinishEditDialog(Calendar dueDate) {
        this.dueDate = dueDate;
        update();
    }

    public void updateDateRepresentations() {
        updateTimeUntilDate();
        dateText.setText(dateFormat.format(dueDate.getTime()));
    }

    private void updateTimeUntilDate() {
        Date nowDate = now.getTime();
        Date calDate = dueDate.getTime();

        long duration = calDate.getTime() - nowDate.getTime();
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
        long minutesRemainder = diffInMinutes % 60;
        long hoursRemainder = diffInHours % 24;

        String dayStr = (diffInDays == 1L) ? " day, " : " days, ";
        String hourStr = (hoursRemainder == 1L) ? " hour, " : " hours, ";
        String minuteStr = (minutesRemainder == 1L) ? " minute" : " minutes";

        String timeUntilDateStr = new String(
                String.valueOf(diffInDays) + dayStr +
                        String.valueOf(hoursRemainder) + hourStr +
                        String.valueOf(minutesRemainder) + minuteStr
        );

        timeUntilDate.setText(
                (duration > 0) ? timeUntilDateStr : "n/a"
        );
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
