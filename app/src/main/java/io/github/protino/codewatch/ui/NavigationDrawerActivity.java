/*
 * Copyright 2017 Gurupad Mamadapur
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.protino.codewatch.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.bundler.ProfileDataBundler;
import io.github.protino.codewatch.data.LeaderContract;
import io.github.protino.codewatch.model.user.ProfileData;
import io.github.protino.codewatch.remote.DashboardFragment;
import io.github.protino.codewatch.utils.AchievementsUtils;
import io.github.protino.codewatch.utils.CacheUtils;
import io.github.protino.codewatch.utils.Constants;

import static io.github.protino.codewatch.utils.Constants.BRONZE_BADGE;
import static io.github.protino.codewatch.utils.Constants.GOLD_BADGE;
import static io.github.protino.codewatch.utils.Constants.SILVER_BADGE;

/**
 * This activity handles setting up the navigation drawer fragment
 *
 * @author Gurupad Mamadapur
 */

public class NavigationDrawerActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int RANK_LOADER_ID = 99;
    //@formatter:off
    @State  public String firebaseUid;
    @BindView(R.id.toolbar) public Toolbar toolbar;
    @BindView(R.id.drawer_layout) public DrawerLayout drawerLayout;
    @BindView(R.id.navigationView) public NavigationView navigationView;

    @State public AtomicInteger mutex = new AtomicInteger();
    @State public boolean hasUserLearntDrawer;
    @State(ProfileDataBundler.class) public ProfileData basicUserData;
    @State public long currentAchievements;
    @State public long newAchievements = 0;
    @State public boolean appUsageAchievementsChecked = false;
    //@formatter:on
    private ActionBarDrawerToggle drawerToggler;
    private SharedPreferences sharedPreferences;

    private DatabaseReference achievementsDatabaseRef;
    private ValueEventListener valueEventListener;

    private TextView username;
    private TextView silverBadgeCounts;
    private TextView goldBadgeCounts;
    private TextView bronzeBadgeCounts;
    private ImageView avatar;
    private TextView rankText;

    private FirebaseAnalytics firebaseAnalytics;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        setContentView(R.layout.activity_navigation_drawer);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (savedInstanceState == null) {
            firebaseUid = sharedPreferences.getString(Constants.PREF_FIREBASE_USER_ID, null);
            String basicData = sharedPreferences.getString(Constants.PREF_BASIC_USER_DETAILS, null);
            basicUserData = new Gson().fromJson(basicData, ProfileData.class);

            MenuItem item = navigationView.getMenu().findItem(R.id.dashboard);
            onNavigationItemSelected(item);
            item.setChecked(true);
        }

        setupDrawer();
        mutex.set(0);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        achievementsDatabaseRef = firebaseDatabase.getReference().child("achv").child(firebaseUid);
        getLoaderManager().initLoader(RANK_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachValueEventListener();
    }

    @Override
    protected void onPause() {
        detachValueEventListener();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Icepick.saveInstanceState(this, outState);
        super.onSaveInstanceState(outState);
    }

    private void attachValueEventListener() {
        if (valueEventListener == null) {
            valueEventListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object data = dataSnapshot.child(basicUserData.getId()).getValue();
                    if (data != null) {
                        currentAchievements = (long) data;
                    } else {
                        currentAchievements = 0;
                    }
                    mutex.incrementAndGet();
                    onAchievementsLoaded();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
        }
        achievementsDatabaseRef.addValueEventListener(valueEventListener);
    }


    private void detachValueEventListener() {
        if (valueEventListener != null) {
            achievementsDatabaseRef.removeEventListener(valueEventListener);
            valueEventListener = null;
        }
    }

    private void setupDrawer() {
        navigationView.setNavigationItemSelectedListener(this);
        drawerToggler = setupDrawerToggler();
        setupHeader();
        drawerLayout.setDrawerListener(drawerToggler);
        hasUserLearntDrawer = sharedPreferences.getBoolean(Constants.PREF_USER_LEARNED_DRAWER, false);
        if (!hasUserLearntDrawer) {
            drawerLayout.openDrawer(Gravity.START);
        }
    }

    private ActionBarDrawerToggle setupDrawerToggler() {
        return new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                if (!hasUserLearntDrawer) {
                    sharedPreferences.edit().putBoolean(Constants.PREF_USER_LEARNED_DRAWER, true).apply();
                }
                super.onDrawerClosed(drawerView);
            }
        };
    }

    private void setupHeader() {

        View navigationHeader = navigationView.inflateHeaderView(R.layout.navigation_header);

        username = (TextView) navigationHeader.findViewById(R.id.username);
        avatar = (ImageView) navigationHeader.findViewById(R.id.avatar);
        rankText = (TextView) navigationHeader.findViewById(R.id.rank);

        int rank = basicUserData.getRank();
        rankText.setText(rank != -1 ? String.valueOf(rank) : "-");

        silverBadgeCounts = (TextView) navigationHeader.findViewById(R.id.silver_badge_count);
        goldBadgeCounts = (TextView) navigationHeader.findViewById(R.id.gold_badge_count);
        bronzeBadgeCounts = (TextView) navigationHeader.findViewById(R.id.bronze_badge_count);


        navigationHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(NavigationDrawerActivity.this);
                Intent intent = new Intent(NavigationDrawerActivity.this, ProfileActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, getWakatimeUid());
                startActivity(intent, options.toBundle());
            }
        });
    }

    private void bindHeaderViews() {
        username.setText(basicUserData.getDisplayName());
        if (!basicUserData.getPhotoPublic()) {
            basicUserData.setPhoto(null);
        }
        Glide.with(this)
                .load(basicUserData.getPhoto())
                .asBitmap()
                .placeholder(R.drawable.ic_account_circle_white_24dp)
                .into(new BitmapImageViewTarget(avatar) {
                    @Override
                    protected void setResource(Bitmap resource) {

                        RoundedBitmapDrawable drawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        drawable.setCircular(true);
                        avatar.setImageDrawable(drawable);
                    }
                });

        String goldCount = String.valueOf(AchievementsUtils.getBadgeCount(currentAchievements, GOLD_BADGE));
        String silverCount = String.valueOf(AchievementsUtils.getBadgeCount(currentAchievements, SILVER_BADGE));
        String bronzeCount = String.valueOf(AchievementsUtils.getBadgeCount(currentAchievements, BRONZE_BADGE));

        goldBadgeCounts.setText(goldCount);
        silverBadgeCounts.setText(silverCount);
        bronzeBadgeCounts.setText(bronzeCount);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggler.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggler.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        item.setChecked(!item.isChecked());
        String text;
        switch (item.getItemId()) {
            case R.id.dashboard:
                text = getString(R.string.dashboard_title);
                replaceFragment(new DashboardFragment(), text);
                break;
            case R.id.goals:
                text = getString(R.string.goals_title);
                replaceFragment(new GoalsFragment(), text);
                break;
            case R.id.achievements:
                text = getString(R.string.achievements_title);
                replaceFragment(new AchievementFragment(), text);
                break;
            case R.id.leaderboards:
                text = getString(R.string.leaderboards);
                replaceFragment(new LeaderboardFragment(), text);
                break;
            case R.id.projects:
                text = getString(R.string.projects_title);
                replaceFragment(new ProjectsFragment(), text);
                break;
            case R.id.settings:
                text = getString(R.string.settings_title);
                replaceFragment(new SettingsFragment(), text);
                break;
            default:
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    private void replaceFragment(Fragment fragment, String text) {
        getSupportActionBar().setTitle(text);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Constants.FA_ITEM_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, text);
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, Constants.FA_CATEGORY_FRAGMENT);
        if (firebaseAnalytics != null) {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    public String getWakatimeUid() {
        return basicUserData.getId();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                LeaderContract.LeaderEntry.buildProfileUri(getWakatimeUid()),
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.moveToFirst()) {
                int rank = data.getInt(data.getColumnIndex(LeaderContract.LeaderEntry.COLUMN_RANK));
                int dailyAverage = data.getInt(data.getColumnIndex(LeaderContract.LeaderEntry.COLUMN_DAILY_AVERAGE));
                newAchievements |= AchievementsUtils.checkAchievements(rank, dailyAverage);
                rankText.setText(String.valueOf(rank));
            }
        }
        mutex.incrementAndGet();
        onAchievementsLoaded();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mutex = new AtomicInteger();
    }

    public void newAchievementUnlocked(long newAchievements) {
        currentAchievements |= newAchievements;
        if (achievementsDatabaseRef != null) {
            achievementsDatabaseRef.child(getWakatimeUid()).setValue(currentAchievements);
            bindHeaderViews();
        }
    }

    private void onAchievementsLoaded() {
        if (mutex.get() == 2) {
            currentAchievements |= newAchievements;
            if (!appUsageAchievementsChecked) {
                newAchievements = AchievementsUtils.checkUsageAchievements(CacheUtils.getConsecutiveDays(this));
                currentAchievements |= newAchievements;
                appUsageAchievementsChecked = true;
            }
            achievementsDatabaseRef.child(getWakatimeUid()).setValue(currentAchievements);
            bindHeaderViews();
        }
    }
}

