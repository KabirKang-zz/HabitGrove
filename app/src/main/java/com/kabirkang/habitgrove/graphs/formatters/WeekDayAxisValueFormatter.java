package com.kabirkang.habitgrove.graphs.formatters;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.kabirkang.habitgrove.utils.HabitGroveDateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.kabirkang.habitgrove.utils.HabitGroveDateUtils.getCalendarWithTime;
import static com.kabirkang.habitgrove.utils.HabitGroveDateUtils.getStartOfCurrentWeek;

/**
 * Created by kabirkang on 9/24/17.
 */

public class WeekDayAxisValueFormatter extends BaseAxisValueFormatter {
    private static SimpleDateFormat FORMATTER = new SimpleDateFormat("EEE", Locale.getDefault());

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return FORMATTER.format(new Date(getDateForValue(value)));
    }

    @Override
    public long getDateForValue(float value) {
        Calendar calendar = getCalendarWithTime(getStartOfCurrentWeek());
        calendar.add(Calendar.DATE, (int) value);
        return calendar.getTimeInMillis();
    }
}
