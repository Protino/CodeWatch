package io.github.protino.codewatch.remote;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import io.github.protino.codewatch.BuildConfig;
import io.github.protino.codewatch.data.LeaderContract;
import io.github.protino.codewatch.model.AccessToken;
import io.github.protino.codewatch.utils.CacheUtils;
import io.github.protino.codewatch.utils.Constants;
import timber.log.Timber;

/**
 * Fetches leaderboard data from wakatime API endpoint, parses it
 * and stores in database
 * <p>
 * Implementation - Instead of using Retrofit and gson, {@link HttpsURLConnection} is way
 * better to download the data and do JSON{@link org.json} parsing which is much faster.
 * Retrofit took almost 45s just to deserialize the data. With JSON parsing it just takes around
 * 2-3s ¯\_(⊙ ⊙)_/¯. Overall there is 600% improvement in downloading-parsing-storing.
 * </p>
 *
 * @author Gurupad Mamadapur
 */
public class FetchLeaderBoardData {

    private final Context context;

    public FetchLeaderBoardData(Context context) {
        this.context = context;
    }

    /**
     * todo : Change boolean result to an error code
     *
     * @return true, on successful completion else false
     */
    public boolean execute() {
        long start = System.currentTimeMillis();
        List<String> jsonDataList = fetchLeaderBoardJson(false);
        if (jsonDataList == null || jsonDataList.isEmpty()) {
            return false;
        }
        Timber.d("Data downloaded successfully - " + (System.currentTimeMillis() - start));
        try {
            ContentValues[] contentValues = parseLeaderDataFromJson(jsonDataList);
            storeToDb(contentValues);
        } catch (JSONException e) {
            Timber.e(e);
            return false;
        }
        return true;
    }

