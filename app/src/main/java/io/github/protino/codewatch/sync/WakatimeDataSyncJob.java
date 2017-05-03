package io.github.protino.codewatch.sync;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.github.protino.codewatch.R;
import io.github.protino.codewatch.data.LeaderContract;
import io.github.protino.codewatch.model.GoalItem;
import io.github.protino.codewatch.model.WakatimeDataWrapper;
import io.github.protino.codewatch.model.firebase.User;
import io.github.protino.codewatch.remote.FetchLeaderBoardData;
import io.github.protino.codewatch.remote.FetchWakatimeData;
import io.github.protino.codewatch.ui.NavigationDrawerActivity;
import io.github.protino.codewatch.utils.CacheUtils;
import io.github.protino.codewatch.utils.Constants;
import io.github.protino.codewatch.utils.FormatUtils;
import io.github.protino.codewatch.utils.TransformUtils;
import timber.log.Timber;

import static io.github.protino.codewatch.utils.Constants.GOAL_NOTIFICATION_ID;
import static io.github.protino.codewatch.utils.Constants.LANGUAGE_GOAL;
import static io.github.protino.codewatch.utils.Constants.LEADERBOARD_NOTIFICATION_ID;
import static io.github.protino.codewatch.utils.Constants.PREF_WAKATIME_USER_ID;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DAILY_GOAL;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DEADLINE_GOAL;


