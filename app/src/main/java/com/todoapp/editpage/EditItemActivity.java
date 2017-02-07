package com.todoapp.editpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.todoapp.R;

public class EditItemActivity extends AppCompatActivity {

    EditText et;
    private RadioGroup radioGroup;
    private int todoPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        String todoText = getIntent().getStringExtra("todoText");
        String todoPriority = getIntent().getStringExtra("todoPriority");
        todoPos = getIntent().getIntExtra("todoPos", 0);

        et = (EditText) findViewById(R.id.eiEditText);
        et.setText(todoText);
        et.setSelection(et.getText().length());

        radioGroup = (RadioGroup) findViewById(R.id.eiRadioGroup);

        if(todoPriority.equals(getResources().getString(R.string.high)))
            radioGroup.check(R.id.rbHigh);
        else if(todoPriority.equals(getResources().getString(R.string.medium)))
            radioGroup.check(R.id.rbMedium);
        else if(todoPriority.equals(getResources().getString(R.string.low)))
            radioGroup.check(R.id.rbLow);
        else
            radioGroup.check(R.id.rbHigh);
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

        setResult(RESULT_OK, data);
        finish();
    }
}
