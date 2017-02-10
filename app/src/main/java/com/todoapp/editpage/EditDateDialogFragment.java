package com.todoapp.editpage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.todoapp.R;

import java.util.Calendar;

public class EditDateDialogFragment extends DialogFragment {

    private Calendar cal;

    private DateAndTimeUntilDateView datudView;

    public EditDateDialogFragment() {}

    public static EditDateDialogFragment newInstance(Calendar date) {
        EditDateDialogFragment frag = new EditDateDialogFragment();
        Bundle args = new Bundle();

        CalendarParcel dateParcel = new CalendarParcel(date);
        args.putParcelable("date", dateParcel);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_date, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CalendarParcel dateParcel = getArguments().getParcelable("date");
        cal = dateParcel.cal;

        datudView = (DateAndTimeUntilDateView) view.findViewById(R.id.eiDatesView);
        datudView.setDate(cal);


        TimePicker timePicker = (TimePicker) view.findViewById(R.id.edTimePicker);
        timePicker.setHour(cal.get(cal.HOUR_OF_DAY));
        timePicker.setMinute(cal.get(cal.MINUTE));

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    cal.set(Calendar.MINUTE, minute);
                    datudView.setDate(cal);
                }
            }
        );


        final String[] months = getResources().getStringArray(R.array.month_values);

        NumberPicker monthPicker = (NumberPicker) view.findViewById(R.id.edMonthPicker);
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        monthPicker.setDisplayedValues(months);
        monthPicker.setValue(cal.get(cal.MONTH));

        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                cal.set(Calendar.MONTH, newVal);
                datudView.setDate(cal);
            }
        });


        NumberPicker datePicker = (NumberPicker) view.findViewById(R.id.edDatePicker);
        datePicker.setMinValue(1);
        datePicker.setMaxValue(31);  //TODO: max date by month
        datePicker.setValue(cal.get(cal.DATE));

        datePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                cal.set(Calendar.DATE, newVal);
                datudView.setDate(cal);
            }
        });


        NumberPicker yearPicker = (NumberPicker) view.findViewById(R.id.edYearPicker);
        yearPicker.setMinValue(cal.get(cal.YEAR));
        Calendar nextYear = (Calendar)(cal.clone());
        nextYear.add(Calendar.YEAR, 1);
        yearPicker.setMaxValue(nextYear.get(nextYear.YEAR));

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                cal.set(Calendar.YEAR, newVal);
                datudView.setDate(cal);
            }
        });


        Button saveBtn = (Button) view.findViewById(R.id.edSaveBtn);
        saveBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditDateDialogListener listener = (EditDateDialogListener) getActivity();
                        listener.onFinishEditDialog(cal);
                        dismiss();
                    }
                }
        );
    }

    public interface EditDateDialogListener {
        void onFinishEditDialog(Calendar cal);
    }
}
