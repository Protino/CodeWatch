package io.github.protino.codewatch.sync;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.github.protino.codewatch.remote.FetchWakatimeData;
import io.github.protino.codewatch.remote.model.WakatimeData;
import io.github.protino.codewatch.remote.model.firebase.User;
import io.github.protino.codewatch.remote.model.leaders.LeadersData;
import io.github.protino.codewatch.utils.Cache;
import io.github.protino.codewatch.utils.Constants;
import io.github.protino.codewatch.utils.LeaderDb;
import io.github.protino.codewatch.utils.Transform;
import timber.log.Timber;


public class WakatimeDataSyncJob extends JobService {
    @Override
    public boolean onStartJob(final JobParameters job) {
        //check whether logged in or not, also check network connectivity
        if (!Cache.isLoggedIn(getApplicationContext())) {
            jobFinished(job, true);
        }
        new MainThread(job).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    private class MainThread extends Thread {
        private JobParameters jobParameters;
        private CountDownLatch successLatch = new CountDownLatch(2);
        private volatile boolean needsReschedule = false;

        public MainThread(JobParameters parameters) {
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

        public FirebaseSyncTask(CountDownLatch countDownLatch, boolean needsReschedule) {
            this.countDownLatch = countDownLatch;
            this.needsReschedule = needsReschedule;
        }

        @SuppressLint("ApplySharedPref")
        @Override
        public void run() {
            //download all relevant data and sync with firebase
            try {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.WAKATIME_DATA_UPDATED, false);
                FetchWakatimeData fetchWakatimeData = new FetchWakatimeData(getApplicationContext());
                WakatimeData wakatimeData = fetchWakatimeData.execute();
                Timber.d("Successfully download wakatimeData");
                User user = new Transform(wakatimeData, new User()).execute();
                //save to firebase
                Timber.d("Saving to firebase rdb");
                String uid = sharedPreferences.getString(Constants.FIREBASE_USER_ID_PREF_KEY, null);
                if (uid != null) {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference().child("users");
                    databaseReference.child(uid).setValue(user);
                    editor.putBoolean(Constants.WAKATIME_DATA_UPDATED, true);
                }
                editor.commit();
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

        public LeaderboardSyncTask(CountDownLatch countDownLatch, boolean needsReschedule) {
            this.countDownLatch = countDownLatch;
            this.needsReschedule = needsReschedule;
        }

        @Override
        public void run() {
            FetchWakatimeData fetchWakatimeData = new FetchWakatimeData(getApplicationContext());
            try {
                List<LeadersData> dataList = fetchWakatimeData.fetchLeaders().getData();
                Timber.d("Successfully download leadersData");
                LeaderDb.store(getApplicationContext(), dataList);
                Timber.d("Successfully stored wakatimeData");
            } catch (Exception e) {
                Timber.d(e, "LeaderboardSyncTask failed");
                needsReschedule = true;
            }
            countDownLatch.countDown();
        }
    }
}
