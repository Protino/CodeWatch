package io.github.protino.codewatch.ui.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CountDownLatch;

import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.firebase.Stats;
import io.github.protino.codewatch.ui.NavigationDrawerActivity;
import io.github.protino.codewatch.utils.CacheUtils;
import io.github.protino.codewatch.utils.FormatUtils;

/**
 * @author Gurupad Mamadapur
 */

public class PerformanceWidgetIntentService extends IntentService {

    private ValueEventListener valueEventListener;
    private DatabaseReference statsDbRef;
    private Stats stats;
    private Context context;

    public PerformanceWidgetIntentService() {
        super(PerformanceWidgetIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Retrieve widget ids
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, PerformanceWidgetProvider.class));
        //Create a latch for synchronization
        final CountDownLatch latch = new CountDownLatch(1);

        context = getApplicationContext();

        //Get firebaseuid
        String firebaseUid = CacheUtils.getFirebaseUserId(context);

        //Get data from firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        statsDbRef = firebaseDatabase.getReference()
                .child("users").child(firebaseUid).child("stats");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                stats = dataSnapshot.getValue(Stats.class);
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                latch.countDown();
            }
        };
        statsDbRef.addListenerForSingleValueEvent(valueEventListener);

        try {

            //wait for firebase to download data
            latch.await();

            //check if download is successful
            if (stats != null) {
                final int todaysTotalSeconds = stats.getTodaysTotalSeconds();
                final int dailyAverage = stats.getDailyAverageSeconds();

                float progress;
                if (todaysTotalSeconds < dailyAverage) {
                    progress = (todaysTotalSeconds / (float) dailyAverage) * 100f;
                } else {
                    progress = 100f;
                }
                progress = Math.round(progress * 100) / 100f;

                for (int appWidgetId : appWidgetIds) {
                    RemoteViews views = new RemoteViews(getPackageName(), R.layout.performance_widget);

                    views.setTextViewText(R.id.todays_activity,
                            getString(R.string.todays_total_log_time, FormatUtils.getFormattedTime(context, todaysTotalSeconds)));
                    views.setTextViewText(R.id.daily_average_text,
                            getString(R.string.daily_average_format, FormatUtils.getFormattedTime(context, dailyAverage)));

                    views.setProgressBar(R.id.performance_bar, 100, (int) progress, false);

                    String progressCd = getString(R.string.progress_description, progress);
                    views.setContentDescription(R.id.performance_bar, progressCd);

                    // Create an Intent to launch NavigationDrawerActivity
                    Intent launchIntent = new Intent(this, NavigationDrawerActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
                    views.setOnClickPendingIntent(R.id.widget, pendingIntent);

                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            }

        } catch (InterruptedException e) {
            FirebaseCrash.report(e);
        }
    }


    @Override
    public void onDestroy() {
        if (statsDbRef != null && valueEventListener != null) {
            statsDbRef.removeEventListener(valueEventListener);
        }
        super.onDestroy();
    }
}
