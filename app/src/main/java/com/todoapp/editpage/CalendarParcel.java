package com.todoapp.editpage;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CalendarParcel implements Parcelable {
    public Calendar cal;

    public CalendarParcel(Calendar cal) {
        this.cal = cal;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(cal.getTimeInMillis());
        out.writeString(cal.getTimeZone().getID());
    }

    private CalendarParcel(Parcel in) {

        long milliseconds = in.readLong();
        String timezone_id = in.readString();

        cal = new GregorianCalendar(TimeZone.getTimeZone(timezone_id));
        cal.setTimeInMillis(milliseconds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<CalendarParcel> CREATOR
            = new Parcelable.Creator<CalendarParcel>() {

        @Override
        public CalendarParcel createFromParcel(Parcel in) {
            return new CalendarParcel(in);
        }

        @Override
        public CalendarParcel[] newArray(int size) {
            return new CalendarParcel[size];
        }
    };
}
