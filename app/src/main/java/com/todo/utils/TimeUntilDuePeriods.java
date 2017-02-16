package com.todo.utils;

import android.content.Context;

import com.todo.R;

import java.util.ArrayList;

public class TimeUntilDuePeriods {

    private Context context;
    public ArrayList<TimeUntilDuePeriod> periods;

    private static TimeUntilDuePeriods sInstance;

    public static TimeUntilDuePeriods getInstance(Context context) {
        if(sInstance == null) {
            sInstance = new TimeUntilDuePeriods(context);
        }
        return sInstance;
    }

    //NOTE: Processing of time is not exact, and was leading bug like this:
    //      1) add todo due in 4 hours 2) shows up as due in 2-4 hours
    //      So, just did quick fix below by having all duration periods subtract 5000ms

    //NOTE: Also having due date period reports (e.g.-"In 2 to 4 hours") include durations
    //      somewhat below their duration range, for better user experience.
    //      This helps with user seeing due date durations they just set stay in that range
    //      for some time after it is set. Without this, if user sets duration (e.g.-2 hours)
    //      they would see their todo's duration almost immediately go down to
    //      lower due date period report (e.g.-"In 1 to 2 hours").

    private TimeUntilDuePeriods(Context context) {
        this.context = context;
        periods = new ArrayList<TimeUntilDuePeriod>();

        //900000ms - 15min
        periods.add(
            new TimeUntilDuePeriod(1, 3600000-5000-900000, context.getString(R.string.period_0_1_hours))
                //actual: 0:00 to 0:45
        );
        periods.add(
                new TimeUntilDuePeriod(3600001-5000-900000, 7200000-5000-900000,
                        context.getString(R.string.period_1_2_hours))
                //actual: 0:45 to 1:45
        );
        periods.add(
                new TimeUntilDuePeriod(7200001-5000-900000, 14400000-5000-900000,
                        context.getString(R.string.period_2_4_hours))
                //actual: 1:45 to 3:45
        );
        periods.add(
                new TimeUntilDuePeriod(14400001-5000-900000, 28800000-5000-900000,
                        context.getString(R.string.period_4_8_hours))
                //actual: 3:45 to 7:45
        );
        periods.add(
                new TimeUntilDuePeriod(28800001-5000-900000, 86400000-5000-3600000,
                        context.getString(R.string.period_8_24_hours))
                //actual: 7:45 to 23:00
        );
        periods.add(
                new TimeUntilDuePeriod(86400001-5000-3600000, 172800000-5000-3600000,
                        context.getString(R.string.period_1_2_days))
                //actual: 23:00 to 47:00
        );
        periods.add(
                new TimeUntilDuePeriod(172800001-5000-3600000, 345600000-5000-3600000,
                        context.getString(R.string.period_2_4_days))
                //actual: 47:00 to 95:00
        );
        periods.add(
                new TimeUntilDuePeriod(345600001-5000-3600000, 604800000-5000-43200000,
                        context.getString(R.string.period_4_7_days))
                //actual: 95:00 to a week-12:00
        );
        periods.add(
                new TimeUntilDuePeriod(604800001-5000-43200000, 1209600000-5000-86400000,
                        context.getString(R.string.period_1_2_weeks))
                //actual: week-12:00 to 2 weeks - 1 day
        );
        periods.add(
                new TimeUntilDuePeriod(1209600000L-5000L-86400000L, 2592000000L-5000L-172800000L,
                        context.getString(R.string.period_2_weeks_to_month))
                //actual: 2 weeks - 1 day to a month - 2 days
        );
        periods.add(
                new TimeUntilDuePeriod(2592000001L-5000L-172800000L, 63113904000L,
                        context.getString(R.string.period_1_month_to_2_years))
                //actual a month - 2 days to 2 years (user can potentially set todo almost
                //                                    2 years in the future)
        );
    }

    public static class TimeUntilDuePeriod {
        public long min;
        public long max;
        public String message;
        public TimeUntilDuePeriod(long min, long max, String msg) {
            this.min = min;
            this.max = max;
            this.message = msg;
        }
    }
}
