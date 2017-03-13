package io.github.protino.codewatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.protino.codewatch.remote.Constants;
import io.github.protino.codewatch.remote.FetchWakatimeData;
import io.github.protino.codewatch.remote.model.WakatimeData;
import io.github.protino.codewatch.remote.model.firebase.CustomPair;
import io.github.protino.codewatch.remote.model.firebase.Stats;
import io.github.protino.codewatch.remote.model.firebase.User;
import io.github.protino.codewatch.remote.model.statistics.Editor;
import io.github.protino.codewatch.remote.model.statistics.Language;
import io.github.protino.codewatch.remote.model.statistics.OperatingSystem;
import io.github.protino.codewatch.remote.model.statistics.StatsData;
import io.github.protino.codewatch.remote.model.user.ProfileData;
import timber.log.Timber;

import static io.github.protino.codewatch.remote.Constants.WAKATIME_DATA;

public class MainActivity extends AppCompatActivity {

    private static final String ANONYMOUS = "ANONYMOUS";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userDatabaseReference;
    private ChildEventListener childEventListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String firebaseUserId;
    private User user;

    //Lifecycle start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        userDatabaseReference = firebaseDatabase.getReference().child("users");

        firebaseAuth = FirebaseAuth.getInstance();
        user = readData(new View(this));

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    onSignedInInitialize(firebaseUser.getUid());
                } else {
                    onSignedOutCleanup();
                    signWithMailAndPassword(user.getEmail(), user.getUserId());
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        detachDatabaseReadListener();
    }
