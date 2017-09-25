package com.kabirkang.habitgrove.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.kabirkang.habitgrove.R;
import com.kabirkang.habitgrove.graphs.GraphRange;
import com.kabirkang.habitgrove.graphs.formatters.BaseAxisValueFormatter;
import com.kabirkang.habitgrove.graphs.formatters.MonthAxisValueFormatter;
import com.kabirkang.habitgrove.graphs.formatters.WeekDayAxisValueFormatter;
import com.kabirkang.habitgrove.graphs.formatters.YearAxisValueFormatter;
import com.kabirkang.habitgrove.models.Habit;
import com.kabirkang.habitgrove.utils.HabitGroveDateUtils;
import com.kabirkang.habitgrove.utils.HabitGroveStringUtils;

public class GraphView {

    private Habit mHabit;
    private GraphRange.DateRange mDateRange;

    public GraphView(Habit habit, GraphRange.DateRange dateRange) {
        this.mHabit = habit;
        this.mDateRange = dateRange;
    }

    public BaseAxisValueFormatter getXAxisFormatter() {
        switch (mDateRange) {
            case WEEK:
                return new WeekDayAxisValueFormatter();
            case MONTH:
                return new MonthAxisValueFormatter();
            case YEAR:
                return new YearAxisValueFormatter();
            default:
                throw new IllegalArgumentException("Invalid date range");
        }
    }

    public String getBarDataSetName(@NonNull final Context context) {
        Resources resources = context.getResources();
        switch (mDateRange) {
            case WEEK:
                String week = resources.getString(R.string.week).toLowerCase();
                return HabitGroveStringUtils.capitalized(
                        resources.getString(R.string.bar_chart_set_name, week));
            case MONTH:
                String month = resources.getString(R.string.month).toLowerCase();
                return HabitGroveStringUtils.capitalized(
                        resources.getString(R.string.bar_chart_set_name, month));
            case YEAR:
                String year = resources.getString(R.string.year).toLowerCase();
                return HabitGroveStringUtils.capitalized(
                        resources.getString(R.string.bar_chart_set_name, year));
            default:
                throw new IllegalArgumentException("Receive illegal date range");
        }
    }
}