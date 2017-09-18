package com.kabirkang.habitgrove.view;

import android.content.Context;
import android.content.res.Resources;

import com.kabirkang.habitgrove.R;
import com.kabirkang.habitgrove.models.Habit;
import com.kabirkang.habitgrove.models.HabitRecord;
import com.kabirkang.habitgrove.models.ResetFrequency;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kabirkang on 9/18/17.
 */

public class HabitListItem {

    private Habit mHabit;
    private Context mContext;

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

    public HabitListItem(Context context) {
        this.mContext = context;
    }

    public HabitListItem(Context context, Habit habit) {
        this.mContext = context;
        this.mHabit = habit;
    }

    public void setHabit(Habit habit) {
        this.mHabit = habit;
    }

    public int getBackgroundColor() {
        return mHabit.getRecord().getColor();
    }

    public String getHabitName() {
        return mHabit.getRecord().getName();
    }

    public String getResetFreq() {
        Resources resources = mContext.getResources();
        switch (mHabit.getRecord().getResetFreq()) {
            case ResetFrequency.DAY:
                return resources.getString(R.string.list_item_reset_today);
            case ResetFrequency.WEEK:
                return resetFreqStringWithParameter(ResetFrequency.WEEK);
            case ResetFrequency.MONTH:
                return resetFreqStringWithParameter(ResetFrequency.MONTH);
            case ResetFrequency.YEAR:
                return resetFreqStringWithParameter(ResetFrequency.YEAR);
            case ResetFrequency.NEVER:
                Date date = new Date(mHabit.getRecord().getCreatedAt());
                return resources.getString(R.string.list_item_reset_never, FORMAT.format(date));
            default:
                throw new IllegalArgumentException("Unsupported reset frequency");
        }
    }

    public String getScore() {
        final int score = mHabit.getRecord().getScore();
        final int target = mHabit.getRecord().getTarget();
        if (target > 0) {
            return String.valueOf(score) + "/"
                    + String.valueOf(target);
        } else {
            return String.valueOf(score);
        }
    }

    private String resetFreqStringWithParameter(String frequency) {
        return mContext.getResources().getString(R.string.list_item_reset_frequency, frequency);
    }

}