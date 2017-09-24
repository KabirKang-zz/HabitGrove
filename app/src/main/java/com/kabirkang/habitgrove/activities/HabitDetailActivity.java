package com.kabirkang.habitgrove.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.kabirkang.habitgrove.R;
import com.kabirkang.habitgrove.graphs.GraphConfiguration;
import com.kabirkang.habitgrove.graphs.GraphRange;
import com.kabirkang.habitgrove.models.Habit;
import com.kabirkang.habitgrove.sync.FirebaseSyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HabitDetailActivity extends AppCompatActivity {

    public static final String HABIT_EXTRA_KEY = "com.kabirkang.habitgrove.activities.habit";

    private static final String TAG = "HabitDetailActivity";
    private static final int RC_EDIT_HABIT = 1991;

    @BindView(R.id.bar_chart)
    BarChart barChart;

    @BindView(R.id.tv_score)
    TextView scoreTextView;

    private Habit mHabit;
    private GraphConfiguration mGraphConfiguration;
    private GraphRange.DateRange mGraphRange = GraphRange.DateRange.WEEK;

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

        updateUI();

    }

    private void updateUI() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mHabit.getRecord().getName());
        }
        updateScore();
        mGraphConfiguration.setup(mHabit, mGraphRange);
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

    private void updateScore() {
        String scoreString = String.valueOf(mHabit.getRecord().getScore());
        scoreTextView.setText(scoreString);
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

}