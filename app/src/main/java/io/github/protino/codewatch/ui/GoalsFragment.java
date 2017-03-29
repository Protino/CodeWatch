package io.github.protino.codewatch.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.GoalItem;
import io.github.protino.codewatch.ui.adapter.GoalsAdapter;
import io.github.protino.codewatch.ui.dialog.AddGoalFragment;

/**
 * @author Gurupad Mamadapur
 */

public class GoalsFragment extends Fragment {

    private static final String ADD_GOAL_TAG = "ADD_GOAL_TAG";
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
        goalItemList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_goals, container, false);
        ButterKnife.bind(this, rootView);
        context = getContext();

        goalsAdapter = new GoalsAdapter(context, goalItemList, projectNameMap);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(goalsAdapter);
        return rootView;
    }

    @OnClick({R.id.add_goal})
    public void addGoal() {
        AddGoalFragment addGoalFragment = new AddGoalFragment();
        addGoalFragment.show(getActivity().getFragmentManager(), ADD_GOAL_TAG);
    }
}
