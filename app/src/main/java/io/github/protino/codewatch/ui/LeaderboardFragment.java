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

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.data.LeaderContract;
import io.github.protino.codewatch.model.DefaultLeaderItem;
import io.github.protino.codewatch.model.TopperItem;
import io.github.protino.codewatch.remote.FetchLeaderBoardData;
import io.github.protino.codewatch.ui.adapter.LeadersAdapter;
import io.github.protino.codewatch.utils.CacheUtils;
import io.github.protino.codewatch.utils.Constants;
import io.github.protino.codewatch.utils.LanguageValidator;
import io.github.protino.codewatch.utils.NetworkUtils;
import io.github.protino.codewatch.utils.UiUtils;
import timber.log.Timber;

/**
 * @author Gurupad Mamadapur
 */

public class LeaderboardFragment extends Fragment implements DialogInterface.OnShowListener,
        LoaderManager.LoaderCallbacks<Cursor>, LeadersAdapter.OnItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int LOADER_ID = 100;
    //@formatter:off
    @BindView(R.id.leaders_list) RecyclerView recyclerView;
    @BindArray(R.array.languages) String[] validLanguages;
    @BindView(R.id.error_text) TextView errorText;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    //@formatter:on

    private Dialog dialog;
    private View rootView;
    private Unbinder unbinder;
    private LanguageValidator validator;
    private View dialogView;
    private CheckBox filteredCheckbox;
    private Context context;
    private LeadersAdapter leadersAdapter;
    private AutoCompleteTextView autoCompleteTextView;

    private List<DefaultLeaderItem> defaultLeaderItems;
    private List<DefaultLeaderItem> filteredLeaderItems;

    private FilterState filterState;
    private String userId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        context = getActivity();
        filterState = new FilterState();
        userId = CacheUtils.getWakatimeUserId(context);

        leadersAdapter = new LeadersAdapter(context, new ArrayList<>());
        leadersAdapter.setOnItemSelectedListener(this);

        //recyclerView setup
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(leadersAdapter);

        //Dialog related
        dialogView = inflater.inflate(R.layout.dialog_leaders_filter, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null);
        dialog = builder.create();
        dialog.setOnShowListener(this);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        swipeRefreshLayout.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionBarTitle(context.getString(R.string.leaderboards));
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.leaders_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.find_me:
                //search in the data
                int position = leadersAdapter.getItemPositionById(userId);
                if (position == -1) {
                    Toast.makeText(context, R.string.not_found, Toast.LENGTH_SHORT).show();
                } else {
                    //scroll to the position
                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .scrollToPositionWithOffset(position, (int) (UiUtils.getScreenHeight() * 0.4));
                }
                return true;
            case R.id.filter:
                createLeaderDialog();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onShow(final DialogInterface dialog) {
        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* If a filter checkbox is enabled and filter parameters are changed reload
                   to affect the changes
                 */

                if (filteredCheckbox.isChecked()) {
                    autoCompleteTextView.performValidation();
                    if (validator.isValidLanguage()) {
                        dialog.dismiss();
                        filterState.setCurrentFilterLanguage(autoCompleteTextView.getText().toString());
                    }
                } else {
                    dialog.dismiss();
                    filterState.setCurrentFilterLanguage(FilterState.EMPTY);
                }
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(context,
                LeaderContract.LeaderEntry.CONTENT_URI,
                Constants.LEADER_COLUMNS,
                null,
                null,
                Constants.LEADER_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            /*
             Filtering the cursor is hard and inefficient in this case
             because internally the data on which filter parameters effect are
             serialized.

             Hence deserialize the data and filter quickly
             */
            new DeserializeCursorTask().execute(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //invalidate data and the adapter
        leadersAdapter.swapData(new ArrayList<>());
        defaultLeaderItems = new ArrayList<>();
        filteredLeaderItems = new ArrayList<>();
    }


    private void initializeData() {
        filterState = new FilterState();
        defaultLeaderItems = new ArrayList<>();
        filteredLeaderItems = new ArrayList<>();
    }

    private void createLeaderDialog() {
        autoCompleteTextView =
                (AutoCompleteTextView) dialogView.findViewById(R.id.language_autocomplete);
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

        filteredCheckbox = (CheckBox) dialogView.findViewById(R.id.filter_rb);
        filteredCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoCompleteTextView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void loadFilteredChanges() {
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        if (filterState.getCurrentFilterLanguage().equals(FilterState.EMPTY)) {
            //change adapter data to use normal data
            leadersAdapter.swapData(buildDataItems(defaultLeaderItems));
            swipeRefreshLayout.setRefreshing(false);
            setActionBarTitle(context.getString(R.string.leaderboards));
            return;
        }

        String language = filterState.getCurrentFilterLanguage();
        new FilterLeaderBoardTask().execute(language);
        setActionBarTitle(language);
    }

    private List<Object> buildDataItems(List<DefaultLeaderItem> itemList) {
        List<Object> resultList = new ArrayList<>();
        if (itemList.size() >= LeadersAdapter.TOPPER_VIEW_THRESHOLD) {
            TopperItem topperItem = new TopperItem(itemList.subList(0, 3));
            resultList.add(topperItem);
            resultList.addAll(itemList.subList(3, itemList.size()));
        } else {
            resultList.addAll(itemList);
        }
        return resultList;
    }


    public void displayErrorText(String text) {
        swipeRefreshLayout.setVisibility(View.GONE);
        errorText.setText(text);
        errorText.setVisibility(View.VISIBLE);
    }

    private void setActionBarTitle(String title) {
        ActionBar actionBar = ((NavigationDrawerActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void onItemSelected(String userId) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity());
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, userId);
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onRefresh() {
        if (NetworkUtils.isNetworkUp(context)) {
            new StoreToDbTask(context).execute();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            displayErrorText(context.getString(R.string.internet_error_message));
        }
    }

    /**
     * Holds data current state of the filter - empty
     * or that there has been a change in the filterlanguage so the list
     * is reloaded to display the changes
     */
    private class FilterState {

        private static final String EMPTY = "";
        private String currentFilterLanguage;

        private FilterState() {
            currentFilterLanguage = EMPTY;
        }

        private String getCurrentFilterLanguage() {
            return currentFilterLanguage;
        }

        private void setCurrentFilterLanguage(String language) {
            if (!currentFilterLanguage.equals(language)) {
                currentFilterLanguage = language;
                loadFilteredChanges();
            }
        }
    }

    private class FocusListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getId() == R.id.language_autocomplete && !hasFocus) {
                ((AutoCompleteTextView) v).performValidation();
            }
        }
    }

    private class DeserializeCursorTask extends AsyncTask<Cursor, Void, List<Object>> {

        private Cursor data;

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
            super.onPreExecute();
        }

        @Override
        protected List<Object> doInBackground(Cursor... params) {
            long start = System.currentTimeMillis();
            try {
                data = params[0];
                initializeData();

                DefaultLeaderItem defaultLeaderItem;
                Map<String, Integer> languageMap;
                for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                    defaultLeaderItem = new DefaultLeaderItem();
                    defaultLeaderItem.setUserId(data.getString(Constants.COL_USER_ID));
                    defaultLeaderItem.setDisplayName(data.getString(Constants.COL_DISPLAY_NAME));
                    defaultLeaderItem.setTotalSeconds(data.getInt(Constants.COL_TOTAL_SECONDS));
                    defaultLeaderItem.setDailyAverage(data.getInt(Constants.COL_DAILY_AVERAGE));
                    defaultLeaderItem.setRank(data.getInt(Constants.COL_RANK));

                    languageMap = new HashMap<>();
                    String jsonData = data.getString(Constants.COL_LANGUAGE_STATS);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        Iterator<String> keys = jsonObject.keys();
                        while ((keys.hasNext())) {
                            String key = keys.next();
                            Integer totalSeconds = jsonObject.getInt(key);
                            languageMap.put(key, totalSeconds);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Timber.e(e, " Unexpected JSONException!");
                    }

                    defaultLeaderItem.setLanguageStats(languageMap);
                    defaultLeaderItem.setPhotoUrl(data.getString(Constants.COL_PHOTO_URL));
                    defaultLeaderItems.add(defaultLeaderItem);
                }
                Timber.d(String.valueOf(System.currentTimeMillis() - start));
            } catch (Exception e) {
                //ignore, probably caused by closing the fragment
                Timber.d(e);
            }

            return buildDataItems(defaultLeaderItems);
        }

        @Override
        protected void onPostExecute(List<Object> result) {
            //notify changes to the adapter
            try {
                leadersAdapter.swapData(result);
                swipeRefreshLayout.setRefreshing(false);
                setActionBarTitle(context.getString(R.string.leaderboards));
            } catch (NullPointerException e) {
                Timber.d(e);
            }
        }
    }


    /**
     * Filter the data items based on the filter language selected and sort
     * by total seconds spent on that language
     */
    private class FilterLeaderBoardTask extends AsyncTask<String, Void, List<Object>> {

        private String filterLanguage;

        @Override
        protected List<Object> doInBackground(String... params) {
            filterLanguage = params[0];
            //sort the data to fit the new changes
            long startTime = System.currentTimeMillis();
            filteredLeaderItems = new ArrayList<>();
            for (DefaultLeaderItem defaultLeaderItem : defaultLeaderItems) {
                Integer totalSeconds = defaultLeaderItem.getLanguageStats().get(filterLanguage);
                if (totalSeconds != null) {
                    defaultLeaderItem.setTotalSeconds(totalSeconds);
                    filteredLeaderItems.add(defaultLeaderItem);
                }
            }
            Timber.d(String.valueOf(System.currentTimeMillis() - startTime));
            startTime = System.currentTimeMillis();
            Collections.sort(filteredLeaderItems, new Comparator<DefaultLeaderItem>() {
                @Override
                public int compare(DefaultLeaderItem o1, DefaultLeaderItem o2) {
                    return o2.getTotalSeconds() - o1.getTotalSeconds();
                }
            });
            Timber.d(String.valueOf(System.currentTimeMillis() - startTime));

            //change adapter data
            return buildDataItems(filteredLeaderItems);
        }

        @Override
        protected void onPostExecute(List<Object> result) {
            try {
                if (result.isEmpty()) {
                    displayErrorText(context.getString(R.string.empty_leaderboard, filterLanguage));
                } else {
                    leadersAdapter.swapData(result);
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerView.scrollToPosition(0);
                }
            } catch (NullPointerException e) {
                Timber.e(e);
            }
        }
    }

    /**
     * Fetch new data from wakatime API endpoint
     * <p>
     * //Note : Due to recent changes in the API endpoint, this task takes lots of time
     * todo: --
     * Since the leaderboard is same to everyone setup a custom API endpoint on a custom
     * server that serves filtered leaderboard and also checks for achievements periodically.
     */
    private class StoreToDbTask extends AsyncTask<Void, Void, Boolean> {
        private Context context;

        private StoreToDbTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            FetchLeaderBoardData fetchLeaderBoardData = new FetchLeaderBoardData(context);
            return fetchLeaderBoardData.execute();
        }

        // TODO: 20-05-2017 Change boolean result to an error code
        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                Snackbar.make(rootView, R.string.internet_error_message, Snackbar.LENGTH_LONG);
            }
        }
    }
}
