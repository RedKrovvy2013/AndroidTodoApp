package com.todoapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class EditItemDialogFragment extends DialogFragment {

    private EditText mEditText;
    private Spinner spinner;
    private RadioGroup radioGroup;
    private int pos;

    public EditItemDialogFragment() { }

    public static EditItemDialogFragment newInstance(String todoText, String priority, int pos) {
        EditItemDialogFragment frag = new EditItemDialogFragment();
        Bundle args = new Bundle();

        args.putString("todoText", todoText);
        args.putString("priority", priority);
        args.putInt("pos", pos);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_item, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String todoText = getArguments().getString("todoText");
        String priority = getArguments().getString("priority");

        mEditText = (EditText) view.findViewById(R.id.eiEditText);
        radioGroup = (RadioGroup) view.findViewById(R.id.eiRadioGroup);
        pos = getArguments().getInt("pos");

        mEditText.setText(todoText);
        if(priority.equals(getResources().getString(R.string.high)))
            radioGroup.check(R.id.rbHigh);
        else if(priority.equals(getResources().getString(R.string.medium)))
            radioGroup.check(R.id.rbMedium);
        else if(priority.equals(getResources().getString(R.string.low)))
            radioGroup.check(R.id.rbLow);
        else
            radioGroup.check(R.id.rbHigh);

        Button updateBtn = (Button) view.findViewById(R.id.eiUpdateBtn);
        updateBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id= radioGroup.getCheckedRadioButtonId();
                        View radioButton = radioGroup.findViewById(id);
                        int radioId = radioGroup.indexOfChild(radioButton);
                        RadioButton radioBtn = (RadioButton) radioGroup.getChildAt(radioId);

                        EditItemDialogListener listener = (EditItemDialogListener) getActivity();
                        listener.onFinishEditDialog(mEditText.getText().toString(),
                                                    radioBtn.getText().toString(),
                                                    pos);

                        dismiss();
                    }
                }
        );

        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public interface EditItemDialogListener {
        void onFinishEditDialog(String todoText, String priority, int pos);
    }


}