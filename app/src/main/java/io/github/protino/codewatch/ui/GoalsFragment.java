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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @BindView(R.id.goals_list) RecyclerView recyclerView;
    @BindView(R.id.progressBar) View progressBar;
    @BindView(R.id.add_goal) FloatingActionButton fab;
    //@formatter:on

    private View rootView;
    private GoalsAdapter goalsAdapter;
    private Context context;

    //data
    private List<GoalItem> goalItemList;
    private List<String> projectNames; //project id is irrelevant

    private DatabaseReference projectsDatabaseReference;
    private DatabaseReference goalsDatabaseReference;

    private ValueEventListener projectValueEventListener;
    private ValueEventListener goalValueEventListener;
    private ProgressDialog goalDetailProgressDialog;

    public GoalsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_goals, container, false);
        ButterKnife.bind(this, rootView);
        hideProgressBar(false);
        fab.setVisibility(View.INVISIBLE);

        context = getActivity();

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

    private void attachListeners() {
        if (goalValueEventListener == null) {
            goalValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    goalItemList = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        goalItemList.add(child.getValue(GoalItem.class));
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

    private void initializeData() {
        goalItemList = new ArrayList<>();
        projectNames = new ArrayList<>();

        String firebaseUid = CacheUtils.getFirebaseUserId(context);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        projectsDatabaseReference = firebaseDatabase.getReference()
                .child("users").child(firebaseUid).child("projects");
        goalsDatabaseReference = firebaseDatabase.getReference()
                .child("goals").child(firebaseUid);
    }

    private void setUpSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // TODO: 13-04-2017 remove redundant deletion
                deleteGoalItem(goalsAdapter.getItem(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void deleteGoalItem(String uid) {
        goalsDatabaseReference.child(uid).removeValue();
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
        goalDetailProgressDialog = new ProgressDialog(context);
        goalDetailProgressDialog.setMessage(context.getString(R.string.loading_goal_details));
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

    public void onGoalDetailsDownloaded(String dataString, GoalItem goalItem) {
        goalDetailProgressDialog.dismiss();
        Bundle bundle = new Bundle();
        bundle.putString(GOAL_ITEM_KEY, new Gson().toJson(goalItem, GoalItem.class));
        bundle.putString(GOAL_DATA_KEY, dataString);

        GoalsDetailFragment fragment = new GoalsDetailFragment();
        fragment.setOnDeleteListener(this);
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.add(android.R.id.content, fragment).addToBackStack(null).commit();
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

    private void hideProgressBar(Boolean hide) {
        progressBar.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
        recyclerView.setVisibility(hide ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onDeleteSelected(GoalItem goalItem) {
        deleteGoalItem(goalItem.getUid());
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
                    for (Language language : data.getLanguages()) {
                        if (language.getName().equals(languageName)) {
                            timeSpentList.add(i++, language.getTotalSeconds());
                        }
                    }
                }
                Timber.d("Parse time - " + (System.currentTimeMillis() - start));
                return timeSpentList;
            } catch (IOException e) {
                Timber.e(e);
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
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Integer> list) {
            ProjectGoal projectGoal = new ProjectGoal();
            projectGoal.setProjectName(goalItem.getName());
            projectGoal.setDaily((int) goalItem.getData());
            projectGoal.setProgressSoFar(list);
            String dataString = new Gson().toJson(projectGoal, ProjectGoal.class);
            onGoalDetailsDownloaded(dataString, goalItem);
        }
    }
}