//Lifecycle end

    private void signWithMailAndPassword(final String email, final String userId) {
        firebaseAuth.signInWithEmailAndPassword(email, userId)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            createUser(email, userId);
                        }
                    }
                });
    }

    private void createUser(String email, String userId) {
        firebaseAuth.createUserWithEmailAndPassword(email, userId)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Timber.e(task.getException(), "Could not create user : ");
                        }
                    }
                });
    }

    private void onSignedInInitialize(String uid) {
        firebaseUserId = uid;
        attachDatabaseListener();
    }

    private void onSignedOutCleanup() {
        firebaseUserId = ANONYMOUS;
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {
        if (childEventListener != null) {
            userDatabaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    private void attachDatabaseListener() {
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //User addedUser = dataSnapshot.getValue(User.class);
                    //Timber.d("Child added : Name " + addedUser.getFullName());
                    Timber.d("Key " + dataSnapshot.getKey() + "  s: " + s);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Timber.d("Changed " + s);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            userDatabaseReference.child(firebaseUserId).addChildEventListener(childEventListener);
        }
    }

    public void fetchData(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getString(WAKATIME_DATA, null) != null) {
            new FetchWakatimeDataTask(this).execute();
        }
    }

    public void transform(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String data = sharedPreferences.getString(WAKATIME_DATA, null);
        WakatimeData wakatimeData = new Gson().fromJson(data, WakatimeData.class);

        User user = new User();
        ProfileData profileData = wakatimeData.getUserResponse().getProfileData();
        user.setEmail(profileData.getEmail());
        user.setFullName(profileData.getFullName());
        user.setAchievements(0);
        user.setCurrentPlan(profileData.getPlan());
        user.setIsEmailConfirmed(profileData.getIsEmailConfirmed());
        user.setUserId(profileData.getId());
        user.setWebsite(profileData.getWebsite());
        user.setTimeZone(profileData.getTimezone());
        user.setPhotoUrl(profileData.getPhoto());
        user.setHasPremiumFeatures(profileData.getHasPremiumFeatures());

        Stats stats = new Stats();
        StatsData statsData = wakatimeData.getStatsResponse().getStatsData();
        stats.setUpToDate(statsData.getIsUpToDate());
        stats.setStartDate(statsData.getStart());
        stats.setEndDate(statsData.getEnd());
        stats.setBestDayDate(statsData.getBestDay().getDate());
        stats.setBestDaySeconds(statsData.getBestDay().getTotalSeconds());
        stats.setDailyAverageSeconds(statsData.getDailyAverage());
        stats.setTotalSeconds(statsData.getTotalSeconds());
        stats.setChangeInTotalSeconds(0);

        // TODO: 12-03-2017 separate call to fetch projects data
        stats.setProjectPairList(null);

        List<CustomPair> languagePairsList = new ArrayList<>();
        List<CustomPair> osPairList = new ArrayList<>();
        List<CustomPair> editorPairList = new ArrayList<>();

        for (Language language : statsData.getLanguages()) {
            languagePairsList.add(new CustomPair(language.getName(), language.getTotalSeconds()));
        }
        for (OperatingSystem operatingSystem : statsData.getOperatingSystems()) {
            osPairList.add(new CustomPair(operatingSystem.getName(), operatingSystem.getTotalSeconds()));
        }
        for (Editor editor : statsData.getEditors()) {
            editorPairList.add(new CustomPair(editor.getName(), editor.getTotalSeconds()));
        }
        stats.setLanguagePairList(languagePairsList);
        stats.setOsPairList(osPairList);
        stats.setEditorPairList(editorPairList);

        user.setStats(stats);

        // TODO: 12-03-2017 Fetch, transform and update dynamically
        /*
        long start = System.currentTimeMillis();
        Map<String, Project> projectMap = new HashMap<>();
        for (ProjectsData projectsData : wakatimeData.getProjectsResponse().getProjectsList()) {
            Project project = new Project();
            project.setId(projectsData.getId());
            project.setName(projectsData.getName());
            project.setPublicUrl(projectsData.getPublicUrl());

            List<SummaryData> summary = wakatimeData.getSummaryResponse().get(project.getName()).getData();
            List<Integer> timeSpent = new ArrayList<>();
            languagePairsList = new ArrayList<>();
            osPairList = new ArrayList<>();
            editorPairList = new ArrayList<>();

            Map<String, Integer> editorsMap = new HashMap<>();
            Map<String, Integer> languageMap = new HashMap<>();
            Map<String, Integer> osMap = new HashMap<>();
            for (SummaryData summaryData : summary) {
                timeSpent.add(summaryData.getGrandTotal().getTotalSeconds());

                for (io.github.protino.codewatch.remote.model.project.summary.Editor editor : summaryData.getEditors()) {
                    Integer count = editorsMap.get(editor.getName());
                    editorsMap.put(editor.getName(), editor.getTotalSeconds() + ((count != null) ? count : 0));
                }
                for (io.github.protino.codewatch.remote.model.project.summary.OperatingSystem operatingSystem : summaryData.getOperatingSystems()) {
                    Integer count = osMap.get(operatingSystem.getName());
                    osMap.put(operatingSystem.getName(), operatingSystem.getTotalSeconds() + ((count != null) ? count : 0));
                }
                for (io.github.protino.codewatch.remote.model.project.summary.Language language : summaryData.getLanguages()) {
                    Integer count = languageMap.get(language.getName());
                    languageMap.put(language.getName(), language.getTotalSeconds() + ((count != null) ? count : 0));
                }
            }

            for (Map.Entry<String, Integer> entry : editorsMap.entrySet()) {
                editorPairList.add(new CustomPair(entry.getKey(), entry.getValue()));
            }
            for (Map.Entry<String, Integer> entry : languageMap.entrySet()) {
                languagePairsList.add(new CustomPair(entry.getKey(), entry.getValue()));
            }
            for (Map.Entry<String, Integer> entry : osMap.entrySet()) {
                osPairList.add(new CustomPair(entry.getKey(), entry.getValue()));
            }

            project.setTimeSpent(timeSpent);
            project.setOsPairList(osPairList);
            project.setLanguageList(languagePairsList);
            project.setEditorPaiList(editorPairList);

            projectMap.put(project.getId(), project);
        }
        Timber.i("Projects data calculated - " + (System.currentTimeMillis() - start) + " ms");
        user.setProjects(projectMap);
        */

        Gson gson = new Gson();
        String userDataString = gson.toJson(user);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.FIREBASE_USER_DATA_PREF_KEY, userDataString);
        editor.apply();
    }

    public User readData(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String jsonString = sharedPreferences.getString(Constants.FIREBASE_USER_DATA_PREF_KEY, null);
        return new Gson().fromJson(jsonString, User.class);
        //Timber.i(user.getFullName());
    }

    public void sendData(View view) {
        if (userDatabaseReference != null) {
            userDatabaseReference.child(firebaseUserId).setValue(user);
        }
    }

    public void updateData(View view) {
        userDatabaseReference.child(firebaseUserId).child("timeZone").setValue("10  ");
    }

    public void logout(View view) {
        firebaseAuth.signOut();
        onSignedOutCleanup();
    }

    public void launchLeadersActivity(View view) {

    }

    private class FetchWakatimeDataTask extends AsyncTask<Void, Void, Void> {

        private Context context;

        public FetchWakatimeDataTask(Context context) {
            this.context = context;
        }

        @SuppressLint("ApplySharedPref")
        @Override
        protected Void doInBackground(Void... params) {
            Timber.i("Starting to fetch");
            WakatimeData wakatimeData;
            try {
                wakatimeData = new FetchWakatimeData(getApplicationContext()).execute();
            } catch (IOException e) {
                return null;
            }
            Timber.i("Data fetched");
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String dataString = gson.toJson(wakatimeData);
            Timber.i("Data converted");
            editor.putString(Constants.WAKATIME_DATA, dataString);
            editor.commit();
            Timber.i("Service completed successfully");
            return null;
        }
    }
}
