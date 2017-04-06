package io.github.protino.codewatch.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.firebase.User;
import io.github.protino.codewatch.utils.Constants;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        ButterKnife.bind(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setSupportActionBar(toolbar);
        setupDrawer();
        if (savedInstanceState == null) {
            MenuItem item = navigationView.getMenu().findItem(R.id.dashboard);
            onNavigationItemSelected(item);
            item.setChecked(true);
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
        User user = new User()
                .setDisplayName("Gurupad Mamadapur")
                .setAchievements(0)
                .setPhotoUrl(null);

        View navigationHeader = navigationView.inflateHeaderView(R.layout.navigation_header);

        ((TextView) navigationHeader.findViewById(R.id.username)).setText(user.getDisplayName());

        String ach = String.valueOf(user.getAchievements());
        ((TextView) navigationHeader.findViewById(R.id.silver_badge_count)).setText(ach);
        ((TextView) navigationHeader.findViewById(R.id.gold_badge_count)).setText(ach);
        ((TextView) navigationHeader.findViewById(R.id.bronze_badge_count)).setText(ach);
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
                text = "Ach";
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
                break;
            default:
                throw new UnsupportedOperationException("Invalid menu item");
        }
        drawerLayout.closeDrawers();
        return true;
    }

    private void replaceFragment(Fragment fragment, String text) {
        Bundle bundle = new Bundle();
        bundle.putString(Intent.EXTRA_TEXT, text);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
