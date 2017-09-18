package com.kabirkang.habitgrove.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.kabirkang.habitgrove.R;
import com.kabirkang.habitgrove.models.Habit;

import butterknife.ButterKnife;

public class HabitDetailActivity extends AppCompatActivity {

    public static final String HABIT_EXTRA_KEY = "com.kabirkang.habitgrove.activities.habit";

    private static final String TAG = "DetailHabitActivity";

    private Habit mHabit;

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void configure() {
        setContentView(R.layout.activity_habit_detail);
        ButterKnife.bind(this);

        getHabit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mHabit.getRecord().getName());
        }
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
        startActivity(new Intent(this, EditHabitActivity.class));
    }

}