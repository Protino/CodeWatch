package io.github.protino.codewatch.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.GoalItem;
import io.github.protino.codewatch.model.firebase.LanguageGoal;
import io.github.protino.codewatch.model.firebase.ProjectGoal;
import io.github.protino.codewatch.ui.adapter.GoalsAdapter;
import io.github.protino.codewatch.ui.dialog.AddGoalFragment;

import static io.github.protino.codewatch.utils.Constants.GOAL_DATA_KEY;
import static io.github.protino.codewatch.utils.Constants.GOAL_TYPE_KEY;
import static io.github.protino.codewatch.utils.Constants.LANGUAGE_GOAL;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DAILY_GOAL;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DEADLINE_GOAL;

/**
 * @author Gurupad Mamadapur
 */

public class GoalsFragment extends Fragment implements GoalsAdapter.OnGoalItemClickListener {

    private static final String ADD_GOAL_TAG = "ADD_GOAL_TAG";
    private static final String GD_TAG = "GOAL_DETAIL_TAG";
    //@formatter:off
    @BindView(R.id.goals_list) RecyclerView recyclerView;
    //@formatter:on

    private View rootView;
    private GoalsAdapter goalsAdapter;
    private Context context;

    //data
    private List<GoalItem> goalItemList;
    private Map<String, String> projectNameMap;

    public GoalsFragment() {
        projectNameMap = new HashMap<>();

        projectNameMap.put("sdfaad378", "CodeWatch");
        projectNameMap.put("dvcsad564378", "Lego");
        projectNameMap.put("27814s", "CodeWatch");
        projectNameMap.put("98vcds", "Brink");
        projectNameMap.put("90adsu9", "Dummy");
        projectNameMap.put("weryuiw", "So");

        goalItemList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_goals, container, false);
        ButterKnife.bind(this, rootView);
        context = getActivity();

        goalsAdapter = new GoalsAdapter(context, goalItemList, projectNameMap);
        goalsAdapter.setGoalItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(goalsAdapter);

        setUpSwipeToDelete();
        return rootView;
    }

    private void setUpSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                goalsAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }

    @OnClick({R.id.add_goal})
    public void addGoal() {
        AddGoalFragment addGoalFragment = new AddGoalFragment();
        Bundle bundle = new Bundle();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        bundle.putString(Intent.EXTRA_TEXT, new Gson().toJson(projectNameMap, type));
        addGoalFragment.setArguments(bundle);
        addGoalFragment.show(getActivity().getFragmentManager(), ADD_GOAL_TAG);
    }

    public LanguageGoal getLanguageGoalById(String goalId) {
        int oneHour = 60 * 60;
        return new LanguageGoal(goalId, 4,
                new int[]{
                        oneHour + 4334, oneHour * 3, oneHour * 2,
                        oneHour * 5 + 324, (int) (oneHour * 2.7), oneHour + 8947});
    }


    private ProjectGoal getProjectGoalById(String goalId) {
        int oneHour = 60 * 60;
        return new ProjectGoal(goalId, "CodeWatch", 1491982809, 1490691846, 4,
                new int[]{
                        oneHour,
                        oneHour + 4334, oneHour * 3, oneHour * 2,
                        oneHour * 5 + 324, (int) (oneHour * 2.7), oneHour + 8947});
    }

    @Override
    public void onGoalItemClicked(GoalItem goalItem) {
        String goalId = goalItem.getId();
        Bundle bundle = new Bundle();
        String objectDataString;
        int goalType;

        switch (goalItem.getType()) {
            case LANGUAGE_GOAL:
                goalType = LANGUAGE_GOAL;
                objectDataString = new Gson().toJson(getLanguageGoalById(goalId), LanguageGoal.class);
                break;
            case PROJECT_DAILY_GOAL:
                goalType = PROJECT_DAILY_GOAL;
                objectDataString = new Gson().toJson(getProjectGoalById(goalId), ProjectGoal.class);
                break;
            case PROJECT_DEADLINE_GOAL:
                goalType = PROJECT_DEADLINE_GOAL;
                objectDataString = new Gson().toJson(getProjectGoalById(goalId), ProjectGoal.class);
                break;
            default:
                throw new IllegalArgumentException("Incorrect goal item");
        }
        bundle.putInt(GOAL_TYPE_KEY, goalType);
        bundle.putString(GOAL_DATA_KEY, objectDataString);

        GoalsDetailFragment fragment = new GoalsDetailFragment();
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.add(android.R.id.content, fragment).addToBackStack(null).commit();

    }

    @Subscribe
    public void onGoalItemAdded(GoalItem goalItem) {
        goalsAdapter.addItem(goalItem);
        //check the type and store accordingly in database
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }
}
