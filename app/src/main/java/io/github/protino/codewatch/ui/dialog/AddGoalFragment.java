package io.github.protino.codewatch.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

import butterknife.BindArray;
import butterknife.ButterKnife;
import io.github.protino.codewatch.R;

/**
 * @author Gurupad Mamadapur
 */

public class AddGoalFragment extends DialogFragment {

    private static final String LGD_TAG = "LANGUAGE_GOAL_DIALOG_FRAGMENT";
    private static final String PDG_TAG = "PROJECT_DEADLINE_GOAL_FRAGMENT";
    private static final String PDAILY_TAG = "PROJECT_DAILY_GOAL_FRAGMENT";
    //@formatter:off
    @BindArray(R.array.goal_types) public String[] goalTypes;
    //@formatter:on
    private Bundle bundle;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String mapString = getArguments().getString(Intent.EXTRA_TEXT);
            Type mapType = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> projectNameMap = new Gson().fromJson(mapString, mapType);

            bundle = new Bundle();
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            bundle.putString(Intent.EXTRA_TEXT, new Gson().toJson(projectNameMap, type));

        } catch (Exception e) {
            throw new IllegalArgumentException("Project ID-NAME map not set.");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ButterKnife.bind(getActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_goal_type)
                .setItems(R.array.goal_types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                new LanguageGoalFragment().show(getFragmentManager(), LGD_TAG);
                                break;
                            case 1:
                                ProjectDeadlineGoalFragment fragment = new ProjectDeadlineGoalFragment();
                                fragment.setArguments(bundle);
                                fragment.show(getFragmentManager(), PDG_TAG);
                                break;
                            case 2:
                                ProjectDailyGoalFragment fragment1 = new ProjectDailyGoalFragment();
                                fragment1.setArguments(bundle);
                                fragment1.show(getFragmentManager(), PDAILY_TAG);
                                break;
                            default:
                                break;
                        }
                    }
                });
        return builder.create();
    }
}
