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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.protino.codewatch.R;
import timber.log.Timber;

/**
 * @author Gurupad Mamadapur
 */

public class ProjectDeadlineGoalFragment extends DialogFragment{

    private static final String DPD_TAG = "DATE_PICKER_TAG";
    //@formatter:off
    @BindView(R.id.spinner) Spinner spinner;
    @BindView(R.id.date) TextView date;
    //@formatter:on
    private Calendar calendar = Calendar.getInstance();
    private long deadlineDate;
    private List<String> projects;

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_project_deadline_goal, null);
        ButterKnife.bind(this, view);

        loadProjectsData();

        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yy", Locale.getDefault());

        deadlineDate = calendar.getTime().getTime();
        date.setText(simpleDateFormat.format(calendar.getTime()));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Timber.d(new Date(deadlineDate).toString() + "  " + spinner.getSelectedItem().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item, projects);

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        return builder.create();
    }

    private void loadProjectsData() {
        projects = new ArrayList<>();
        projects.add("CodeWatch");
        projects.add("CodeWatch");
        projects.add("Lego");
        projects.add("Something");
        projects.add("One");
        projects.add("Two");
        projects.add("Three");
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
}
