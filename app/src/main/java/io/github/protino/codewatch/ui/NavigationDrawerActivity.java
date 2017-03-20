package io.github.protino.codewatch.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.remote.model.firebase.User;
import io.github.protino.codewatch.ui.adapter.NavigationDrawerAdapter;
import timber.log.Timber;

/**
 * This activity handles setting up the navigation drawer
 * fragment
 *
 * @author Gurupad Mamadapur
 */

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationDrawerAdapter.NavigationDrawerCallbacks {

    //@formatter:off
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer) DrawerLayout drawerLayout;
    //@formatter:on
    private NavigationDrawerFragment navigationDrawerFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        navigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Set up the drawer.
        navigationDrawerFragment.setup(R.id.fragment_drawer, drawerLayout, toolbar);

        User user = new User()
                .setDisplayName("Gurupad Mamadapur")
                .setAchievements(0)
                .setPhotoUrl(null);
        // populate the navigation drawer.
        navigationDrawerFragment.setCurrentUserProfileData(user);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Timber.d(String.valueOf(position));
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment.isDrawerOpen()) {
            navigationDrawerFragment.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }


}
