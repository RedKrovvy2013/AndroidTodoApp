package com.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    EditText et;
    private int itemPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        String itemText = getIntent().getStringExtra("itemText");
        itemPos = getIntent().getIntExtra("itemPos", 0);

        et = (EditText) findViewById(R.id.eiEditText);
        et.setText(itemText);
        et.setSelection(et.getText().length());

    }

    public void onSaveEdit(View v) {
        EditText et = (EditText) findViewById(R.id.eiEditText);

        Intent data = new Intent();
        data.putExtra("itemText", et.getText().toString());
        data.putExtra("itemPos", itemPos);

        setResult(RESULT_OK, data);
        finish();
    }
}