    /*
     * A major change to the API_ENDPOINT had been made. It now takes a parameter - "page"
     * and returns about 100 items for every query. There about 3000 users.
     *
     * I could load each page dynamically but it also needs to be stored in the database
     * (because there is requirement in rubric to use the loaders), which creates more problems
     * and there is less time now.
     *
     * todo: Remove all this when revamping the app for play store release
     */
    private List<String> fetchLeaderBoardJson(boolean fetchUserRankOnly) {
        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;

        List<String> jsonDataList = new ArrayList<>();
        try {

            final String LEADER_PATH = "leaders";
            final String API_SUFFIX = "api/v1";
            final String API_KEY = "api_key";
            final String PAGE = "page";


            Uri.Builder builder;
            String jsonStr;

            int totalPages = -1;
            int page = 1;
            do {

                builder = Uri.parse(Constants.WAKATIME_BASE_URL).buildUpon();
                builder.appendPath(API_SUFFIX)
                        .appendPath(LEADER_PATH)
                        .appendQueryParameter(API_KEY, BuildConfig.API_KEY)
                        .appendQueryParameter(PAGE, String.valueOf(page))
                        .build();

                URL url = new URL(builder.toString());

                // Create the request to Wakatime.com, and open the connection

                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
                jsonDataList.add(jsonStr);

                //parse totalpages
                if (totalPages == -1) {
                    totalPages = new JSONObject(jsonStr).getInt("total_pages");
                }
                page++;
            } while (totalPages != page);
        } catch (IOException e) {
            Timber.e(e, "IO Error");
        } catch (JSONException e) {
            Timber.e(e, "JSON error");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Timber.e(e, "Error closing stream");
                }
            }
        }
        return jsonDataList;
    }


    private ContentValues[] parseLeaderDataFromJson(List<String> dataList) throws JSONException {
        final long start = System.currentTimeMillis();

        final String ROOT_DATA = "data";
        final String RANK = "rank";
        final String ROOT_USER_DATA = "user";
        final String ROOT_STATS = "running_total";

        final String DAILY_AVERAGE = "daily_average";
        final String TOTAL_SECONDS = "total_seconds";
        final String LANGUAGE_LIST = "languages";

        //keys under languages section
        final String LANGUAGE_NAME = "name";

        //keys under user section
        final String DISPLAY_NAME = "display_name";
        final String EMAIL = "email";
        final String FULL_NAME = "full_name"; //most of time this is empty, ignoring it
        final String USER_ID = "id";
        final String LOCATION = "location";
        final String PHOTO_URL = "photo";
        final String USERNAME = "username";
        final String WEBSITE = "website";
        List<ContentValues> leaderValues = new ArrayList<>();

        for (String dataJsonStr : dataList) {
            JSONObject rootObject = new JSONObject(dataJsonStr);
            JSONArray dataArray = rootObject.getJSONArray(ROOT_DATA);

            ContentValues values;
            for (int i = 0; i < dataArray.length(); i++) {
                values = new ContentValues();

                JSONObject rootUserData = dataArray.getJSONObject(i);

                values.put(LeaderContract.LeaderEntry.COLUMN_RANK, rootUserData.getInt(RANK));

            /* User stats*/
                JSONObject runningTotal = rootUserData.getJSONObject(ROOT_STATS);
                values.put(LeaderContract.LeaderEntry.COLUMN_TOTAL_SECONDS, runningTotal.getInt(TOTAL_SECONDS));
                values.put(LeaderContract.LeaderEntry.COLUMN_DAILY_AVERAGE, runningTotal.getInt(DAILY_AVERAGE));
                // transform language list to a simple name-seconds map
                // Doing so simplifies the structure and takes less storage space
                JSONArray languageList = runningTotal.getJSONArray(LANGUAGE_LIST);
                JSONObject languageMap = new JSONObject();
                for (int j = 0; j < languageList.length(); j++) {
                    JSONObject language = languageList.getJSONObject(j);
                    languageMap.put(language.getString(LANGUAGE_NAME), language.getInt(TOTAL_SECONDS));
                }
                values.put(LeaderContract.LeaderEntry.COLUMN_LANGUAGE_STATS, languageMap.toString());

            /* User data*/
                JSONObject userData = rootUserData.getJSONObject(ROOT_USER_DATA);
                values.put(LeaderContract.LeaderEntry.COLUMN_USER_ID, userData.getString(USER_ID));
                values.put(LeaderContract.LeaderEntry.COLUMN_PHOTO, userData.getString(PHOTO_URL));
                values.put(LeaderContract.LeaderEntry.COLUMN_USER_NAME, userData.getString(USERNAME));
                values.put(LeaderContract.LeaderEntry.COLUMN_DISPLAY_NAME, userData.getString(DISPLAY_NAME));
                values.put(LeaderContract.LeaderEntry.COLUMN_LOCATION, userData.getString(LOCATION));
                values.put(LeaderContract.LeaderEntry.COLUMN_EMAIL, userData.getString(EMAIL));
                values.put(LeaderContract.LeaderEntry.COLUMN_WEBSITE, userData.getString(WEBSITE));
                leaderValues.add(values);
            }
        }
        Timber.d("Data parsed - " + (System.currentTimeMillis() - start) + "ms");

        ContentValues[] contentValuesArray = new ContentValues[leaderValues.size()];
        return leaderValues.toArray(contentValuesArray);
    }

    private void storeToDb(ContentValues[] leaderValues) {
        final long start = System.currentTimeMillis();
        //delete earlier data and then bulkInsert
        context.getContentResolver().delete(LeaderContract.LeaderEntry.CONTENT_URI, null, null);
        int rows = context.getContentResolver().bulkInsert(LeaderContract.LeaderEntry.CONTENT_URI, leaderValues);
        Timber.d("Successfully inserted " + rows + " rows - " + (System.currentTimeMillis() - start));
    }

    public int fetchUserRank() {
        AccessToken accessToken = CacheUtils.getAccessToken(context);
        if (accessToken == null) {
            return -1;
        }

        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonStr = null;
        try {

            final String LEADER_PATH = "leaders";
            final String API_SUFFIX = "api/v1";
            final String CLIENT_SECRET = "secret";
            final String APP_SECRET = "app_secret";
            final String ACCESS_TOKEN = "token";

            Uri.Builder builder;

            builder = Uri.parse(Constants.WAKATIME_BASE_URL).buildUpon();
            builder.appendPath(API_SUFFIX)
                    .appendPath(LEADER_PATH)
                    .appendQueryParameter(APP_SECRET, BuildConfig.CLIENT_ID)
                    .appendQueryParameter(CLIENT_SECRET, BuildConfig.CLIENT_SECRET)
                    .appendQueryParameter(ACCESS_TOKEN, accessToken.getAccessToken())
                    .build();

            URL url = new URL(builder.toString());

            // Create the request to Wakatime.com, and open the connection

            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a string
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return -1;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            if (buffer.length() == 0) {
                return -1;
            }
            jsonStr = buffer.toString();

            JSONObject currentUser = new JSONObject(jsonStr).getJSONObject("current_user");
            if (currentUser == null) {
                return -1;
            } else {
                return currentUser.getInt("rank");
            }

        } catch (IOException e) {
            Timber.e(e, "IO Error");
            return -1;
        } catch (JSONException e) {
            Timber.e(e, "JSON error");
            return -1;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Timber.e(e, "Error closing stream");
                }
            }
        }

    }
}
