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
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.GoalItem;

import static io.github.protino.codewatch.utils.Constants.PROJECT_DAILY_GOAL;

/**
 * DialogFragment that helps user to add a new project goal, where he/she
 * has to spend x amount of hours each day
 *
 * @author Gurupad Mamadapur
 */

public class ProjectDailyGoalFragment extends DialogFragment {
    //@formatter:off
    @BindView(R.id.spinner) Spinner spinner;
    @BindView(R.id.hours_picker) NumberPicker hoursPicker;
    private Unbinder unbinder;
    //@formatter:on

    private List<String> projectNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String mapString = getArguments().getString(Intent.EXTRA_TEXT);
            Type mapType = new TypeToken<List<String>>() {
            }.getType();
            projectNames = new Gson().fromJson(mapString, mapType);
        } catch (Exception e) {
            throw new IllegalArgumentException("Project ID-NAME map not set.");
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_add_project_daily_goal, null);
        unbinder = ButterKnife.bind(this, rootView);

        hoursPicker.setMinValue(1);
        hoursPicker.setMaxValue(24);


        builder.setView(rootView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String projectName = spinner.getSelectedItem().toString();

                        GoalItem goalItem = new GoalItem(
                                UUID.randomUUID().toString(),
                                projectName,
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

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}
