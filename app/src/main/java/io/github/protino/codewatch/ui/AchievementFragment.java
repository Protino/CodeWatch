package io.github.protino.codewatch.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.BadgeItem;
import io.github.protino.codewatch.ui.adapter.AchievementsAdapter;
import io.github.protino.codewatch.utils.CacheUtils;

import static io.github.protino.codewatch.utils.AchievementsUtils.createBadgeItems;
import static io.github.protino.codewatch.utils.AchievementsUtils.getUnlockedAchievements;
import static io.github.protino.codewatch.utils.AchievementsUtils.obtainBadgeMap;
import static io.github.protino.codewatch.utils.Constants.BRONZE_BADGE;
import static io.github.protino.codewatch.utils.Constants.GOLD_BADGE;
import static io.github.protino.codewatch.utils.Constants.SILVER_BADGE;

/**
 * @author Gurupad Mamadapur
 */

public class AchievementFragment extends Fragment {

    //@formatter:off
    @BindView(R.id.list_achievements) RecyclerView recyclerView;
    @State boolean isSortedByUnLocked = true;
    @State public String firebaseUserId;
    @State public String uid;
    //@formatter:on

    private Context context;
    private List<BadgeItem> badgeItemList;
    private AchievementsAdapter achievementsAdapter;

    private long currentAchievements;
    private List<Integer> unlockedAchievementsList;

    private MenuItem sortByUnlocked;
    private MenuItem sortByLocked;

    private DatabaseReference achievementsDatabase;
    private ValueEventListener achievementsChildListener;

    private Map<Integer, Pair<String, String>> bronzeBadges;
    private Map<Integer, Pair<String, String>> silverBadges;
    private Map<Integer, Pair<String, String>> goldBadges;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Icepick.saveInstanceState(this, outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: 13-04-2017 Do all database reading in the activity and read from there

        if (savedInstanceState == null) {
            firebaseUserId = CacheUtils.getFirebaseUserId(getActivity());
            uid = ((NavigationDrawerActivity) getActivity()).getWakatimeUid();
        }
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        achievementsDatabase = firebaseDatabase.getReference().child("achv").child(firebaseUserId).child(uid);
    }

    @Override
    public void onResume() {
        super.onResume();
        attachDataBaseListener();
    }

    @Override
    public void onPause() {
        detachDatabaseListeners();
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_achievements, container, false);
        context = getActivity();
        ButterKnife.bind(this, rootView);

        setUpBadgeItemData();
        achievementsAdapter = new AchievementsAdapter(context, badgeItemList);

        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(achievementsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.achievements_menu, menu);

        sortByLocked = menu.findItem(R.id.menu_sort_by_locked);
        sortByUnlocked = menu.findItem(R.id.menu_sort_by_unlocked);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by_locked:
                sortByUnLocked(false);
                sortByLocked.setChecked(true);
                achievementsAdapter.swapData(badgeItemList);
                return true;
            case R.id.menu_sort_by_unlocked:
                sortByUnLocked(true);
                sortByUnlocked.setChecked(true);
                achievementsAdapter.swapData(badgeItemList);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void attachDataBaseListener() {
        if (achievementsChildListener == null) {
            achievementsChildListener = new ValueEventListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentAchievements = (long) dataSnapshot.getValue();
                    unlockedAchievementsList = getUnlockedAchievements(currentAchievements);
                    badgeItemList = loadData();
                    achievementsAdapter.swapData(badgeItemList);
                    onOptionsItemSelected(isSortedByUnLocked ? sortByUnlocked : sortByLocked);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            achievementsDatabase.addValueEventListener(achievementsChildListener);
        }
    }

    private void detachDatabaseListeners() {
        if (achievementsChildListener != null) {
            achievementsDatabase.removeEventListener(achievementsChildListener);
            achievementsChildListener = null;
        }
    }

    private void sortByUnLocked(boolean reverse) {
        Collections.sort(badgeItemList, new Comparator<BadgeItem>() {
            @Override
            public int compare(BadgeItem o1, BadgeItem o2) {
                int item1 = o1.getIsUnlocked() ? 1 : 0;
                int item2 = o2.getIsUnlocked() ? 1 : 0;
                return item1 - item2;
            }
        });
        if (reverse) {
            Collections.reverse(badgeItemList);
        }
        isSortedByUnLocked = reverse;
    }

    private void setUpBadgeItemData() {
        badgeItemList = new ArrayList<>();
        unlockedAchievementsList = new ArrayList<>();
        goldBadges = obtainBadgeMap(context, R.array.gold_badges);
        silverBadges = obtainBadgeMap(context, R.array.silver_badges);
        bronzeBadges = obtainBadgeMap(context, R.array.bronze_badges);
    }

    private List<BadgeItem> loadData() {
        badgeItemList = new ArrayList<>();
        badgeItemList.addAll(createBadgeItems(goldBadges, unlockedAchievementsList, GOLD_BADGE));
        badgeItemList.addAll(createBadgeItems(silverBadges, unlockedAchievementsList, SILVER_BADGE));
        badgeItemList.addAll(createBadgeItems(bronzeBadges, unlockedAchievementsList, BRONZE_BADGE));
        return badgeItemList;
    }
}
