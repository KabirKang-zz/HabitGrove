package com.kabirkang.habitgrove.graphs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.kabirkang.habitgrove.models.Habit;

/**
 * Created by kabirkang on 9/25/17.
 */

public class GraphDataLoader extends AsyncTaskLoader<GraphDataSource> {

    private Habit mHabit;
    private GraphRange.DateRange mDateRange;

    public GraphDataLoader(Context context,
                                    @NonNull Habit habit,
                                    @NonNull GraphRange.DateRange dateRange) {
        super(context);
        this.mHabit = habit;
        this.mDateRange = dateRange;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public GraphDataSource loadInBackground() {
        GraphDataSource dataSource = new GraphDataSource(mHabit, mDateRange);
        dataSource.prefetch();
        return dataSource;
    }

}