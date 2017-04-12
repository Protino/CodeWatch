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

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;
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
import java.util.HashMap;
import java.util.Map;

import io.github.protino.codewatch.model.WakatimeData;
import io.github.protino.codewatch.model.firebase.Goals;
import io.github.protino.codewatch.model.firebase.LanguageGoal;
import io.github.protino.codewatch.model.firebase.ProjectGoal;
import io.github.protino.codewatch.model.firebase.Stats;
import io.github.protino.codewatch.model.firebase.User;
import io.github.protino.codewatch.model.statistics.Editor;
import io.github.protino.codewatch.model.statistics.Language;
import io.github.protino.codewatch.model.statistics.OperatingSystem;
import io.github.protino.codewatch.model.statistics.StatsData;
import io.github.protino.codewatch.model.user.ProfileData;
import io.github.protino.codewatch.remote.FetchWakatimeData;
import io.github.protino.codewatch.sync.WakatimeDataSyncJob;
import io.github.protino.codewatch.utils.Constants;
import timber.log.Timber;

import static io.github.protino.codewatch.utils.Constants.PREF_WAKATIME_DATA_UPDATED;

public class FirebaseTestActivity extends AppCompatActivity {

    private static final String ANONYMOUS = "ANONYMOUS";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userDatabaseReference;
    private DatabaseReference goalsDatabaseReference;
    private ChildEventListener childEventListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String firebaseUserId;
    private DatabaseReference achievementsReference;
    private DatabaseReference goalsReference;
    private User user;

    //Lifecycle start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        userDatabaseReference = firebaseDatabase.getReference().child("users");
        achievementsReference = firebaseDatabase.getReference().child("achv");
        goalsDatabaseReference = firebaseDatabase.getReference().child("goals");

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
                    signWithMailAndPassword("guru@gmail.com", "12313213");
                    // TODO: 17-03-2017  generate password with random stuff obfuscate the code
                }
            }
        };

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        Job synJob = dispatcher.newJobBuilder()
                .setService(WakatimeDataSyncJob.class)
                .setTag(Constants.WAKATIME_DATA_SYNC_JOB_TAG)
                .setReplaceCurrent(true)
                .setRecurring(false)
                .setTrigger(Trigger.executionWindow(1, 2))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        //dispatcher.mustSchedule(synJob);

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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(Constants.PREF_FIREBASE_USER_ID, uid).apply();
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
                    //Timber.d("Child added : Name " + addedUser.getDisplayName());
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
        if (sharedPreferences.getString(PREF_WAKATIME_DATA_UPDATED, null) != null) {
            new FetchWakatimeDataTask(this).execute();
        }
    }

    public void transform(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String data = sharedPreferences.getString(PREF_WAKATIME_DATA_UPDATED, null);
        WakatimeData wakatimeData = new Gson().fromJson(data, WakatimeData.class);

        User user = new User();
        ProfileData profileData = wakatimeData.getUserResponse().getProfileData();
        user.setEmail(profileData.getEmail());
        user.setDisplayName(profileData.getFullName());
        //user.setAchievements(0); // TODO: 16-03-2017 Store as achievements as auth data
        user.setCurrentPlan(profileData.getPlan());
        user.setIsEmailConfirmed(profileData.getIsEmailConfirmed());
        user.setUserId(profileData.getId());
        user.setWebsite(profileData.getWebsite());
        user.setTimeZone(profileData.getTimezone());
        user.setPhotoUrl(profileData.getPhoto());
        user.setHasPremiumFeatures(profileData.getHasPremiumFeatures());
        // Todo : Add generic language goals here

        Stats stats = new Stats();
        StatsData statsData = wakatimeData.getStatsResponse().getStatsData();
        stats.setUpToDate(statsData.getIsUpToDate());
        stats.setStartDate(statsData.getStart());
        stats.setEndDate(statsData.getEnd());
        stats.setBestDayDate(statsData.getBestDay().getDate());
        stats.setBestDaySeconds(statsData.getBestDay().getTotalSeconds());
        stats.setDailyAverageSeconds(statsData.getDailyAverage());
        stats.setTotalSeconds(statsData.getTotalSeconds());

        // TODO: 12-03-2017 separate call to fetch projects data
        stats.setProjectPairList(null);

        Map<String, Integer> map = new HashMap<>();

        for (Language language : statsData.getLanguages()) {
            map.put(language.getName(), language.getTotalSeconds());
        }
        stats.setLanguagesMap(map);

        map = new HashMap<>();
        for (OperatingSystem operatingSystem : statsData.getOperatingSystems()) {
            map.put(operatingSystem.getName(), operatingSystem.getTotalSeconds());
        }
        stats.setOsMap(map);

        map = new HashMap<>();

        for (Editor editor : statsData.getEditors()) {
            map.put(editor.getName(), editor.getTotalSeconds());
        }
        stats.setEditorsMap(map);

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
            project.setOsMap(osPairList);
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
        editor.putString(Constants.PREF_FIREBASE_USER_DATA, userDataString);
        editor.apply();
    }

    public User readData(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String jsonString = sharedPreferences.getString(Constants.PREF_FIREBASE_USER_DATA, null);
        return new Gson().fromJson(jsonString, User.class);
        //Timber.i(user.getDisplayName());
    }

    public void sendData(View view) {
        if (goalsDatabaseReference != null) {
            Goals goals = new Goals();

            Map<String, LanguageGoal> map = new HashMap<>();
            LanguageGoal languageGoal = new LanguageGoal("Java", 45684, new int[]{2, 4, 5, 6, 7, 78});

            map.put("Java", languageGoal);
            goals.setLanguageGoals(map);

            ProjectGoal projectGoal = new ProjectGoal("32424", "CodeWatch", 43, 564, 345, new int[]{23, 5, 46, 56});
            projectGoal.setDaily(2432);
            projectGoal.setDeadline(2343);

            Map<String, ProjectGoal> newMap = new HashMap<>();
            newMap.put("CodeWatch", projectGoal);
            goals.setProjectGoals(newMap);

            goalsDatabaseReference.child(firebaseUserId).setValue(goals);
            //userDatabaseReference.child(firebaseUserId).setValue(user);
            //Map<String, Integer> hashMap = new HashMap();
            //hashMap.put(user.getUserId(), 123);
            //achievementsReference.child(firebaseUserId).setValue(hashMap);
        }
    }

    public void updateData(View view) {
        //userDatabaseReference.child(firebaseUserId).child("goals").setValue("10  ");
    }

    public void logout(View view) {
        firebaseAuth.signOut();
        onSignedOutCleanup();
    }

    public void launchLeadersActivity(View view) {
        //startActivity(new Intent(this, LeaderActivity.class));
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
            editor.putString(Constants.PREF_WAKATIME_DATA_UPDATED, dataString);
            editor.commit();
            Timber.i("Service completed successfully");
            return null;
        }
    }
}
