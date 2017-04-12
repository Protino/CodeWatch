package io.github.protino.codewatch.ui;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.protino.codewatch.OnBoardActivity;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.user.ProfileData;
import io.github.protino.codewatch.utils.AchievementsUtils;
import io.github.protino.codewatch.utils.CacheUtils;
import io.github.protino.codewatch.utils.Constants;

import static io.github.protino.codewatch.utils.Constants.BRONZE_BADGE;
import static io.github.protino.codewatch.utils.Constants.GOLD_BADGE;
import static io.github.protino.codewatch.utils.Constants.SILVER_BADGE;

/**
 * This activity handles setting up the navigation drawer
 * fragment
 *
 * @author Gurupad Mamadapur
 */

public class NavigationDrawerActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    //@formatter:off
    @BindView(R.id.toolbar) public Toolbar toolbar;
    @BindView(R.id.drawer_layout) public DrawerLayout drawerLayout;
    @BindView(R.id.navigationView) public NavigationView navigationView;
    //@formatter:on
    private ActionBarDrawerToggle drawerToggler;
    private SharedPreferences sharedPreferences;
    private boolean hasUserLearntDrawer;
    private String firebaseUid;
    private DatabaseReference databaseReference;
    private ProfileData basicUserData;
    private long currentAchievements;
    private ValueEventListener valueEventListener;

    private TextView username;
    private TextView silverBadgeCounts;
    private TextView goldBadgeCounts;
    private TextView bronzeBadgeCounts;
    private ImageView avatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CacheUtils.isLoggedIn(this) || !CacheUtils.isFireBaseSetup(this)) {
            Intent intent = new Intent(this, OnBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_navigation_drawer);
        ButterKnife.bind(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        firebaseUid = sharedPreferences.getString(Constants.PREF_FIREBASE_USER_ID, null);
        String basicData = sharedPreferences.getString(Constants.PREF_BASIC_USER_DETAILS, null);
        basicUserData = new Gson().fromJson(basicData, ProfileData.class);

        setSupportActionBar(toolbar);
        setupDrawer();
        if (savedInstanceState == null) {
            MenuItem item = navigationView.getMenu().findItem(R.id.dashboard);
            onNavigationItemSelected(item);
            item.setChecked(true);
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("achv").child(firebaseUid);
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


    private void attachValueEventListener() {
        if (valueEventListener == null) {
            valueEventListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentAchievements = (long) dataSnapshot.child(basicUserData.getId()).getValue();
                    bindHeaderViews();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
        }
        databaseReference.addValueEventListener(valueEventListener);
    }

    private void detachValueEventListener() {
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
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

        silverBadgeCounts = (TextView) navigationHeader.findViewById(R.id.silver_badge_count);
        goldBadgeCounts = (TextView) navigationHeader.findViewById(R.id.gold_badge_count);
        bronzeBadgeCounts = (TextView) navigationHeader.findViewById(R.id.bronze_badge_count);


        navigationHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NavigationDrawerActivity.this, ProfileActivity.class));
            }
        });
    }

    private void bindHeaderViews() {
        username.setText(basicUserData.getDisplayName());
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
                text = "Dashboard";
                replaceFragment(new DashboardFragment(), text);
                break;
            case R.id.goals:
                text = "Goals";
                replaceFragment(new GoalsFragment(), text);
                break;
            case R.id.achievements:
                text = "Achievements";
                replaceFragment(new AchievementFragment(), text);
                break;
            case R.id.leaderboards:
                text = "Leaderboards";
                replaceFragment(new LeaderboardFragment(), text);
                break;
            case R.id.projects:
                text = "Projects";
                replaceFragment(new ProjectsFragment(), text);
                break;
            case R.id.settings:
                text = "Settings";
                replaceFragment(new SettingsFragment(), text);
                break;
            default:
                throw new UnsupportedOperationException("Invalid menu item");
        }
        drawerLayout.closeDrawers();
        return true;
    }

    private void replaceFragment(Fragment fragment, String text) {
        getSupportActionBar().setTitle(text);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
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
}
