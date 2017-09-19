package com.kabirkang.habitgrove.pickers;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public static final String HOUR_EXTRA_KEY = "hour";
    public static final String MINUTES_EXTRA_KEY = "minutes";

    private static final String TAG = "TimePickerFragment";
    public interface OnTimeSetListener {
        void onTimeSet(TimePicker view, int hourOfDay, int minute);
    }

    private OnTimeSetListener mOnTimeSetListener;

    private int mHour;
    private int mMinutes;

    public static TimePickerFragment newInstance(int hour, int minutes) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();

        Bundle args = new Bundle();
        args.putInt(HOUR_EXTRA_KEY, hour);
        args.putInt(MINUTES_EXTRA_KEY, minutes);
        timePickerFragment.setArguments(args);

        return timePickerFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            this.mHour = args.getInt(HOUR_EXTRA_KEY);
            this.mMinutes = args.getInt(MINUTES_EXTRA_KEY);
        } else {
            final Calendar calendar = Calendar.getInstance();
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinutes = calendar.get(Calendar.MINUTE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), this, mHour, mMinutes,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (mOnTimeSetListener != null) mOnTimeSetListener.onTimeSet(view, hourOfDay, minute);
    }

    public void setOnTimeSetListener(OnTimeSetListener onTimeSetListener) {
        this.mOnTimeSetListener = onTimeSetListener;
    }

}