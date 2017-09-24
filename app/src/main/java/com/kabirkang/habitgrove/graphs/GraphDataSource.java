package com.kabirkang.habitgrove.graphs;

import android.support.annotation.NonNull;

import com.github.mikephil.charting.data.BarEntry;
import com.kabirkang.habitgrove.models.Habit;
import com.kabirkang.habitgrove.utils.HabitGroveDateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kabirkang on 9/24/17.
 */

public class GraphDataSource {
    public interface Delegate {
        int numberOfEntries();
    }

    private Habit mHabit;
    private GraphRange.DateRange mDateRange;
    private Delegate mDelegate;
    private int mMaxValue = 0;

    public GraphDataSource(@NonNull Habit habit, @NonNull GraphRange.DateRange dateRange,
                           @NonNull Delegate delegate) {
        this.mHabit = habit;
        this.mDateRange = dateRange;
        this.mDelegate = delegate;
    }

    /**
     * @return Max value within a given range (e.g. max value between days, weeks of months).
     */
    public int getMaxValue() {
        return mMaxValue;
    }

    public List<BarEntry> buildData() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        long baseDate = getBaseDate();
        mMaxValue = 0;

        for (int i = 0; i < mDelegate.numberOfEntries(); i++) {
            long currentDate = getDateForEntryAtIndex(baseDate, i);

            int countInRange = 0;
            for (long checkmarkDate : mHabit.getRecord().getCheckmarks()) {
                if (isMeetCompareRule(currentDate, checkmarkDate)) countInRange++;
            }
            entries.add(new BarEntry(i, countInRange));

            if (countInRange > mMaxValue) mMaxValue = countInRange;
        }

        return entries;
    }

    private long getBaseDate() {
        switch (mDateRange) {
            case WEEK:
                return HabitGroveDateUtils.getStartOfCurrentWeek();
            case MONTH:
                return HabitGroveDateUtils.getStartOfCurrentMonth();
            case YEAR:
                return HabitGroveDateUtils.getStartOfCurrentYear();
            default:
                throw new IllegalArgumentException("Illegal date range");
        }
    }

    private long getDateForEntryAtIndex(long baseDate, int index) {
        Calendar calendar = HabitGroveDateUtils.getCalendarWithTime(baseDate);
        switch (mDateRange) {
            case WEEK:
                calendar.add(Calendar.DATE, index);
                break;
            case MONTH:
                calendar.add(Calendar.DAY_OF_WEEK_IN_MONTH, index);
                break;
            case YEAR:
                calendar.add(Calendar.MONTH, index);
                break;
            default:
                throw new IllegalArgumentException("Illegal date range");
        }
        return calendar.getTimeInMillis();
    }

    private boolean isMeetCompareRule(long currentDate, long checkmarkDate) {
        switch (mDateRange) {
            case WEEK:
                return HabitGroveDateUtils.isSameDay(currentDate, checkmarkDate);
            case MONTH:
                return HabitGroveDateUtils.isDatesInSameWeek(currentDate, checkmarkDate);
            case YEAR:
                return HabitGroveDateUtils.isDatesInSameMonth(currentDate, checkmarkDate);
            default:
                throw new IllegalArgumentException("Illegal date range");
        }
    }
}
