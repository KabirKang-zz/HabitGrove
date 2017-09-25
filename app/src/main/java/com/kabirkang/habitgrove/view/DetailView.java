package com.kabirkang.habitgrove.view;

import android.support.annotation.NonNull;

import com.kabirkang.habitgrove.graphs.GraphRange;
import com.kabirkang.habitgrove.utils.HabitGroveDateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kabirkang on 9/25/17.
 */

public class DetailView {
    private static final SimpleDateFormat WEEK_FORMAT = new SimpleDateFormat("d MMM yyyy",
            Locale.getDefault());
    private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("d MMM yyyy",
            Locale.getDefault());
    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("MMM yyyy",
            Locale.getDefault());

    private GraphRange.DateRange mDateRange = GraphRange.DateRange.WEEK;

    public DetailView() {
    }

    public DetailView(@NonNull GraphRange.DateRange dateRange) {
        this.mDateRange = dateRange;
    }

    public void setDateRange(GraphRange.DateRange dateRange) {
        this.mDateRange = dateRange;
    }

    public String getScoreString(int score) {
        return String.valueOf(score);
    }

    public String getDateRangeString() {
        switch (mDateRange) {
            case WEEK:
                return getFormattedWeek(HabitGroveDateUtils.getStartOfCurrentWeek(),
                        HabitGroveDateUtils.getEndOfCurrentWeek());
            case MONTH:
                return getFormattedMonth(HabitGroveDateUtils.getStartOfCurrentMonth(),
                        HabitGroveDateUtils.getEndOfCurrentMonth());
            case YEAR:
                return getFormattedYear(HabitGroveDateUtils.getStartOfCurrentYear(),
                        HabitGroveDateUtils.getEndOfCurrentYear());
            default:
                throw new IllegalArgumentException("Receive illegal date range");
        }
    }

    private String getFormattedWeek(long start, long end) {
        return formatDates(WEEK_FORMAT, start, end);
    }

    private String getFormattedMonth(long start, long end) {
        return formatDates(MONTH_FORMAT, start, end);
    }

    private String getFormattedYear(long start, long end) {
        return formatDates(YEAR_FORMAT, start, end);
    }

    private String formatDates(SimpleDateFormat dateFormat, long start, long end) {
        return dateFormat.format(new Date(start))
                + " - "
                + dateFormat.format(new Date(end));
    }
}
