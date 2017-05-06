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

package io.github.protino.codewatch.utils;

import android.content.Context;
import android.widget.AutoCompleteTextView;

import java.util.Arrays;

import io.github.protino.codewatch.R;

/**
 * Helper class the checks if the language typed in the editText field is valid
 *
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
