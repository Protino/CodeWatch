package io.github.protino.codewatch.utils;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.github.protino.codewatch.data.LeaderContract;
import io.github.protino.codewatch.model.leaders.Language;
import io.github.protino.codewatch.model.leaders.LeadersData;
import io.github.protino.codewatch.model.leaders.RunningTotal;
import io.github.protino.codewatch.model.leaders.User;
import timber.log.Timber;

/**
 * Created by Gurupad Mamadapur on 17-03-2017.
 */

public class LeaderDbUtils {

    public static void store(Context context, List<LeadersData> dataList) throws JSONException {

        ContentValues[] leaderValues = new ContentValues[dataList.size()];
        ContentValues values;
        for (int i = 0; i < dataList.size(); i++) {
            values = new ContentValues();
            LeadersData leadersData = dataList.get(i);
            RunningTotal runningTotal = leadersData.getRunningTotal();
            values.put(LeaderContract.LeaderEntry.COLUMN_TOTAL_SECONDS, runningTotal.getTotalSeconds());
            values.put(LeaderContract.LeaderEntry.COLUMN_DAILY_AVERAGE, runningTotal.getDailyAverage());

            //transform language list to languageMap and then to jsonString
            List<Language> languages = runningTotal.getLanguages();
            JSONObject jsonObject = new JSONObject();
            for (Language language : languages) {
                jsonObject.put(language.getName(), language.getTotalSeconds());
            }
            values.put(LeaderContract.LeaderEntry.COLUMN_LANGUAGE_STATS, jsonObject.toString());

            User user = leadersData.getUser();
            values.put(LeaderContract.LeaderEntry.COLUMN_USER_ID, user.getId());
            values.put(LeaderContract.LeaderEntry.COLUMN_PHOTO, user.getPhoto());
            values.put(LeaderContract.LeaderEntry.COLUMN_USER_NAME, user.getUsername());
            values.put(LeaderContract.LeaderEntry.COLUMN_DISPLAY_NAME, user.getDisplayName());
            values.put(LeaderContract.LeaderEntry.COLUMN_LOCATION, user.getLocation());
            values.put(LeaderContract.LeaderEntry.COLUMN_EMAIL, user.getEmail());
            values.put(LeaderContract.LeaderEntry.COLUMN_WEBSITE, user.getWebsite());
            leaderValues[i] = values;
        }
        //delete earlier data and then bulkInsert
        context.getContentResolver().delete(LeaderContract.LeaderEntry.CONTENT_URI, null, null);
        int rows = context.getContentResolver().bulkInsert(LeaderContract.LeaderEntry.CONTENT_URI, leaderValues);
        Timber.d("Successfully inserted " + rows);
    }
}