public class WakatimeDataSyncJob extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        //check whether logged in or not, also check network connectivity
        if (!CacheUtils.isLoggedIn(getApplicationContext())) {
            return false;
        }
        new MainThread(job).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    public void updateNotifications() {
        //Notify goals
        final CountDownLatch firebaseLatch = new CountDownLatch(1);
        final List<GoalItem> goalItemList = new ArrayList<>();
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean notifyGoals = sharedPreferences.getBoolean(context.getString(R.string.pref_goal_reminders_key), true);
        boolean notifyRank = sharedPreferences.getBoolean(context.getString(R.string.pref_leaderboard_changes_key), true);
        String firebaseUid = sharedPreferences.getString(Constants.PREF_FIREBASE_USER_ID, null);


        String title = context.getString(R.string.app_name);
        Intent resultIntent = new Intent(context, NavigationDrawerActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntent(resultIntent);
        PendingIntent pendingIntent =
                taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        if (notifyGoals && firebaseUid != null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("goals").child(firebaseUid);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        goalItemList.add(snapshot.getValue(GoalItem.class));
                    }
                    firebaseLatch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    firebaseLatch.countDown();
                }
            });
            try {
                //Build the notification
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(context.getString(R.string.goals_reminder));

                firebaseLatch.await();
                NotificationCompat.InboxStyle inboxStyle =
                        new NotificationCompat.InboxStyle();

                for (GoalItem goalItem : goalItemList) {
                    String goalText = null;
                    switch (goalItem.getType()) {
                        case LANGUAGE_GOAL:
                            goalText = context.getString(R.string.language_goal, goalItem.getName(), goalItem.getData());
                            break;
                        case PROJECT_DAILY_GOAL:
                            goalText = context.getString(R.string.project_daily_goal, goalItem.getData(), goalItem.getName());
                            break;
                        case PROJECT_DEADLINE_GOAL:
                            long deadline = goalItem.getData();
                            long currentDate = System.currentTimeMillis();
                            int remainingDays = Days.daysBetween(new DateTime(currentDate), new DateTime(deadline)).getDays();

                            if (remainingDays > 0) {
                                if (currentDate < deadline) {
                                    goalText = context.getString(R.string.finish_project_today, goalItem.getName());
                                }
                            } else {
                                goalText = context.getString(R.string.finish_project_within, goalItem.getName(), remainingDays);
                            }
                            break;
                        default:
                            break;
                    }
                    if (goalText != null) {
                        inboxStyle.addLine(goalText);
                    }
                }
                builder.setStyle(inboxStyle);
                builder.setContentIntent(pendingIntent);

                NotificationManager notificationManager
                        = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(GOAL_NOTIFICATION_ID, builder.build());
            } catch (InterruptedException e) {
                Timber.e(e);
            }
        }

        if (notifyRank) {
            String wakatimeUid = sharedPreferences.getString(PREF_WAKATIME_USER_ID, null);
            Cursor cursor = getContentResolver().query(
                    LeaderContract.LeaderEntry.buildProfileUri(wakatimeUid),
                    null,
                    null,
                    null,
                    null);
            if (cursor == null || !cursor.moveToFirst()) {
                return;
            }
            int rank = cursor.getInt(cursor.getColumnIndex(LeaderContract.LeaderEntry.COLUMN_RANK));
            int dailyAverage = cursor.getInt(cursor.getColumnIndex(LeaderContract.LeaderEntry.COLUMN_DAILY_AVERAGE));

            String text = context.getString(
                    R.string.leaderboard_notification_text, rank, FormatUtils.getFormattedTime(context, dailyAverage));
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
                            .setContentTitle(title)
                            .setContentText(text)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                            .setContentIntent(pendingIntent);
            NotificationManager notificationManager
                    = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(LEADERBOARD_NOTIFICATION_ID, builder.build());
            cursor.close();
        }

    }

    private void updateWidgets() {
        Context context = getApplicationContext();
        Intent intent = new Intent(Constants.ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }

    private class MainThread extends Thread {
        private JobParameters jobParameters;
        private CountDownLatch successLatch = new CountDownLatch(2);
        private volatile boolean needsReschedule = false;

        private MainThread(JobParameters parameters) {
            jobParameters = parameters;
        }

        @Override
        public void run() {
            try {
                // TODO: 17-03-2017 Schedule only if necessary
                new FirebaseSyncTask(successLatch, needsReschedule).start();
                new LeaderboardSyncTask(successLatch, needsReschedule).start();
                Timber.d("Jobs Started " + System.currentTimeMillis());
                successLatch.await();
                Timber.d("Jobs finished" + System.currentTimeMillis());
                if (!needsReschedule) {
                    updateNotifications();
                    updateWidgets();
                }
                jobFinished(jobParameters, needsReschedule);
            } catch (InterruptedException e) {
                e.printStackTrace();
                jobFinished(jobParameters, true);
            }
        }
    }

    private class FirebaseSyncTask extends Thread {

        volatile boolean needsReschedule;
        private CountDownLatch countDownLatch;

        private FirebaseSyncTask(CountDownLatch countDownLatch, boolean needsReschedule) {
            this.countDownLatch = countDownLatch;
            this.needsReschedule = needsReschedule;
        }

        @SuppressLint("ApplySharedPref")
        @Override
        public void run() {
            //download all relevant data and sync with firebase
            try {
                FetchWakatimeData fetchWakatimeData = new FetchWakatimeData(getApplicationContext());
                WakatimeDataWrapper wakatimeDataWrapper = fetchWakatimeData.execute();
                Timber.d("Successfully download wakatimeData");
                User user = new TransformUtils(wakatimeDataWrapper, new User()).execute();
                //save to firebase
                Timber.d("Saving to firebase rdb");
                String uid = CacheUtils.getFirebaseUserId(getApplicationContext());
                if (uid != null) {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference().child("users");
                    databaseReference.child(uid).setValue(user);
                }
            } catch (Exception e) {
                Timber.e(e, "LeaderboardSyncTask failed");
                needsReschedule = true;
            }
            countDownLatch.countDown();
        }
    }

    private class LeaderboardSyncTask extends Thread {
        volatile boolean needsReschedule;
        private CountDownLatch countDownLatch;

        private LeaderboardSyncTask(CountDownLatch countDownLatch, boolean needsReschedule) {
            this.countDownLatch = countDownLatch;
            this.needsReschedule = needsReschedule;
        }

        @Override
        public void run() {
            FetchLeaderBoardData fetchLeaderBoardData = new FetchLeaderBoardData(getApplicationContext());
            try {
                boolean result = fetchLeaderBoardData.execute();
                if (!result) {
                    Timber.e("LeaderboardSyncTask failed");
                    needsReschedule = true;
                } else {

                }
            } catch (Exception e) {
                Timber.d(e, "LeaderboardSyncTask failed");
                needsReschedule = true;
            }
            countDownLatch.countDown();
        }
    }
}
