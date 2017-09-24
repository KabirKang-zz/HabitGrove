package com.kabirkang.habitgrove.view;

import com.kabirkang.habitgrove.graphs.GraphRange;
import com.kabirkang.habitgrove.models.Habit;
import com.kabirkang.habitgrove.utils.HabitGroveDateUtils;

public class GraphView {

    private Habit mHabit;
    private GraphRange.DateRange mDateRange;

    public GraphView(Habit habit, GraphRange.DateRange dateRange) {
        this.mHabit = habit;
        this.mDateRange = dateRange;
    }

    public int getXAxisLabelCount() {
        switch (mDateRange) {
            case WEEK:
                return 7;
            case MONTH:
                return HabitGroveDateUtils.getNumberOfWeeksInCurrentMonth();
            case YEAR:
                return 12;
            default:
                throw new IllegalArgumentException("Receive illegal date range");
        }
    }

    public String getBarDataSetName() {
        switch (mDateRange) {
            case WEEK:
                return "This week";
            case MONTH:
                return "This month";
            case YEAR:
                return "This year";
            default:
                throw new IllegalArgumentException("Receive illegal date range");
        }
    }

}