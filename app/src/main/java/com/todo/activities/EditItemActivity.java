package com.todo.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.todo.R;
import com.todo.utils.CalendarParcel;
import com.todo.ui.DateAndTimeUntilDateView;
import com.todo.fragments.EditDateDialogFragment;

import java.util.Calendar;

public class EditItemActivity extends AppCompatActivity
        implements EditDateDialogFragment.EditDateDialogListener {

    private EditText et;
    private RadioGroup radioGroup;

    private String todoText;
    private String todoPriority;
    private int todoPos;

    private Calendar dueDate;
    private DateAndTimeUntilDateView datudView;

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

        datudView = (DateAndTimeUntilDateView) findViewById(R.id.eiDatesView);

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

        datudView.setDate(dueDate);
    }

    public void onSaveEdit(View v) {
        Intent data = new Intent();
        data.putExtra("todoText", et.getText().toString());
        data.putExtra("todoPriority", getSelectedPriorityText());
        data.putExtra("todoPos", todoPos);

        CalendarParcel dueDateParcel = new CalendarParcel(dueDate);
        data.putExtra("todoDueDate", dueDateParcel);

        setResult(RESULT_OK, data);
        finish();
    }

    public void onEditDate(View v) {
        todoText = et.getText().toString();
        todoPriority = getSelectedPriorityText();
        //this avoids bug of saving due date in due date dialog,
        //then onFinishEditDialog updating with old todoText and todoPriority

        FragmentManager fm = getSupportFragmentManager();
        EditDateDialogFragment editDateDialogFragment =
                EditDateDialogFragment.newInstance(dueDate);
        editDateDialogFragment.show(fm, "fragment_edit_date");
    }

    private String getSelectedPriorityText() {
        int id= radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(id);
        int radioId = radioGroup.indexOfChild(radioButton);
        RadioButton radioBtn = (RadioButton) radioGroup.getChildAt(radioId);
        return radioBtn.getText().toString();
    }

    public void onFinishEditDialog(Calendar dueDate) {
        this.dueDate = dueDate;
        update();
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
