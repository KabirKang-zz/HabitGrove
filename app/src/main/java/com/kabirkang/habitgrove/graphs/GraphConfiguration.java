package com.kabirkang.habitgrove.graphs;

import android.support.annotation.NonNull;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kabirkang.habitgrove.models.Habit;
import com.kabirkang.habitgrove.utils.HabitGroveDateUtils;
import com.kabirkang.habitgrove.view.GraphView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kabirkang on 9/24/17.
 */

public class GraphConfiguration {
    private static final int MAX_VISIBLE_VALUE_COUNT = 60;

    private BarChart mBarChart;

    private IAxisValueFormatter mXAxisFormatter;

    private Habit mHabit;
    private GraphRange.DateRange mDateRange;
    private GraphView mView;

    public GraphConfiguration(BarChart barChart) {
        this.mBarChart = barChart;
    }

    public void setup(@NonNull final Habit habit, GraphRange.DateRange dateRange) {
        this.mHabit = habit;
        this.mDateRange = dateRange;
        this.mView = new GraphView(habit, dateRange);
        configureBarChart();
        setData();
    }

    private void configureBarChart() {
        mBarChart.setDrawBarShadow(true);
        mBarChart.setDrawValueAboveBar(true);

        mBarChart.getDescription().setEnabled(false);
        mBarChart.setDrawGridBackground(false);

        // If more than 60 entries are displayed in the chart, no values will be drawn.
        mBarChart.setMaxVisibleValueCount(MAX_VISIBLE_VALUE_COUNT);

        // Scaling can now only be done on x- and y-axis separately
        mBarChart.setPinchZoom(false);

        configureXAxis();
        configureLeftAxis();
        configureRightAxis();
        configureLegend();
        configureMarkerView();
    }

    private void configureXAxis() {
        mXAxisFormatter = new WeekDayAxisValueFormatter();

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(mView.getXAxisLabelCount());
        xAxis.setValueFormatter(mXAxisFormatter);
    }

    private void configureLeftAxis() {

        YAxis leftAxis = mBarChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);
    }
    private void configureRightAxis() {

        YAxis rightAxis = mBarChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void configureLegend() {
        Legend legend = mBarChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(9f);
        legend.setTextSize(11f);
        legend.setXEntrySpace(4f);
    }

    private void configureMarkerView() {
        XYMarkerView markerView = new XYMarkerView(mBarChart.getContext(), mXAxisFormatter);
        markerView.setChartView(mBarChart);
        mBarChart.setMarker(markerView);
    }

    private void setData() {
        List<BarEntry> yValues = createData();
        BarDataSet barDataSet;

        if (mBarChart.getData() != null &&
                mBarChart.getData().getDataSetCount() > 0) {
            barDataSet = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
            barDataSet.setValues(yValues);
            mBarChart.getData().notifyDataChanged();
            mBarChart.notifyDataSetChanged();
            mBarChart.invalidate();
        } else {
            barDataSet = new BarDataSet(yValues, mView.getBarDataSetName());
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(barDataSet);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.7f);

            mBarChart.setData(data);
        }
    }

    private List<BarEntry> createData() {
        ArrayList<BarEntry> yValues = new ArrayList<>();
        List<Long> checkmarks = mHabit.getRecord().getCheckmarks();
        Calendar calendar = HabitGroveDateUtils.getCurrentCalendar();
        int maxValueInAllRange = 0;

        switch (mDateRange) {
            case WEEK:
                long thisWeek = HabitGroveDateUtils.getStartOfThisWeek();
                for (int i = 0; i < 7; i++) {
                    calendar.setTimeInMillis(thisWeek);
                    calendar.add(Calendar.DATE, i);
                    long targetDate = calendar.getTimeInMillis();

                    int count = 0;
                    for (long checkmarkDate : checkmarks) {
                        if (HabitGroveDateUtils.sameDay(targetDate, checkmarkDate)) count++;
                    }
                    yValues.add(new BarEntry(i, count));

                    if (count > maxValueInAllRange) maxValueInAllRange = count;
                }
                break;
            case MONTH:
                break;
            case YEAR:
                break;
            default:
                throw new IllegalArgumentException("Receive illegal date range");
        }

        YAxis leftAxis = mBarChart.getAxisLeft();
        leftAxis.setLabelCount((int) Math.floor(maxValueInAllRange / 2) + 1, false);
        return yValues;
    }
}
