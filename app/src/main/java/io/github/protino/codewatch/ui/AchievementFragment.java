package io.github.protino.codewatch.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.BadgeItem;
import io.github.protino.codewatch.ui.adapter.AchievementsAdapter;
import timber.log.Timber;

import static io.github.protino.codewatch.utils.AchievementsUtils.createBadgeItems;
import static io.github.protino.codewatch.utils.AchievementsUtils.getUnlockedAchievements;
import static io.github.protino.codewatch.utils.AchievementsUtils.obtainBadgeMap;

/**
 * @author Gurupad Mamadapur
 */

public class AchievementFragment extends Fragment {

    //@formatter:off
    @BindView(R.id.list_achievements) RecyclerView recyclerView;
    //@formatter:on

    private Context context;
    private List<BadgeItem> badgeItemList;
    private AchievementsAdapter achievementsAdapter;

    private long currentAchievements;
    private List<Integer> unlockedAchievementsList;

    private MenuItem sortByUnlocked;
    private MenuItem sortByLocked;

    private DatabaseReference achievementsDatabase;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ValueEventListener achievementsChildListener;

    private Map<Integer, Pair<String, String>> bronzeBadges;
    private Map<Integer, Pair<String, String>> silverBadges;
    private Map<Integer, Pair<String, String>> goldBadges;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        achievementsDatabase = firebaseDatabase.getReference().child("achv");
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    onSignedInInitialize(firebaseUser.getUid());
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onPause() {
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
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
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));
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
                sortByLocked(false);
                achievementsAdapter.swapData(badgeItemList);
                sortByLocked.setChecked(true);
                return true;
            case R.id.menu_sort_by_unlocked:
                sortByLocked(true);
                sortByUnlocked.setChecked(true);
                achievementsAdapter.swapData(badgeItemList);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void onSignedInInitialize(String uid) {
        userId = uid;
        attachDataBaseListener();
    }

    private void attachDataBaseListener() {
        if (achievementsChildListener == null) {
            achievementsChildListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentAchievements = (long) dataSnapshot.getValue();
                    unlockedAchievementsList = getUnlockedAchievements(currentAchievements);
                    badgeItemList = loadData();
                    achievementsAdapter.swapData(badgeItemList);
                    Timber.d(unlockedAchievementsList.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            achievementsDatabase.child(userId).child("data")
                    .addValueEventListener(achievementsChildListener);
        }
    }

    private void detachDatabaseListeners() {
        if (achievementsChildListener != null) {
            achievementsDatabase.removeEventListener(achievementsChildListener);
            achievementsChildListener = null;
        }
    }

    private void sortByLocked(boolean reverse) {
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
        badgeItemList.addAll(createBadgeItems(goldBadges, unlockedAchievementsList));
        badgeItemList.addAll(createBadgeItems(silverBadges, unlockedAchievementsList));
        badgeItemList.addAll(createBadgeItems(bronzeBadges, unlockedAchievementsList));
        return badgeItemList;
    }
}
