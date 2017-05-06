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

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.data.LeaderContract;
import io.github.protino.codewatch.model.BadgeItem;
import io.github.protino.codewatch.model.ProfileItem;
import io.github.protino.codewatch.ui.adapter.AchievementsAdapter;
import io.github.protino.codewatch.utils.AchievementsUtils;
import io.github.protino.codewatch.utils.CacheUtils;
import io.github.protino.codewatch.utils.Constants;
import io.github.protino.codewatch.utils.FormatUtils;
import timber.log.Timber;

import static io.github.protino.codewatch.utils.AchievementsUtils.getUnlockedAchievements;
import static io.github.protino.codewatch.utils.AchievementsUtils.obtainBadgeMap;

/**
 * Display profile data of the user
 *
 * @author Gurupad Mamadapur
 */

public class ProfileActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 101;
    //@formatter:off
    @BindView(R.id.avatar) ImageView avatar;
    @BindView(R.id.username) TextView userName;
    @BindView(R.id.rank) TextView rank;
    @BindView(R.id.daily_average_text) TextView dailyAverage;
    @BindView(R.id.email) TextView email;
    @BindView(R.id.website) TextView website;
    @BindView(R.id.location) TextView location;

    @BindView(R.id.list_languages) RecyclerView languageListView;
    @BindView(R.id.list_achievements) RecyclerView achievementsListView;
    @BindView(R.id.card_achievements) View achievementsContainer;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    //@formatter:on

    private String userId;
    private ProfileItem profileItem;
    private DatabaseReference achievementDatabaseRef;
    private ValueEventListener valueEventListener;
    private List<Integer> unlockedAchievements;

    private LanguageAdapter languageAdapter;
    private AchievementsAdapter achievementsAdapter;
    private List<BadgeItem> badgeItemList;
    private List<Pair<String, Integer>> languageList;
    private Map<Integer, Pair<String, String>> goldBadges;
    private Map<Integer, Pair<String, String>> bronzeBadges;
    private Map<Integer, Pair<String, String>> silverBadges;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        userId = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        if (userId == null) {
            throw new IllegalArgumentException("User-id not passed");
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        userId = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        collapsingToolbarLayout.setTitle("");
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        initializeData();

        achievementsAdapter = new AchievementsAdapter(this, badgeItemList);
        languageAdapter = new LanguageAdapter(this, languageList);

        //setup recycler views and adapters
        languageListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        languageListView.setAdapter(languageAdapter);

        achievementsListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        achievementsListView.setAdapter(achievementsAdapter);


        String firebaseUserId = CacheUtils.getFirebaseUserId(this);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        achievementDatabaseRef = firebaseDatabase.getReference()
                .child("achv").child(firebaseUserId).child(userId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachReadListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                LeaderContract.LeaderEntry.buildProfileUri(userId),
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.moveToFirst()) {
                profileItem.setName(data.getString(
                        data.getColumnIndex(LeaderContract.LeaderEntry.COLUMN_DISPLAY_NAME)));
                profileItem.setDailyAverage(data.getInt(
                        data.getColumnIndex(LeaderContract.LeaderEntry.COLUMN_DAILY_AVERAGE)));
                profileItem.setLocation(data.getString(
                        data.getColumnIndex(LeaderContract.LeaderEntry.COLUMN_LOCATION)));
                profileItem.setWebsite(data.getString(
                        data.getColumnIndex(LeaderContract.LeaderEntry.COLUMN_WEBSITE)));
                profileItem.setEmail(data.getString(
                        data.getColumnIndex(LeaderContract.LeaderEntry.COLUMN_EMAIL)));
                profileItem.setLanguageStats(data.getString(
                        data.getColumnIndex(LeaderContract.LeaderEntry.COLUMN_LANGUAGE_STATS)));
                profileItem.setPhotoUrl(data.getString(
                        data.getColumnIndex(LeaderContract.LeaderEntry.COLUMN_PHOTO)));
                profileItem.setRank(data.getInt(
                        data.getColumnIndex(LeaderContract.LeaderEntry.COLUMN_RANK)));
                bindViews();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        profileItem = new ProfileItem();
    }

    private void initializeData() {
        profileItem = new ProfileItem();
        languageList = new ArrayList<>();

        badgeItemList = new ArrayList<>();
        unlockedAchievements = new ArrayList<>();
        goldBadges = obtainBadgeMap(this, R.array.gold_badges);
        silverBadges = obtainBadgeMap(this, R.array.silver_badges);
        bronzeBadges = obtainBadgeMap(this, R.array.bronze_badges);
    }

    private void bindViews() {
        Glide.with(this)
                .load(profileItem.getPhotoUrl())
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

        userName.setText(profileItem.getName());
        collapsingToolbarLayout.setTitle(profileItem.getName());

        rank.setText(String.valueOf(profileItem.getRank()));

        website.setText(profileItem.getWebsite());
        Linkify.addLinks(website, Linkify.WEB_URLS);
        removeViewIfNoData(website);

        email.setText(profileItem.getEmail());
        Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
        removeViewIfNoData(email);

        location.setText(profileItem.getLocation());
        removeViewIfNoData(location);

        dailyAverage.setText(getString(R.string.daily_average_format,
                FormatUtils.getFormattedTime(this, profileItem.getDailyAverage())));

        JSONObject languageMap;
        try {
            languageMap = new JSONObject(profileItem.getLanguageStats());
            Iterator<?> keys = languageMap.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                languageList.add(new Pair<>(key, languageMap.getInt(key)));
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        languageAdapter.swapData(languageList);
    }

    private void removeViewIfNoData(TextView view) {
        String text = view.getText().toString();
        if (text.isEmpty() || text.contentEquals("null")) {
            view.setVisibility(View.GONE);
        }
    }

    private void attachReadListener() {
        if (valueEventListener == null) {
            valueEventListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object data = dataSnapshot.getValue();
                    if (data == null) {
                        achievementsContainer.setVisibility(View.GONE);
                        return;
                    }
                    long achievements = (long) data;
                    unlockedAchievements = getUnlockedAchievements(achievements);
                    profileItem.setUnlockedAchievements(unlockedAchievements);
                    List<BadgeItem> allBadges = new ArrayList<>();
                    allBadges.addAll(AchievementsUtils.createBadgeItems(goldBadges, unlockedAchievements, Constants.GOLD_BADGE));
                    allBadges.addAll(AchievementsUtils.createBadgeItems(silverBadges, unlockedAchievements, Constants.SILVER_BADGE));
                    allBadges.addAll(AchievementsUtils.createBadgeItems(bronzeBadges, unlockedAchievements, Constants.BRONZE_BADGE));

                    //add unlocked badges to badgeItemList
                    badgeItemList = new ArrayList<>();
                    for (BadgeItem badgeItem : allBadges) {
                        if (badgeItem.getIsUnlocked()) {
                            badgeItemList.add(badgeItem);
                        }
                    }
                    if (badgeItemList.size() == 0) {
                        achievementsContainer.setVisibility(View.GONE);
                    } else {
                        achievementsContainer.setVisibility(View.VISIBLE);
                        achievementsAdapter.swapData(badgeItemList);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        achievementDatabaseRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void detachReadListener() {
        if (valueEventListener != null) {
            achievementDatabaseRef.removeEventListener(valueEventListener);
        }
    }

    public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

        private Context context;
        private List<Pair<String, Integer>> dataList;

        public LanguageAdapter(Context context, List<Pair<String, Integer>> dataList) {
            this.context = context;
            this.dataList = dataList;
        }

        @Override
        public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
            return new LanguageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LanguageViewHolder holder, int position) {
            Pair<String, Integer> pair = dataList.get(position);
            holder.name.setText(pair.first);
            holder.totalSeconds.setText(FormatUtils.getFormattedTime(context, pair.second));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public void swapData(List<Pair<String, Integer>> languageList) {
            this.dataList = languageList;
            notifyDataSetChanged();
        }

        public class LanguageViewHolder extends RecyclerView.ViewHolder {

            //@formatter:off
            @BindView(R.id.name) TextView name;
            @BindView(R.id.time) TextView totalSeconds;
            //@formatter:on

            public LanguageViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
