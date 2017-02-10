package com.todoapp.editpage;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.todoapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateAndTimeUntilDateView extends RelativeLayout {

    private Calendar date;

    public DateAndTimeUntilDateView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.DateAndTimeUntilDateView);
        String dateLabelText = a.getString(R.styleable.DateAndTimeUntilDateView_dateLabel);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_date_and_timeuntildate, this, true);

        TextView dateLabel = (TextView) findViewById(R.id.dateLabel);
        if(dateLabelText != null)
            dateLabel.setText(dateLabelText);
        //else: default in resource xml file stays
    }

    public void setDate(Calendar date) {
        this.date = (Calendar)(date.clone());
        //more comfortable cloning date

        TextView dateText = (TextView) findViewById(R.id.date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, hh:mm aaa");
        dateText.setText(dateFormat.format(date.getTime()));

        updateTimeUntilDate();
    }

    private void updateTimeUntilDate() {
        Date nowDate = Calendar.getInstance().getTime();
        Date calDate = date.getTime();

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

        TextView timeUntilDate = (TextView) findViewById(R.id.timeUntilDate);

        timeUntilDate.setText(
                (duration > 0) ? timeUntilDateStr : "n/a"
        );
    }

}
