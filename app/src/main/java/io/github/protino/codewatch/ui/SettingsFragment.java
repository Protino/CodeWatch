package io.github.protino.codewatch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import io.github.protino.codewatch.R;
import timber.log.Timber;

/**
 * @author Gurupad Mamadapur
 */


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {


    private String prefAboutPageKey;
    private String prefLogoutButtonKey;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        Preference preference = findPreference(getString(R.string.pref_about_key));
        preference.setOnPreferenceClickListener(this);

        prefAboutPageKey = getActivity().getString(R.string.pref_about_key);
        prefLogoutButtonKey = getActivity().getString(R.string.pref_logout_key);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(prefAboutPageKey)) {
            startActivity(new Intent(getActivity(), AboutActivity.class));
            return true;
        } else if (key.equals(prefLogoutButtonKey)) {
            Timber.d("Logout pressed");
            return true;
        }
        return false;
    }
}
