package com.kabirkang.habitgrove.graphs.formatters;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by kabirkang on 9/24/17.
 */

public abstract class BaseAxisValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.valueOf(value);
    }

    public abstract long getDateForValue(float value);

}