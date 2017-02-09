package com.todoapp.editpage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.todoapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EditDateDialogFragment extends DialogFragment {

    public Calendar cal;
    public Calendar now;
    public TextView timeUntilDate;
    public TextView dateText;
    public SimpleDateFormat dateFormat;

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

        dateText = (TextView) view.findViewById(R.id.edDateText);
        dateFormat = new SimpleDateFormat("EEE, MMM d, hh:mm aaa");
        dateFormat.setTimeZone(cal.getTimeZone());
        dateText.setText(dateFormat.format(cal.getTime()));

        now = Calendar.getInstance();
        timeUntilDate = (TextView) view.findViewById(R.id.edTimeUntilDate);

        //dependent on above members being init'd
        updateDates();

        TimePicker timePicker = (TimePicker) view.findViewById(R.id.edTimePicker);
        timePicker.setHour(cal.get(cal.HOUR_OF_DAY));
        timePicker.setMinute(cal.get(cal.MINUTE));

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    cal.set(Calendar.MINUTE, minute);
                    updateDates();
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
                updateDates();
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
                updateDates();
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
                updateDates();
            }
        });


        Button saveBtn = (Button) view.findViewById(R.id.edSaveBtn);
        saveBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EditDateDialogListener listener = (EditDateDialogListener) getActivity();
                        listener.onFinishEditDialog();

                        dismiss();
                    }
                }
        );
    }

    public void updateDates() {
        updateTimeUntilDate();
        dateText.setText(dateFormat.format(cal.getTime()));
    }

    private void updateTimeUntilDate() {
        Date nowDate = now.getTime();
        Date calDate = cal.getTime();

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

    public interface EditDateDialogListener {
        void onFinishEditDialog();
    }
}
