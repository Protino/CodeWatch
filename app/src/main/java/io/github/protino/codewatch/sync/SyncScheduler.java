package io.github.protino.codewatch.sync;


import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import io.github.protino.codewatch.utils.Constants;

public class SyncScheduler extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        Job synJob = dispatcher.newJobBuilder()
                .setService(WakatimeDataSyncJob.class)
                .setTag(Constants.WAKATIME_DATA_SYNC_JOB_TAG)
                .setReplaceCurrent(true)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(Constants.SYNC_PERIOD, Constants.SYNC_TOLERANCE))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .build();
        dispatcher.mustSchedule(synJob);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
