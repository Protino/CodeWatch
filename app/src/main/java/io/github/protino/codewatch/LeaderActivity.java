package io.github.protino.codewatch;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.protino.codewatch.data.LeaderContract;
import io.github.protino.codewatch.remote.FetchWakatimeData;
import io.github.protino.codewatch.remote.model.leaders.Language;
import io.github.protino.codewatch.remote.model.leaders.LeadersData;
import io.github.protino.codewatch.utils.Constants;
import io.github.protino.codewatch.utils.FilterCursor;
import io.github.protino.codewatch.utils.LeaderDb;

public class LeaderActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1;
    private static final String LOG_TAG = LeaderActivity.class.getSimpleName();
    private ListView listView;
    private LeaderListAdapter listAdapter;
    private Cursor cursor;

    //Lifecycle start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader);
        listView = (ListView) findViewById(R.id.leaders_list);
    }
//Lifecycle end

    public void storeInDb(View view) {
        //new StoreToDbTask(this).execute();
        loadResultsFromProvider();
    }

    private void loadResultsFromProvider() {
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                LeaderContract.LeaderEntry.CONTENT_URI,
                Constants.LEADER_COLUMNS,
                null,
                null,
                Constants.LEADER_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            cursor = data;
            listAdapter = new LeaderListAdapter(this, data, true);
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
                    String user_id = cursor.getString(Constants.COL_USER_ID);
                    Cursor profileCursor = getContentResolver().query(
                            LeaderContract.LeaderEntry.buildProfileUri(user_id),
                            null,
                            null,
                            null,
                            null);
                    if (profileCursor != null) {
                        int col_id = profileCursor.getColumnIndex(LeaderContract.LeaderEntry.COLUMN_LANGUAGE_STATS);
                        profileCursor.moveToFirst();
                        Log.d(LOG_TAG, "onItemClick: " + profileCursor.getString(col_id));
                        profileCursor.close();
                    }
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listAdapter.swapCursor(null);
    }

    public void filterJava(View view) {
        //get the type of language and filter the original cursor
        List<Integer> filterMap = new ArrayList<>();
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String languageMapString = cursor.getString(Constants.COL_LANGUAGE_STATS);
                if (languageMapString.contains("\"Java\"")) {
                    filterMap.add(cursor.getPosition());
                }
            }
            cursor.moveToFirst();
            listAdapter.swapCursor(new FilterCursor(cursor, filterMap));
        }
    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    private class StoreToDbTask extends AsyncTask<Void, Void, Boolean> {
        private final Type typeLeadersData = new TypeToken<List<LeadersData>>() {
        }.getType();
        private final Type typeHashMap = new TypeToken<HashMap<String, Integer>>() {
        }.getType();
        private Context context;
        private List<LeadersData> dataList;
        private List<Language> languages;
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
                if (sharedPreferences.contains(Constants.LEADERBOARD_UPDATED)) {
                    dataListString = sharedPreferences.getString(Constants.LEADERBOARD_UPDATED, null);
                    dataList = new Gson().fromJson(dataListString, typeLeadersData);
                } else {
                    //Fetch data
                    dataList = wakatimeData.fetchLeaders().getData();
                    dataListString = new Gson().toJson(dataList, typeLeadersData);
                    sharedPreferences.edit().putString(Constants.LEADERBOARD_UPDATED, dataListString).commit();
                }
                //store in cv
                LeaderDb.store(context, dataList);
                return true;
            } catch (IOException e) {
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

    private class LeaderListAdapter extends CursorAdapter {

        public LeaderListAdapter(Context context, Cursor cursor, boolean autoRequery) {
            super(context, cursor, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.leader_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String name = cursor.getString(Constants.COL_FULL_NAME);
            Integer totalSeconds = cursor.getInt(Constants.COL_TOTAL_SECONDS);
            ((TextView) view.findViewById(R.id.name)).setText(name);
            ((TextView) view.findViewById(R.id.total_seconds)).setText(String.valueOf(totalSeconds));
        }
    }
}
