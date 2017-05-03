package io.github.protino.codewatch.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.NumberPicker;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;
import icepick.State;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.GoalItem;
import io.github.protino.codewatch.utils.LanguageValidator;

import static io.github.protino.codewatch.utils.Constants.LANGUAGE_GOAL;

/**
 * @author Gurupad Mamadapur
 */

public class LanguageGoalFragment extends DialogFragment implements DialogInterface.OnShowListener {

    //@formatter:off
    @State public int hoursPicked = 1;
    @BindView(R.id.language_autocomplete) public AutoCompleteTextView autoCompleteTextView;
    @BindView(R.id.hours_picker) public NumberPicker hoursPicker;
    @BindArray(R.array.languages) public String[] validLanguages;
    private Unbinder unbinder;
    private LanguageValidator validator;
    //@formatter:on


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this,savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this,outState);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_add_language_goal, null);
        unbinder = ButterKnife.bind(this, rootView);

        autoCompleteTextView.setAdapter(new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_dropdown_item_1line, validLanguages));
        validator = new LanguageValidator(getActivity(), autoCompleteTextView, validLanguages);
        autoCompleteTextView.setValidator(validator);
        autoCompleteTextView.setOnFocusChangeListener(new FocusListener());
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.showDropDown();
            }
        });

        hoursPicker.setMinValue(1);
        hoursPicker.setMaxValue(24);
        hoursPicker.setValue(hoursPicked);


        builder.setView(rootView)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(this);

        return dialog;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onShow(DialogInterface dialog) {
        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                autoCompleteTextView.performValidation();
                if (validator.isValidLanguage()) {
                    GoalItem goalItem = new GoalItem(
                            UUID.randomUUID().toString(),
                            autoCompleteTextView.getText().toString(),
                            LANGUAGE_GOAL,
                            hoursPicker.getValue());
                    EventBus.getDefault().post(goalItem);
                    dismiss();
                }
            }
        });
    }

    private class FocusListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getId() == R.id.language_autocomplete && !hasFocus) {
                ((AutoCompleteTextView) v).performValidation();
            }
        }
    }
}
