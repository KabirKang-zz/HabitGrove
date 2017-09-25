package com.kabirkang.habitgrove.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.kabirkang.habitgrove.R;
import com.kabirkang.habitgrove.graphs.GraphConfiguration;
import com.kabirkang.habitgrove.graphs.GraphDataLoader;
import com.kabirkang.habitgrove.graphs.GraphDataSource;
import com.kabirkang.habitgrove.graphs.GraphRange;
import com.kabirkang.habitgrove.models.Habit;
import com.kabirkang.habitgrove.sync.FirebaseSyncUtils;
import com.kabirkang.habitgrove.view.DetailView;
import com.kabirkang.habitgrove.view.GraphView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HabitDetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<GraphDataSource> {

    public static final String HABIT_EXTRA_KEY = "com.kabirkang.habitgrove.activities.habit";

    private static final String TAG = "HabitDetailActivity";
    private static final int GRAPH_DATA_SOURCE_LOADER = 1;
    private static final int RC_EDIT_HABIT = 1991;

    @BindView(R.id.bar_chart)
    BarChart barChart;

    @BindView(R.id.tv_score)
    TextView scoreTextView;

    @BindView(R.id.sp_date_range)
    Spinner dateRangeSpinner;

    @BindView(R.id.tv_date_range)
    TextView dateRangeTextView;


    private Habit mHabit;
    private GraphConfiguration mGraphConfiguration;
    private GraphRange.DateRange mGraphRange = GraphRange.DateRange.WEEK;

    private DetailView mView = new DetailView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configure();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.habit_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_edit:
                editHabit();
                return true;
            case R.id.action_delete:
                delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_EDIT_HABIT && resultCode == RESULT_OK) {
            mHabit = data.getParcelableExtra(EditHabitActivity.EDIT_HABIT_RESULT);
            updateUI();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void configure() {
        setContentView(R.layout.activity_habit_detail);
        ButterKnife.bind(this);
        mGraphConfiguration = new GraphConfiguration(barChart);

        getHabit();
        configureDateSpinner();
        updateUI();

    }

    private void updateUI() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mHabit.getRecord().getName());
        }
        String scoreString = mView.getScoreString(mHabit.getRecord().getScore());
        scoreTextView.setText(scoreString);

        dateRangeTextView.setText(mView.getDateRangeString());
        getSupportLoaderManager().restartLoader(GRAPH_DATA_SOURCE_LOADER, null, this);
    }

    private void getHabit() {
        Intent intent = getIntent();
        if (intent.hasExtra(HABIT_EXTRA_KEY)) {
            mHabit = intent.getParcelableExtra(HABIT_EXTRA_KEY);
        } else {
            throw new IllegalArgumentException("Put habit in the intent extras to be able to see details");
        }
    }

    private void editHabit() {
        Intent intent = new Intent(this, EditHabitActivity.class);
        intent.putExtra(EditHabitActivity.EDIT_HABIT_EXTRA_KEY, mHabit);
        startActivityForResult(intent, RC_EDIT_HABIT);
    }

    @OnClick(R.id.bt_increase)
    void onIncreaseScoreClick() {
        final int oldScore = mHabit.getRecord().getScore();
        mHabit.increaseScore();
        updateScoreIfNeeded(oldScore);
    }

    @OnClick(R.id.bt_decrease)
    void onDecreaseClick() {
        final int oldScore = mHabit.getRecord().getScore();
        mHabit.decreaseScore();
        updateScoreIfNeeded(oldScore);
    }

    private void updateScoreIfNeeded(int oldValue) {
        if (oldValue != mHabit.getRecord().getScore()) {
            updateUI();
            FirebaseSyncUtils.applyChangesForHabit(mHabit);
        }
    }

    private void delete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_button)
                .setMessage(R.string.delete_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseSyncUtils.deleteHabit(mHabit);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void configureDateSpinner() {
        List<String> dateRanges = GraphRange.allStringValues(this);
        ArrayAdapter<String> resetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                dateRanges);
        dateRangeSpinner.setAdapter(resetAdapter);
        dateRangeSpinner.setSelection(dateRanges.indexOf(mGraphRange.stringValue(this)));
        dateRangeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();
        if (!selected.equals(mGraphRange.stringValue(this))) {
            mGraphRange = GraphRange.DateRange.fromString(selected, this);
            mView.setDateRange(mGraphRange);
            updateUI();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public Loader<GraphDataSource> onCreateLoader(int id, Bundle args) {
        return new GraphDataLoader(this, mHabit, mGraphRange);
    }

    @Override
    public void onLoadFinished(Loader<GraphDataSource> loader,
                               GraphDataSource dataSource) {
        GraphView view = new GraphView(mHabit, mGraphRange);
        mGraphConfiguration.setup(dataSource, view);
        barChart.animateY(1000);
    }

    @Override
    public void onLoaderReset(Loader<GraphDataSource> loader) {
        barChart.clear();
    }

}