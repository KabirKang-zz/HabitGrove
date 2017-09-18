package com.kabirkang.habitgrove.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kabirkang.habitgrove.R;
import com.kabirkang.habitgrove.models.HabitRecord;
import com.kabirkang.habitgrove.models.ReminderTime;
import com.kabirkang.habitgrove.models.ResetFrequency;
import com.kabirkang.habitgrove.pickers.TimePickerFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import petrov.kristiyan.colorpicker.ColorPicker;

public class EditHabitActivity extends AppCompatActivity implements TimePickerFragment.OnTimeSetListener {

    private static final String TAG = "EditHabitActivity";

    private static final int TARGET_FAILED_NUMBER = -1;

    @BindView(R.id.habit_title)
    EditText habitTitleEditText;

    @BindView(R.id.spinner_reset)
    Spinner resetFrequencySpinner;

    @BindView(R.id.et_habit_target)
    EditText targetEditText;

    @BindView(R.id.tv_reminder_time)
    TextView reminderTimeTextView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mHabitsDatabaseReference;
    private int mSelectedColor = Color.WHITE;
    private ResetFrequency mResetFrequency = new ResetFrequency();
    private ReminderTime mReminderTime = new ReminderTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configure();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_habit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                createNew();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void configure() {
        setContentView(R.layout.activity_edit_habit);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.create_activity_label);
        }

        ButterKnife.bind(this);
        initializeFirebase();

        List<String> resetFrequencies = Arrays.asList(ResetFrequency.ALL);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                resetFrequencies);
        resetFrequencySpinner.setAdapter(adapter);
        resetFrequencySpinner.setPrompt(getResources().getString(R.string.spinner_prompt));
        resetFrequencySpinner.setSelection(resetFrequencies.indexOf(mResetFrequency.getTypeName()));
        resetFrequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                mResetFrequency.setType(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initializeFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mHabitsDatabaseReference = mFirebaseDatabase.getReference().child("habits");
    }

    @OnClick(R.id.tv_reminder_time)
    void onDateSpinnerClick() {
        ColorPicker colorPicker = new ColorPicker(this);
        colorPicker.getPositiveButton().setTextColor(getResources().getColor(R.color.colorAccent));
        colorPicker.setRoundColorButton(true);
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                mSelectedColor = color;
                habitTitleEditText.setTextColor(color);
            }

            @Override
            public void onCancel() {
            }
        });
        colorPicker.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mReminderTime.setHour(hourOfDay);
        mReminderTime.setMinutes(minute);
        reminderTimeTextView.setText(mReminderTime.getTimeString());
    }

    private void createNew() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || !isInputCorrect()) return;


        final long now = System.currentTimeMillis();
        final String userId = currentUser.getUid();
        final String name = habitTitleEditText.getText().toString().trim();
        final int score = 0;

        int target = getTargetValue();
        if (target == TARGET_FAILED_NUMBER) return;


        HabitRecord habit = new HabitRecord(userId, now, name, mSelectedColor, target,
                mResetFrequency.getTypeName(), now, mReminderTime.getHour(),
                mReminderTime.getMinutes(), score, null);
        mHabitsDatabaseReference.push().setValue(habit);
        onBackPressed();
    }

    private boolean isInputCorrect() {
        String name = habitTitleEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.empty_title_toast, Toast.LENGTH_SHORT).show();
            return false;
        }

        String targetString = targetEditText.getText().toString().trim();
        if (TextUtils.isEmpty(targetString)) {
            Toast.makeText(this, R.string.empty_target_toast, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private int getTargetValue() {
        String targetString = targetEditText.getText().toString().trim();
        try {
            return Integer.parseInt(targetString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.failed_target_toast, Toast.LENGTH_LONG).show();
            targetEditText.requestFocus();
            return TARGET_FAILED_NUMBER;
        }
    }
}