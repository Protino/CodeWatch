package io.github.protino.codewatch.utils;

import android.content.Context;
import android.widget.AutoCompleteTextView;

import java.util.Arrays;

import io.github.protino.codewatch.R;

/**
 * @author Gurupad Mamadapur
 */

public class LanguageValidator implements AutoCompleteTextView.Validator {

    private AutoCompleteTextView autoCompleteTextView;
    private boolean validLanguage;
    private String[] validLanguages;
    private Context context;

    public LanguageValidator(Context context, AutoCompleteTextView autoCompleteTextView, String[] validLanguages) {
        this.autoCompleteTextView = autoCompleteTextView;
        this.validLanguages = validLanguages;
        this.context = context;
    }

    @Override
    public boolean isValid(CharSequence text) {
        validLanguage = Arrays.binarySearch(validLanguages, text.toString()) > -1;
        return validLanguage;
    }

    @Override
    public CharSequence fixText(CharSequence invalidText) {
        autoCompleteTextView.setError(
                String.format(context.getString(R.string.invalid_language_hint), invalidText));
        return invalidText;
    }

    public boolean isValidLanguage() {
        return validLanguage;
    }
}
