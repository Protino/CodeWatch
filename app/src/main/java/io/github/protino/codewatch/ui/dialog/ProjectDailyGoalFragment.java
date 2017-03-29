package io.github.protino.codewatch.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.protino.codewatch.R;
import timber.log.Timber;

/**
 * @author Gurupad Mamadapur
 */

public class ProjectDailyGoalFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {
    //@formatter:off
    @BindView(R.id.spinner) Spinner spinner;
    @BindView(R.id.hours_picker) NumberPicker hoursPicker;
    private Unbinder unbinder;
    //@formatter:on

    private List<String> projects;

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
                        Timber.d(spinner.getSelectedItem().toString() + "  " + hoursPicker.getValue());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        spinner.setOnItemSelectedListener(this);

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

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
