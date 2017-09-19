package com.kabirkang.habitgrove.sync;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;

import com.kabirkang.habitgrove.R;
import com.kabirkang.habitgrove.activities.HabitDetailActivity;
import com.kabirkang.habitgrove.models.Habit;
import com.kabirkang.habitgrove.utils.ReminderUtils;

public class ReminderIntentService extends IntentService {

    private static final String ACTION_START = "ACTION_START";

    public ReminderIntentService() {
        super(ReminderIntentService.class.getSimpleName());
    }

    public static Intent buildInstance(Habit habit, Context context) {
        Intent intent = new Intent(context, ReminderIntentService.class);
        intent.setAction(ACTION_START);
        intent.putExtra(ReminderUtils.HABIT_EXTRA_KEY, habit);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (ACTION_START.equals(intent.getAction())) {
                Habit habit = intent.getParcelableExtra(ReminderUtils.HABIT_EXTRA_KEY);
                processNotification(habit);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }


    private void processNotification(Habit habit) {
        Intent resultIntent = new Intent(this, HabitDetailActivity.class);
        resultIntent.putExtra(HabitDetailActivity.HABIT_EXTRA_KEY, habit);
        final int notificationId = (int) habit.getRecord().getCreatedAt();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getString(R.string.app_name))
                .setAutoCancel(true)
                .setColor(habit.getRecord().getColor())
                .setContentText(habit.getRecord().getName())
                .setSmallIcon(R.mipmap.ic_launcher);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(notificationId,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, builder.build());
    }

}