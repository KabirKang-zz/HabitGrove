package com.kabirkang.habitgrove.view;

import android.content.Context;
import android.content.res.Resources;

import com.kabirkang.habitgrove.R;
import com.kabirkang.habitgrove.models.HabitRecord;
import com.kabirkang.habitgrove.models.ResetFrequency;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kabirkang on 9/18/17.
 */

public class HabitListItem {

    private HabitRecord mHabitRecord;
    private Context mContext;

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

    public HabitListItem(Context context) {
        this.mContext = context;
    }

    public HabitListItem(Context context, HabitRecord habitRecord) {
        this.mContext = context;
        this.mHabitRecord = habitRecord;
    }

    public void setHabitRecord(HabitRecord habitRecord) {
        this.mHabitRecord = habitRecord;
    }

    public int getBackgroundColor() {
        return mHabitRecord.getColor();
    }

    public String getHabitName() {
        return mHabitRecord.getName();
    }

    public String getResetFreq() {
        Resources resources = mContext.getResources();
        switch (mHabitRecord.getResetFreq()) {
            case ResetFrequency.DAY:
                return resources.getString(R.string.list_item_reset_today);
            case ResetFrequency.WEEK:
                return resetFreqStringWithParameter(ResetFrequency.WEEK);
            case ResetFrequency.MONTH:
                return resetFreqStringWithParameter(ResetFrequency.MONTH);
            case ResetFrequency.YEAR:
                return resetFreqStringWithParameter(ResetFrequency.YEAR);
            case ResetFrequency.NEVER:
                Date date = new Date(mHabitRecord.getCreatedAt());
                return resources.getString(R.string.list_item_reset_never, FORMAT.format(date));
            default:
                throw new IllegalArgumentException("Unsupported reset frequency");
        }
    }

    public String getScore() {
        if (mHabitRecord.getTarget() > 0) {
            return String.valueOf(mHabitRecord.getScore()) + "/"
                    + String.valueOf(mHabitRecord.getTarget());
        } else {
            return String.valueOf(mHabitRecord.getScore());
        }
    }

    private String resetFreqStringWithParameter(String frequency) {
        return mContext.getResources().getString(R.string.list_item_reset_frequency, frequency);
    }

}