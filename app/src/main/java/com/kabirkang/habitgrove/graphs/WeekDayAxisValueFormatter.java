package com.kabirkang.habitgrove.graphs;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.kabirkang.habitgrove.utils.HabitGroveDateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by kabirkang on 9/24/17.
 */

public class WeekDayAxisValueFormatter implements IAxisValueFormatter {
    public static final int LABEL_COUNT = 7;
    private static SimpleDateFormat FORMATTER = new SimpleDateFormat("EEE", Locale.getDefault());

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(HabitGroveDateUtils.getStartOfThisWeek());
        calendar.add(Calendar.DATE, (int) value);
        return FORMATTER.format(calendar.getTime());
    }
}
