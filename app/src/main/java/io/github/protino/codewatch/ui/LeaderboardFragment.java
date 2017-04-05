package io.github.protino.codewatch.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
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
import io.github.protino.codewatch.model.leaders.LeadersData;
import io.github.protino.codewatch.remote.FetchWakatimeData;
import io.github.protino.codewatch.ui.adapter.LeadersAdapter;
import io.github.protino.codewatch.utils.Constants;
import io.github.protino.codewatch.utils.LanguageValidator;
import io.github.protino.codewatch.utils.LeaderDbUtils;
import io.github.protino.codewatch.utils.UiUtils;
import timber.log.Timber;

/**
 * @author Gurupad Mamadapur
 */

public class LeaderboardFragment extends Fragment implements DialogInterface.OnShowListener,
        LoaderManager.LoaderCallbacks<Cursor> {


    private static final int LOADER_ID = 100;
    //@formatter:off
    @BindView(R.id.leaders_list) RecyclerView recyclerView;
    @BindArray(R.array.languages) String[] validLanguages;
    @BindView(R.id.error_text) TextView errorText;
    @BindView(R.id.progressBarLayout) View progressBarLayout;
    //@formatter:on

    private Dialog dialog;
    private View rootView;
    private Unbinder unbinder;
    private LanguageValidator validator;
    private View dialogView;
    private AutoCompleteTextView autoCompleteTextView;
    private List<DefaultLeaderItem> defaultLeaderItems;
    private List<DefaultLeaderItem> filteredLeaderItems;
    private CheckBox filteredCheckbox;
    private Context context;
    private LeadersAdapter leadersAdapter;
    private FilterState filterState;

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

        context = getContext();
        filterState = new FilterState();

        leadersAdapter = new LeadersAdapter(context, new ArrayList<>());

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
        loadResultsFromProvider();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionBarTitle("Leaderboard");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.leaders_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.load:
                new StoreToDbTask(getContext()).execute();
                return true;
            case R.id.find_me:
                //get current user id
                String sampleId = "5c93d61f-b71b-4406-8e34-f86755d5df18";
                //search in the data
                int position = leadersAdapter.getItemPositionById(sampleId);
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
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onShow(final DialogInterface dialog) {
        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    private void loadResultsFromProvider() {
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                LeaderContract.LeaderEntry.CONTENT_URI,
                Constants.LEADER_COLUMNS,
                null,
                null,
                Constants.LEADER_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            new DeserializeCursorTask().execute(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //invalidate data and the adapter
    }

    private void createLeaderDialog() {
        autoCompleteTextView =
                (AutoCompleteTextView) dialogView.findViewById(R.id.language_autocomplete);
        filteredCheckbox = (CheckBox) dialogView.findViewById(R.id.filter_rb);
        validator = new LanguageValidator(getContext(), autoCompleteTextView, validLanguages);
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
        filteredCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoCompleteTextView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void loadFilteredChanges() {
        hideLoadingScreen(false);
        if (filterState.getCurrentFilterLanguage().equals(FilterState.EMPTY)) {
            //change adapter data to use normal data
            leadersAdapter.swapData(buildDataItems(defaultLeaderItems));
            hideLoadingScreen(true);
            setActionBarTitle("Leaderboard");
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
            resultList.addAll(itemList.subList(4, itemList.size()));
        } else {
            resultList.addAll(itemList);
        }
        return resultList;
    }

    private void initializeData() {
        filterState = new FilterState();
        defaultLeaderItems = new ArrayList<>();
        filteredLeaderItems = new ArrayList<>();
    }

    public void hideLoadingScreen(boolean hide) {
        recyclerView.setVisibility(hide ? View.VISIBLE : View.GONE);
        progressBarLayout.setVisibility(hide ? View.GONE : View.VISIBLE);
        errorText.setVisibility(View.GONE);
    }

    public void displayErrorText(String text) {
        recyclerView.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.GONE);
        errorText.setText(text);
        errorText.setVisibility(View.VISIBLE);
    }

    private void setActionBarTitle(String title) {
        ((NavigationDrawerActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    private class FilterState {

        public static final String EMPTY = "";
        private String currentFilterLanguage;

        private FilterState() {
            currentFilterLanguage = EMPTY;
        }

        public String getCurrentFilterLanguage() {
            return currentFilterLanguage;
        }

        public void setCurrentFilterLanguage(String language) {
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
            hideLoadingScreen(false);
            super.onPreExecute();
        }

        @Override
        protected List<Object> doInBackground(Cursor... params) {
            long start = System.currentTimeMillis();

            data = params[0];
            initializeData();

            DefaultLeaderItem defaultLeaderItem;
            Map<String, Integer> languageMap;
            for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                defaultLeaderItem = new DefaultLeaderItem();
                defaultLeaderItem.setUserId(data.getString(Constants.COL_USER_ID));
                defaultLeaderItem.setDisplayName(data.getString(Constants.COL_DISPLAY_NAME));
                defaultLeaderItem.setTotalSeconds(data.getInt(Constants.COL_TOTAL_SECONDS));

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

            return buildDataItems(defaultLeaderItems);
        }

        @Override
        protected void onPostExecute(List<Object> result) {
            //notify changes to the adapter
            leadersAdapter.swapData(result);
            hideLoadingScreen(true);
            setActionBarTitle("Leaderboard");
        }
    }

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
            if (result.isEmpty()) {
                displayErrorText(context.getString(R.string.empty_leaderboard, filterLanguage));
            } else {
                leadersAdapter.swapData(result);
                hideLoadingScreen(true);
                recyclerView.scrollToPosition(0);
            }
        }
    }

    private class StoreToDbTask extends AsyncTask<Void, Void, Boolean> {
        private final Type typeLeadersData = new TypeToken<List<LeadersData>>() {
        }.getType();
        private Context context;
        private List<LeadersData> dataList;
        private String dataListString;


        public StoreToDbTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("ApplySharedPref")
        @Override
        protected Boolean doInBackground(Void... params) {
            FetchWakatimeData wakatimeData = new FetchWakatimeData(context);
            try {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                if (sharedPreferences.contains(Constants.PREF_LEADERBOARD_UPDATED)) {
                    dataListString = sharedPreferences.getString(Constants.PREF_LEADERBOARD_UPDATED, null);
                    dataList = new Gson().fromJson(dataListString, typeLeadersData);
                } else {
                    //Fetch data
                    dataList = wakatimeData.fetchLeaders().getData();
                    dataListString = new Gson().toJson(dataList, typeLeadersData);
                    sharedPreferences.edit().putString(Constants.PREF_LEADERBOARD_UPDATED, dataListString).commit();
                }
                //store in cv
                LeaderDbUtils.store(context, dataList);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                loadResultsFromProvider();
            }
        }
    }
}
