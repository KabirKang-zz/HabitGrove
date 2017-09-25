package com.kabirkang.habitgrove.graphs;

import android.support.annotation.NonNull;

import com.github.mikephil.charting.data.BarEntry;
import com.kabirkang.habitgrove.models.Habit;
import com.kabirkang.habitgrove.utils.HabitGroveDateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by kabirkang on 9/24/17.
 */

public class GraphDataSource {

    private Habit mHabit;
    private GraphRange.DateRange mDateRange;
    private List<BarEntry> mEntries;
    private int mMaxValue = 0;

    public GraphDataSource(@NonNull Habit habit,
                                    @NonNull GraphRange.DateRange dateRange) {
        this.mHabit = habit;
        this.mDateRange = dateRange;
    }

    public void prefetch() {
        buildData();
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public List<BarEntry> getData() {
        if (mEntries == null) {
            buildData();
        }
        return mEntries;
    }

    public int getNumberOfEntries() {
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

    private void buildData() {
        mEntries = new ArrayList<>();
        long baseDate = getBaseDate();
        mMaxValue = 0;

        for (int i = 0; i < getNumberOfEntries(); i++) {
            long currentDate = getDateForEntryAtIndex(baseDate, i);

            int countInRange = 0;
            for (long checkmarkDate : mHabit.getRecord().getCheckmarks()) {
                if (isMeetCompareRule(currentDate, checkmarkDate)) countInRange++;
            }
            mEntries.add(new BarEntry(i, countInRange));
            if (countInRange > mMaxValue) mMaxValue = countInRange;
        }
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
                Date endOfMonth = new Date(HabitGroveDateUtils.getEndOfCurrentMonth());
                if (calendar.getTime().after(endOfMonth)) {
                    return endOfMonth.getTime();
                }
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
                return HabitGroveDateUtils.isDatesInSameMonth(currentDate, checkmarkDate)
                        && HabitGroveDateUtils.isDatesInSameWeek(currentDate, checkmarkDate);
            case YEAR:
                return HabitGroveDateUtils.isDatesInSameMonth(currentDate, checkmarkDate);
            default:
                throw new IllegalArgumentException("Illegal date range");
        }
    }
}
