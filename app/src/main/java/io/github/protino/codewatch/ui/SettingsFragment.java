package io.github.protino.codewatch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import io.github.protino.codewatch.R;
import io.github.protino.codewatch.utils.CacheUtils;
import io.github.protino.codewatch.utils.Constants;

/**
 * @author Gurupad Mamadapur
 */


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {


    private String prefAboutPageKey;
    private String prefLogoutButtonKey;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        context = getActivity();

        prefAboutPageKey = context.getString(R.string.pref_about_key);
        prefLogoutButtonKey = context.getString(R.string.pref_logout_key);

        findPreference(prefAboutPageKey).setOnPreferenceClickListener(this);
        findPreference(prefLogoutButtonKey).setOnPreferenceClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        int extraSideMargin = (int) context.getResources().getDimension(R.dimen.extra_side_margin);
        layoutParams.setMargins(extraSideMargin, 0, extraSideMargin, 0);
        if (rootView != null) {
            rootView.setLayoutParams(layoutParams);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(prefAboutPageKey)) {
            ((NavigationDrawerActivity) getActivity()).newAchievementUnlocked(1L << Constants.CURIOUS);
            startActivity(new Intent(getActivity(), AboutActivity.class));
            return true;
        } else if (key.equals(prefLogoutButtonKey)) {
            //Clear login cache
            CacheUtils.clearLoginInfo(context);
            //Now launch on-board activity
            startActivity(new Intent(context, OnBoardActivity.class));
            //Now finish the activity
            ((NavigationDrawerActivity) context).finish();
            return true;
        }
        return false;
    }
}
