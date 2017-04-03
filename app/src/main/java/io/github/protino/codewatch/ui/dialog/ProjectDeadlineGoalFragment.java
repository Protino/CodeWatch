package io.github.protino.codewatch.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.GoalItem;

import static io.github.protino.codewatch.utils.Constants.PROJECT_DEADLINE_GOAL;

/**
 * @author Gurupad Mamadapur
 */

public class ProjectDeadlineGoalFragment extends DialogFragment {

    public static final String DPD_TAG = "DATE_PICKER_TAG";
    public static final String dateFormat = "MMM dd yy";
    //@formatter:off
    @BindView(R.id.spinner) Spinner spinner;
    @BindView(R.id.date) TextView date;
    //@formatter:on
    private Calendar calendar = Calendar.getInstance();
    private long deadlineDate;
    private List<String> projectNames;
    private SimpleDateFormat simpleDateFormat;
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
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_project_deadline_goal, null);
        ButterKnife.bind(this, view);

        loadProjectsData();

        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());

        deadlineDate = calendar.getTime().getTime();
        date.setText(simpleDateFormat.format(calendar.getTime()));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String projectId = getProjectId(spinner.getSelectedItem().toString());

                        GoalItem goalItem = new GoalItem(
                                projectId,
                                PROJECT_DEADLINE_GOAL,
                                deadlineDate);

                        EventBus.getDefault().post(goalItem);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ignore
                    }
                });


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item, projectNames);

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        return builder.create();
    }

    private String getProjectId(String projectName) {
        return projectIds.get(projectNames.indexOf(projectName));
    }

    private void loadProjectsData() {
        projectIds = new ArrayList<>();
        projectNames = new ArrayList<>();
        for (Map.Entry<String, String> entry : projectNameMap.entrySet()) {
            projectIds.add(entry.getKey());
            projectNames.add(entry.getValue());
        }
    }

    @OnClick(R.id.date)
    public void onDateClick() {
        //launch calendar
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(Intent.EXTRA_TEXT, deadlineDate);
        datePickerFragment.setArguments(bundle);
        datePickerFragment.show(getFragmentManager(), DPD_TAG);
    }

    @Subscribe
    public void onDateSelected(Calendar calendar) {
        deadlineDate = calendar.getTime().getTime();
        date.setText(simpleDateFormat.format(deadlineDate));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
