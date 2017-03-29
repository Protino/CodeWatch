package io.github.protino.codewatch.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

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
                                new ProjectDeadlineGoalFragment().show(getFragmentManager(), PDG_TAG);
                                break;
                            case 2:
                                new ProjectDailyGoalFragment().show(getFragmentManager(),PDAILY_TAG);
                                break;
                            default:
                                break;
                        }
                    }
                });
        return builder.create();
    }
}
