package io.github.protino.codewatch.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.joda.time.Hours;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.event.LoginEvent;
import io.github.protino.codewatch.model.WakatimeDataWrapper;
import io.github.protino.codewatch.model.firebase.User;
import io.github.protino.codewatch.model.user.ProfileData;
import io.github.protino.codewatch.remote.FetchLeaderBoardData;
import io.github.protino.codewatch.remote.FetchWakatimeData;
import io.github.protino.codewatch.sync.SyncScheduler;
import io.github.protino.codewatch.utils.CacheUtils;
import io.github.protino.codewatch.utils.Constants;
import io.github.protino.codewatch.utils.TransformUtils;
import timber.log.Timber;

public class OnBoardActivity extends AppCompatActivity {

    //@formatter:off
    @BindView(R.id.login_content) View loginContent;
    @BindView(R.id.setup_content) View setupContent;
    //@formatter:on
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_on_board);
        ButterKnife.bind(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (!CacheUtils.isLoggedIn(this)) {
            loginContent.setVisibility(View.VISIBLE);
            setupContent.setVisibility(View.GONE);
        } else {
            loginContent.setVisibility(View.GONE);
            setupContent.setVisibility(View.VISIBLE);
            setUpForFirstTimeUse();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void login(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onLogin(final LoginEvent loginEvent) {
        if (loginEvent.isLoginSuccess()) {
            setUpForFirstTimeUse();
        } else {
            displaySnackBar(R.string.login_failed, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login(null);
                }
            });
        }
    }

    private void setUpForFirstTimeUse() {
        loginContent.setVisibility(View.GONE);
        setupContent.setVisibility(View.VISIBLE);
        new FetchBasicUserDetails(this).execute();
    }

    private void initializeFirebaseUser(final ProfileData profileData) {
        if (profileData != null) {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        onSignedInInitialize(firebaseUser.getUid(), profileData.getId());
                    } else {
                        signWithMailAndPassword(profileData.getEmail(), profileData.getId());
                    }
                }
            });
        }
    }

    @SuppressLint("ApplySharedPref")
    private void onSignedInInitialize(String uid, String id) {
        sharedPreferences.edit().putString(Constants.PREF_FIREBASE_USER_ID, uid).commit();
        new FetchWakatimeDataTask(this, uid).execute();
    }

    /**
     * For the time being, uid of the user is used as the password
     * <br/>
     * <br/>
     * todo : add another sign in option or ask user if he wants to save his/her data on cloud
     *
     * @param mail mail of user
     * @param pass for now, uid is used, which is not correct
     */
    private void signWithMailAndPassword(final String mail, final String pass) {
        firebaseAuth.signInWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            createUser(mail, pass);
                        }
                    }
                });
    }

    private void createUser(String mail, String pass) {
        firebaseAuth.createUserWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Timber.e(task.getException(), "Could not create user : ");
                        }
                    }
                });
    }

    @SuppressLint("ApplySharedPref")
    private void onSetupComplete() {
        //download whole leaderboards data asynchronously
        new FetchLeaderBoardDataAsync(this).execute();

        //schedule daily sync at 3:00 am local time
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        DateTime now = new DateTime();
        DateTime tomorrow = now.plusDays(1).withTimeAtStartOfDay().plusHours(3);
        int windowStart = Hours.hoursBetween(now, tomorrow).getHours() * 60 * 60;
        Job synJob = dispatcher.newJobBuilder()
                .setService(SyncScheduler.class)
                .setTag(Constants.PERIODIC_SYNC_SCHEDULE_KEY)
                .setReplaceCurrent(true)
                .setTrigger(Trigger.executionWindow(windowStart, windowStart + 10))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .build();
        dispatcher.mustSchedule(synJob);

        sharedPreferences.edit().putBoolean(Constants.PREF_FIREBASE_SETUP, true).commit();
        startActivity(new Intent(this, NavigationDrawerActivity.class));
        finish();
    }

    private void emailNotConfirmed() {
        setupContent.setVisibility(View.INVISIBLE);
        displaySnackBar(R.string.confirm_mail, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchBasicUserDetails(OnBoardActivity.this).execute();
            }
        });
    }

    public void displaySnackBar(@StringRes int errorTextId, View.OnClickListener onClickListener) {
        if (snackbar != null) {
            snackbar.dismiss();
        }
        snackbar = Snackbar
                .make(getWindow().getDecorView(), errorTextId, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, onClickListener);
        snackbar.show();
    }

    private class FetchWakatimeDataTask extends AsyncTask<Void, Void, Integer> {

        private Context context;
        private String firebaseUid;

        public FetchWakatimeDataTask(Context context, String firebaseUid) {
            this.context = context;
            this.firebaseUid = firebaseUid;
        }

        @Override
        protected
        @Constants.ErrorCodes
        Integer doInBackground(Void... params) {
            FetchWakatimeData fetchWakatimeData = new FetchWakatimeData(getApplicationContext());
            WakatimeDataWrapper wakatimeDataWrapper;
            User user;
            try {
                wakatimeDataWrapper = fetchWakatimeData.execute();
                user = new TransformUtils(wakatimeDataWrapper, new User()).execute();
            } catch (IOException e) {
                e.printStackTrace();
                return Constants.INTERNET_OFF;
            } catch (NullPointerException e) {
                return Constants.STATS_UPDATING;
            } catch (Exception e) {
                FirebaseCrash.report(e);
                return Constants.UNKNOWN_ERROR;
            }
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("users");
            databaseReference.child(firebaseUid).setValue(user);
            return Constants.NONE;
        }

        @Override
        protected void onPostExecute(@Constants.ErrorCodes Integer result) {
            int errorText = -1;
            switch (result) {
                case Constants.NONE:
                    onSetupComplete();
                    break;
                case Constants.INTERNET_OFF:
                    errorText = R.string.internet_error_message;
                    break;
                case Constants.STATS_UPDATING:
                    errorText = R.string.stats_updating_error_message;
                    break;
                default:
                    errorText = R.string.unknown_error_message;
                    break;
            }
            if (errorText != -1) {
                displaySnackBar(errorText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new FetchWakatimeDataTask(context, firebaseUid).execute();
                    }
                });
            }
        }
    }

    private class FetchBasicUserDetails extends AsyncTask<Void, Void, ProfileData> {
        private Context context;

        private FetchBasicUserDetails(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setupContent.setVisibility(View.VISIBLE);
            if (snackbar != null) {
                snackbar.dismiss();
            }
        }

        @SuppressLint("ApplySharedPref")
        @Override
        protected ProfileData doInBackground(Void... params) {
            FetchWakatimeData fetchWakatimeData = new FetchWakatimeData(context);
            ProfileData data = null;
            try {
                data = fetchWakatimeData.fetchUserDetails().getProfileData();
                String isEmailConfirmed = data.getIsEmailConfirmed();
                if (isEmailConfirmed != null && !isEmailConfirmed.isEmpty() && !isEmailConfirmed.equals("null")) {
                    data.setRank(new FetchLeaderBoardData(context).fetchUserRank());
                }

                String gsonString = new Gson().toJson(data, ProfileData.class);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.PREF_BASIC_USER_DETAILS, gsonString);
                editor.putString(Constants.PREF_WAKATIME_USER_ID, data.getId());
                editor.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(ProfileData profileData) {
            String isEmailConfirmed = profileData.getIsEmailConfirmed();
            if (isEmailConfirmed == null || isEmailConfirmed.isEmpty() || isEmailConfirmed.equals("null")) {
                //ask user check their inbox and confirm mail
                emailNotConfirmed();
            } else {
                initializeFirebaseUser(profileData);
            }
        }
    }

    private class FetchLeaderBoardDataAsync extends AsyncTask<Void, Void, Void> {
        private Context context;

        private FetchLeaderBoardDataAsync(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            new FetchLeaderBoardData(context).execute();
            return null;
        }
    }
}