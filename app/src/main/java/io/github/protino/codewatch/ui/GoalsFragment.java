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
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import icepick.Icepick;
import icepick.State;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.GoalItem;
import io.github.protino.codewatch.model.firebase.LanguageGoal;
import io.github.protino.codewatch.model.firebase.Project;
import io.github.protino.codewatch.model.firebase.ProjectGoal;
import io.github.protino.codewatch.model.project.summary.GenericSummaryData;
import io.github.protino.codewatch.model.project.summary.Language;
import io.github.protino.codewatch.model.project.summary.ProjectSummaryData;
import io.github.protino.codewatch.remote.FetchWakatimeData;
import io.github.protino.codewatch.ui.adapter.GoalsAdapter;
import io.github.protino.codewatch.ui.dialog.AddGoalFragment;
import io.github.protino.codewatch.utils.CacheUtils;
import io.github.protino.codewatch.utils.Constants;
import io.github.protino.codewatch.utils.NetworkUtils;
import timber.log.Timber;

import static io.github.protino.codewatch.utils.Constants.LANGUAGE_GOAL;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DAILY_GOAL;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DEADLINE_GOAL;

/**
 * @author Gurupad Mamadapur
 */

public class GoalsFragment extends Fragment implements
        GoalsAdapter.OnGoalItemClickListener, GoalsDetailFragment.OnDeleteListener {

    public static final String GOAL_DATA_KEY = "goal_data_key";
    public static final String GOAL_ITEM_KEY = "goal_item_key";
    private static final String ADD_GOAL_TAG = "ADD_GOAL_TAG";
    private static final String GD_TAG = "GOAL_DETAIL_TAG";
    //@formatter:off
    @BindView(R.id.goals_list) public RecyclerView recyclerView;
    @BindView(R.id.progressBar) public View progressBar;
    @BindView(R.id.add_goal) public FloatingActionButton fab;
    @BindBool(R.bool.isLargeDevice) public boolean isLargeDevice;
    @State public String firebaseUid;
    //@formatter:on
    private View rootView;
    private GoalsAdapter goalsAdapter;
    private Context context;

    //data
    private List<GoalItem> goalItemList;
    private Set<String> deletedGoals = new HashSet<>();
    private List<String> projectNames; //project id is irrelevant
    private DatabaseReference projectsDatabaseReference;
    private DatabaseReference goalsDatabaseReference;
    private ValueEventListener projectValueEventListener;
    private ValueEventListener goalValueEventListener;
    private ProgressDialog goalDetailProgressDialog;
    private Pair<GoalItem, Integer> tempGoalItem;
    private boolean isReverted = false;
    private Snackbar deleteSnackbar;
    private Unbinder unbinder;

    public GoalsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_goals, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        hideProgressBar(false);
        fab.setVisibility(View.INVISIBLE);

        context = getActivity();

        if (savedInstanceState == null) {
            firebaseUid = CacheUtils.getFirebaseUserId(context);
        }
        initializeData();
        goalsAdapter = new GoalsAdapter(context, goalItemList);
        goalsAdapter.setGoalItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(goalsAdapter);
        setUpSwipeToDelete();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        attachListeners();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        detachListeners();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @OnClick({R.id.add_goal})
    public void addGoal() {
        AddGoalFragment addGoalFragment = new AddGoalFragment();
        Bundle bundle = new Bundle();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        bundle.putString(Intent.EXTRA_TEXT, new Gson().toJson(projectNames, type));
        addGoalFragment.setArguments(bundle);
        addGoalFragment.show(getActivity().getFragmentManager(), ADD_GOAL_TAG);
    }

    @Override
    public void onGoalItemClicked(GoalItem goalItem) {
        if (!NetworkUtils.isNetworkUp(context)) {
            displayInternetErrorSnackBar();
            return;
        }

        goalDetailProgressDialog = new ProgressDialog(context);
        goalDetailProgressDialog.setMessage(context.getString(R.string.loading_goal_details));
        goalDetailProgressDialog.setCancelable(false);
        goalDetailProgressDialog.setCanceledOnTouchOutside(false);
        goalDetailProgressDialog.show();

        switch (goalItem.getType()) {
            case LANGUAGE_GOAL:
                new FetchTimeSpentOnLanguageTask(goalItem).execute();
                break;
            case PROJECT_DAILY_GOAL:
                new FetchTimeSpentOnProjectTask(goalItem).execute();
                break;
            case PROJECT_DEADLINE_GOAL:
                ProjectGoal projectGoal = new ProjectGoal();
                projectGoal.setProjectName(goalItem.getName());
                projectGoal.setDeadline(goalItem.getData());
                projectGoal.setStartDate(goalItem.getExtraData());
                String gsonData = new Gson().toJson(projectGoal, ProjectGoal.class);
                onGoalDetailsDownloaded(gsonData, goalItem);
                break;
            default:
                throw new IllegalArgumentException("Incorrect goal item");
        }
    }

    @Subscribe
    public void onGoalItemAdded(GoalItem goalItem) {
        if (goalsAdapter.isDuplicate(goalItem)) {
            Toast.makeText(context, R.string.duplicate_goal_message, Toast.LENGTH_SHORT).show();
            return;
        }
        goalsAdapter.addItem(goalItem);
        goalsDatabaseReference.child(goalItem.getUid()).setValue(goalItem);

        //set on track achievement
        ((NavigationDrawerActivity) getActivity()).newAchievementUnlocked(1L << Constants.FOCUSED);
    }

    @Override
    public void onDeleteSelected(GoalItem goalItem) {
        deleteGoalItem(goalItem.getUid());
    }

    private void initializeData() {
        goalItemList = new ArrayList<>();
        projectNames = new ArrayList<>();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        projectsDatabaseReference = firebaseDatabase.getReference()
                .child("users").child(firebaseUid).child("projects");
        goalsDatabaseReference = firebaseDatabase.getReference()
                .child("goals").child(firebaseUid);
    }

    private void attachListeners() {
        if (goalValueEventListener == null) {
            goalValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    goalItemList = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        GoalItem goalItem = child.getValue(GoalItem.class);
                        if (!deletedGoals.contains(goalItem.getUid())) {
                            goalItemList.add(goalItem);
                        }
                    }
                    goalsAdapter.swapData(goalItemList);
                    hideProgressBar(true);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.e(databaseError.toException(), databaseError.getMessage());
                }
            };
        }
        if (projectValueEventListener == null) {
            projectValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    projectNames = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Project project = child.getValue(Project.class);
                        projectNames.add(project.getName());
                    }
                    fab.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.e(databaseError.toException(), databaseError.getMessage());
                }

            };
        }
        goalsDatabaseReference.addValueEventListener(goalValueEventListener);
        projectsDatabaseReference.addValueEventListener(projectValueEventListener);
    }

    private void detachListeners() {
        if (projectValueEventListener != null) {
            projectsDatabaseReference.removeEventListener(projectValueEventListener);
            projectValueEventListener = null;
        }
        if (goalValueEventListener != null) {
            goalsDatabaseReference.removeEventListener(goalValueEventListener);
            goalValueEventListener = null;
        }
    }

    private void setUpSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                deleteGoalItem(goalsAdapter.getItem(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void deleteGoalItem(final String uid) {
        deletedGoals.add(uid);
        tempGoalItem = goalsAdapter.getItemByUid(uid);

        //temporary delete
        goalsAdapter.deleteItem(tempGoalItem.second);

        isReverted = false;
        if (deleteSnackbar != null) {
            isReverted = false;
            deleteSnackbar.dismiss();
        }
        deleteSnackbar = Snackbar.make(rootView, R.string.goal_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //revert the changes
                        goalsAdapter.addItem(tempGoalItem.second, tempGoalItem.first);
                        isReverted = true;
                        deletedGoals.remove(uid);
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        //permanent deletion, if not reverted
                        if (!isReverted) {
                            goalsDatabaseReference.child(uid).removeValue();
                        }
                        super.onDismissed(snackbar, event);
                    }
                });
        deleteSnackbar.show();
    }

    private void hideProgressBar(Boolean hide) {
        progressBar.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
        recyclerView.setVisibility(hide ? View.VISIBLE : View.INVISIBLE);
    }

    private void displayInternetErrorSnackBar() {
        Snackbar.make(rootView, R.string.internet_error_message, Snackbar.LENGTH_LONG).show();
    }

    private void onGoalDetailsDownloaded(String dataString, GoalItem goalItem) {
        goalDetailProgressDialog.dismiss();
        Bundle bundle = new Bundle();
        bundle.putString(GOAL_ITEM_KEY, new Gson().toJson(goalItem, GoalItem.class));
        bundle.putString(GOAL_DATA_KEY, dataString);

        GoalsDetailFragment fragment = new GoalsDetailFragment();
        fragment.setOnDeleteListener(this);
        fragment.setArguments(bundle);

        if (isLargeDevice) {
            fragment.show(getActivity().getFragmentManager(), GD_TAG);
        } else {
            FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.add(android.R.id.content, fragment).addToBackStack(null).commit();
        }
    }

    private class FetchTimeSpentOnLanguageTask extends AsyncTask<Void, Void, List<Integer>> {

        private GoalItem goalItem;

        private FetchTimeSpentOnLanguageTask(GoalItem goalItem) {
            this.goalItem = goalItem;
        }

        @Override
        protected List<Integer> doInBackground(Void... params) {
            String languageName = goalItem.getName();
            FetchWakatimeData fetchWakatimeData = new FetchWakatimeData(context);
            List<Integer> timeSpentList = new ArrayList<>();
            try {
                long start = System.currentTimeMillis();
                List<GenericSummaryData> dataList = fetchWakatimeData.fetchGenericSummaryResponse().getData();
                Timber.d("Download time - " + (System.currentTimeMillis() - start));
                int i = 0;
                start = System.currentTimeMillis();
                for (GenericSummaryData data : dataList) {
                    if (data.getLanguages() == null || data.getLanguages().size() == 0) {
                        timeSpentList.add(i++, 0);
                        continue;
                    }
                    for (Language language : data.getLanguages()) {
                        if (language.getName().equals(languageName)) {
                            timeSpentList.add(i++, language.getTotalSeconds());
                        }
                    }
                }
                Timber.d("Parse time - " + (System.currentTimeMillis() - start));
                return timeSpentList;
            } catch (IOException e) {
                FirebaseCrash.report(e);
                return null;
            } catch (Exception e) {
                FirebaseCrash.report(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Integer> list) {
            if (list != null) {
                LanguageGoal languageGoal = new LanguageGoal();
                languageGoal.setGoalId(goalItem.getName());
                languageGoal.setDailyGoal((int) goalItem.getData());
                languageGoal.setProgressSoFar(list);
                String gsonData = new Gson().toJson(languageGoal, LanguageGoal.class);
                onGoalDetailsDownloaded(gsonData, goalItem);
            } else {
                goalDetailProgressDialog.dismiss();
                displayInternetErrorSnackBar();
            }
        }
    }

    private class FetchTimeSpentOnProjectTask extends AsyncTask<Void, Void, List<Integer>> {

        private GoalItem goalItem;

        private FetchTimeSpentOnProjectTask(GoalItem goalItem) {
            this.goalItem = goalItem;
        }

        @Override
        protected List<Integer> doInBackground(Void... params) {
            String projectName = goalItem.getName();
            List<Integer> timeSpentList = new ArrayList<>();
            FetchWakatimeData fetchWakatimeData = new FetchWakatimeData(context);
            try {
                List<ProjectSummaryData> dataList = fetchWakatimeData.fetchProjectSummary(projectName).getData();
                for (ProjectSummaryData summaryData : dataList) {
                    timeSpentList.add(summaryData.getGrandTotal().getTotalSeconds());
                }
                return timeSpentList;
            } catch (IOException e) {
                FirebaseCrash.report(e);
                return null;
            } catch (Exception e) {
                FirebaseCrash.report(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Integer> list) {
            if (list != null) {
                ProjectGoal projectGoal = new ProjectGoal();
                projectGoal.setProjectName(goalItem.getName());
                projectGoal.setDaily((int) goalItem.getData());
                projectGoal.setProgressSoFar(list);
                String dataString = new Gson().toJson(projectGoal, ProjectGoal.class);
                onGoalDetailsDownloaded(dataString, goalItem);
            } else {
                displayInternetErrorSnackBar();
                goalDetailProgressDialog.dismiss();
            }
        }
    }
}
