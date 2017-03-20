package io.github.protino.codewatch.ui;


import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.protino.codewatch.R;
import io.github.protino.codewatch.remote.model.firebase.User;
import io.github.protino.codewatch.ui.adapter.NavigationDrawerAdapter;
import io.github.protino.codewatch.utils.Constants;

/**
 * Fragment used for managing interactions and presentation of a navigation drawer.
 */
public class NavigationDrawerFragment extends Fragment implements NavigationDrawerAdapter.NavigationDrawerCallbacks {


    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private NavigationDrawerAdapter.NavigationDrawerCallbacks callbacks;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private int currentSelectedPosition = 0;
    private boolean isFromSavedInstanceState;
    private RecyclerView recyclerView;
    private View fragmentContainerView;
    private View rootView;
    private boolean userLearnedDrawer;
    //@formatter:on

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        userLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            currentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            isFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.drawerList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setNestedScrollingEnabled(false);

        final List<NavigationDrawerAdapter.NavigationItem> navigationItems = getMenu();
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(navigationItems);
        adapter.setNavigationDrawerCallbacks(this);
        recyclerView.setAdapter(adapter);
        selectItem(currentSelectedPosition);
        return rootView;
    }

    public boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return actionBarDrawerToggle;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        selectItem(position);
    }

    public List<NavigationDrawerAdapter.NavigationItem> getMenu() {
        List<NavigationDrawerAdapter.NavigationItem> items = new ArrayList<>();
        for (Pair<String,Integer> pair : Constants.NAVIGATION_ITEMS_ARRAY) {
            items.add(new NavigationDrawerAdapter.NavigationItem(
                    pair.first, getResources().getDrawable(pair.second)));
        }
        return items;
    }

    /**
     * Setup navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     * @param toolbar      The Toolbar of the activity.
     */
    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        fragmentContainerView = (View) getActivity().findViewById(fragmentId).getParent();
        this.drawerLayout = drawerLayout;

        this.drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(),
                NavigationDrawerFragment.this.drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                if (!userLearnedDrawer) {
                    userLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        if (!userLearnedDrawer && !isFromSavedInstanceState) {
            this.drawerLayout.openDrawer(fragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        this.drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });

        this.drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    private void selectItem(int position) {
        currentSelectedPosition = position;
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(fragmentContainerView);
        }
        if (callbacks != null) {
            callbacks.onNavigationDrawerItemSelected(position);
        }
        ((NavigationDrawerAdapter) recyclerView.getAdapter()).selectPosition(position);
    }

    public void openDrawer() {
        drawerLayout.openDrawer(fragmentContainerView);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(fragmentContainerView);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callbacks = (NavigationDrawerAdapter.NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void setCurrentUserProfileData(User user) {
        ImageView avatarContainer = (ImageView) rootView.findViewById(R.id.imgAvatar);
        ((TextView) rootView.findViewById(R.id.username)).setText(user.getDisplayName());
        String ach = String.valueOf(user.getAchievements());
        ((TextView) fragmentContainerView.findViewById(R.id.silver_badge_count)).setText(ach);
        ((TextView) rootView.findViewById(R.id.gold_badge_count)).setText(ach);
        ((TextView) rootView.findViewById(R.id.bronze_badge_count)).setText(ach);
        //avatarContainer.setImageDrawable(new RoundImage(user.getPhotoUrl()));
    }

    public View getDrawer() {
        return fragmentContainerView.findViewById(R.id.drawer_base);
    }

    public static class RoundImage extends Drawable {
        private final Bitmap bitmap;
        private final Paint paint;
        private final RectF rectF;
        private final int bitmapWidth;
        private final int bitmapHeight;

        public RoundImage(Bitmap bitmap) {
            this.bitmap = bitmap;
            rectF = new RectF();
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setDither(true);
            final BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);

            bitmapWidth = this.bitmap.getWidth();
            bitmapHeight = this.bitmap.getHeight();
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawOval(rectF, paint);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            rectF.set(bounds);
        }

        @Override
        public void setAlpha(int alpha) {
            if (paint.getAlpha() != alpha) {
                paint.setAlpha(alpha);
                invalidateSelf();
            }
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            paint.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public int getIntrinsicWidth() {
            return bitmapWidth;
        }

        @Override
        public int getIntrinsicHeight() {
            return bitmapHeight;
        }

        public void setAntiAlias(boolean aa) {
            paint.setAntiAlias(aa);
            invalidateSelf();
        }

        @Override
        public void setFilterBitmap(boolean filter) {
            paint.setFilterBitmap(filter);
            invalidateSelf();
        }

        @Override
        public void setDither(boolean dither) {
            paint.setDither(dither);
            invalidateSelf();
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

    }
}