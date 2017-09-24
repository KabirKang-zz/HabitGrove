package com.kabirkang.habitgrove.graphs;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by kabirkang on 9/24/17.
 */

public class GraphAxisValueFormatter implements IAxisValueFormatter {
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.valueOf((int) value);
    }
}
