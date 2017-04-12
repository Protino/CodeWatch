package io.github.protino.codewatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.protino.codewatch.event.LoginEvent;
import io.github.protino.codewatch.model.WakatimeData;
import io.github.protino.codewatch.model.firebase.User;
import io.github.protino.codewatch.model.user.ProfileData;
import io.github.protino.codewatch.remote.FetchWakatimeData;
import io.github.protino.codewatch.ui.LoginActivity;
import io.github.protino.codewatch.ui.NavigationDrawerActivity;
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
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), getString(R.string.login_failed), Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            login(null);
                        }
                    });
            snackbar.show();
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
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //initialize achv
        firebaseDatabase.getReference().child("achv").child(uid).child(id).setValue(0);
        new FetchWakatimeDataTask(this,uid).execute();
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
        sharedPreferences.edit().putBoolean(Constants.PREF_FIREBASE_SETUP, true).commit();
        startActivity(new Intent(this, NavigationDrawerActivity.class));
        finish();
    }

    private class FetchWakatimeDataTask extends AsyncTask<Void, Void, Boolean> {

        private Context context;
        private String firebaseUid;

        public FetchWakatimeDataTask(Context context, String firebaseUid) {
            this.context = context;
            this.firebaseUid = firebaseUid;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            FetchWakatimeData fetchWakatimeData = new FetchWakatimeData(getApplicationContext());
            WakatimeData wakatimeData;
            try {
                wakatimeData = fetchWakatimeData.execute();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            User user = new TransformUtils(wakatimeData, new User()).execute();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("users");
            databaseReference.child(firebaseUid).setValue(user);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                onSetupComplete();
            } else {
                Toast.makeText(context, R.string.internet_error_message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class FetchBasicUserDetails extends AsyncTask<Void, Void, ProfileData> {
        private Context context;

        private FetchBasicUserDetails(Context context) {
            this.context = context;
        }

        @SuppressLint("ApplySharedPref")
        @Override
        protected ProfileData doInBackground(Void... params) {
            FetchWakatimeData fetchWakatimeData = new FetchWakatimeData(context);
            ProfileData data = null;
            try {
                data = fetchWakatimeData.fetchUserDetails().getProfileData();
                String gsonString = new Gson().toJson(data, ProfileData.class);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                sharedPreferences.edit().putString(Constants.PREF_BASIC_USER_DETAILS, gsonString).commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(ProfileData profileData) {
            initializeFirebaseUser(profileData);
        }
    }
}
