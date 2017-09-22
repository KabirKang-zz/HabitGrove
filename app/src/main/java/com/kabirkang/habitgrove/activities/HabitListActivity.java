package com.kabirkang.habitgrove.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kabirkang.habitgrove.models.Habit;
import com.kabirkang.habitgrove.models.HabitRecord;
import com.kabirkang.habitgrove.sync.FirebaseSyncUtils;
import com.kabirkang.habitgrove.utils.ReminderUtils;
import com.kabirkang.habitgrove.view.GridSpacing;
import com.kabirkang.habitgrove.adapters.HabitsAdapter;
import com.kabirkang.habitgrove.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HabitListActivity extends AppCompatActivity implements HabitsAdapter.HabitAdapterOnClickListener {

    private static final String TAG = "HabitListActivity";

    private static final int NUM_OF_COLUMNS = 2;
    private static final int SPACE_BETWEEN_ITEMS = 50;

    private static final int RC_SIGN_IN = 10;

    @BindView(R.id.recycler_habits)
    RecyclerView mRecyclerView;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private HabitsAdapter mHabitsAdapter;

    private Query mUserHabitsQuery;
    private ValueEventListener mValueEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configure();
    }

    private void configure() {
        setContentView(R.layout.activity_habit_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        initializeFirebase();

        mHabitsAdapter = new HabitsAdapter();
        mHabitsAdapter.setClickListener(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, NUM_OF_COLUMNS));
        mRecyclerView.addItemDecoration(new GridSpacing(NUM_OF_COLUMNS,
                SPACE_BETWEEN_ITEMS, true));
        mRecyclerView.setAdapter(mHabitsAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && floatingActionButton.isShown()) {
                    floatingActionButton.hide();
                } else if (dy < 0 && !floatingActionButton.isShown()) {
                    floatingActionButton.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(findViewById(R.id.activity_habit_list), R.string.sign_in_success,
                        Snackbar.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.sign_in_canceled, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDBReadListener();
        mHabitsAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.habit_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_button:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(Habit habit, int position) {
        showDetail(habit);
    }

    @OnClick(R.id.fab)
    void onAddClick() {
        createHabit();
    }

    private void initializeFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    onSignedInInitialize();
                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
                                    ).build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void signOut() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.sign_out)
                .setMessage(R.string.sign_out_prompt)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AuthUI.getInstance().signOut(HabitListActivity.this);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void onSignedInInitialize() {
        detachDBReadListener();
        mUserHabitsQuery = FirebaseSyncUtils.getCurrentUserHabitsQuery();
        attachDatabaseReadListener();
        // Keep Synced
    }

    private void onSignedOutCleanup() {
        mHabitsAdapter.clear();
        detachDBReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mValueEventListener != null) return;

        showProgressIndicator();

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Habit> habits = new ArrayList<>((int) dataSnapshot.getChildrenCount());

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    HabitRecord parsedRecord = data.getValue(HabitRecord.class);
                    habits.add(new Habit(data.getKey(), parsedRecord));

                }
                hideProgressIndicator();
                mHabitsAdapter.setHabits(habits);
                ReminderUtils.processAll(habits, HabitListActivity.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressIndicator();
                Log.e(TAG, databaseError.toString());
            }
        };

        mUserHabitsQuery.addValueEventListener(mValueEventListener);
    }

    private void detachDBReadListener() {
        if (mValueEventListener != null) {
            mUserHabitsQuery.removeEventListener(mValueEventListener);
            mValueEventListener = null;
        }
        mUserHabitsQuery = null;
    }

    private void showProgressIndicator() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressIndicator() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showDetail(Habit habit) {
        Intent intent = new Intent(this, HabitDetailActivity.class);
        intent.putExtra(HabitDetailActivity.HABIT_EXTRA_KEY, habit);
        startActivity(intent);
    }

    private void createHabit() {
        if (mFirebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, EditHabitActivity.class));
        }
    }

}