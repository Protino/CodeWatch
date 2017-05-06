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

package io.github.protino.codewatch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import io.github.protino.codewatch.R;
import io.github.protino.codewatch.utils.CacheUtils;
import io.github.protino.codewatch.utils.Constants;

/**
 * Handles user preferences of the application and displays about section and logout button
 *
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

            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity());
            ((NavigationDrawerActivity) getActivity()).newAchievementUnlocked(1L << Constants.CURIOUS);
            startActivity(new Intent(getActivity(), AboutActivity.class), options.toBundle());
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
