package io.github.protino.codewatch.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.GoalItem;

import static io.github.protino.codewatch.utils.Constants.PROJECT_DAILY_GOAL;

/**
 * @author Gurupad Mamadapur
 */

public class ProjectDailyGoalFragment extends DialogFragment {
    //@formatter:off
    @BindView(R.id.spinner) Spinner spinner;
    @BindView(R.id.hours_picker) NumberPicker hoursPicker;
    private Unbinder unbinder;
    //@formatter:on

    private List<String> projectNames;
    private List<String> projectIds;

    private Map<String, String> projectNameMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String mapString = getArguments().getString(Intent.EXTRA_TEXT);
            Type mapType = new TypeToken<Map<String, String>>() {
            }.getType();
            projectNameMap = new Gson().fromJson(mapString, mapType);
        } catch (Exception e) {
            throw new IllegalArgumentException("Project ID-NAME map not set.");
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        loadProjectsData();

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_add_project_daily_goal, null);
        unbinder = ButterKnife.bind(this, rootView);

        hoursPicker.setMinValue(1);
        hoursPicker.setMaxValue(24);


        builder.setView(rootView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String projectId = getProjectId(spinner.getSelectedItem().toString());

                        GoalItem goalItem = new GoalItem(
                                projectId,
                                PROJECT_DAILY_GOAL,
                                hoursPicker.getValue());
                        EventBus.getDefault().post(goalItem);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item, projectNames);

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        return builder.create();
    }

    private void loadProjectsData() {
        projectIds = new ArrayList<>();
        projectNames = new ArrayList<>();
        for (Map.Entry<String, String> entry : projectNameMap.entrySet()) {
            projectIds.add(entry.getKey());
            projectNames.add(entry.getValue());
        }
    }

    private String getProjectId(String projectName) {
        return projectIds.get(projectNames.indexOf(projectName));
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}
