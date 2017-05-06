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
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.GoalItem;

import static io.github.protino.codewatch.utils.Constants.PROJECT_DEADLINE_GOAL;

/**
 * DialogFragment that helps user add a new deadline goal on a project
 *
 * @author Gurupad Mamadapur
 */

public class ProjectDeadlineGoalFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public static final String DPD_TAG = "DATE_PICKER_TAG";
    public static final String dateFormat = "MMM dd yy";
    @State
    public long deadlineDate = -1;
    //@formatter:off
    @BindView(R.id.spinner) Spinner spinner;
    @BindView(R.id.date) TextView date;
    //@formatter:on
    private Calendar calendar = Calendar.getInstance();
    private List<String> projectNames;
    private SimpleDateFormat simpleDateFormat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        try {
            String dataString = getArguments().getString(Intent.EXTRA_TEXT);
            Type type = new TypeToken<List<String>>() {
            }.getType();
            projectNames = new Gson().fromJson(dataString, type);
        } catch (Exception e) {
            throw new IllegalArgumentException("Project names list not set.");
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Icepick.saveInstanceState(this, outState);
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_project_deadline_goal, null);
        ButterKnife.bind(this, view);

        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());

        if (savedInstanceState == null) {
            deadlineDate = calendar.getTime().getTime();
            date.setText(simpleDateFormat.format(calendar.getTime()));
        } else {
            date.setText(simpleDateFormat.format(new Date(deadlineDate)));
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String name = spinner.getSelectedItem().toString();

                        GoalItem goalItem = new GoalItem(
                                UUID.randomUUID().toString(),
                                name,
                                PROJECT_DEADLINE_GOAL,
                                deadlineDate);
                        goalItem.setExtraData(System.currentTimeMillis());

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

    @OnClick(R.id.date)
    public void onDateClick() {

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(deadlineDate));

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(getActivity(),
                        this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    @Subscribe
    public void onDateSelected(Calendar calendar) {
        deadlineDate = calendar.getTime().getTime();
        date.setText(simpleDateFormat.format(deadlineDate));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        EventBus.getDefault().post(calendar);
    }
}
