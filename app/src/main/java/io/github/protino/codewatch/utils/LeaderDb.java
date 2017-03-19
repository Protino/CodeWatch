package io.github.protino.codewatch.utils;

import android.content.ContentValues;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import io.github.protino.codewatch.data.LeaderContract;
import io.github.protino.codewatch.remote.model.leaders.Language;
import io.github.protino.codewatch.remote.model.leaders.LeadersData;
import io.github.protino.codewatch.remote.model.leaders.RunningTotal;
import io.github.protino.codewatch.remote.model.leaders.User;
import timber.log.Timber;

/**
 * Created by Gurupad Mamadapur on 17-03-2017.
 */

public class LeaderDb {

    private final static Type typeHashMap = new TypeToken<HashMap<String, Integer>>() {
    }.getType();

    public static void store(Context context, List<LeadersData> dataList) {

        ContentValues[] leaderValues = new ContentValues[dataList.size()];
        ContentValues values;
        HashMap<String, Integer> languageMap;
        for (int i = 0; i < dataList.size(); i++) {
            values = new ContentValues();
            languageMap = new HashMap<>();
            LeadersData leadersData = dataList.get(i);
            RunningTotal runningTotal = leadersData.getRunningTotal();
            values.put(LeaderContract.LeaderEntry.COLUMN_TOTAL_SECONDS, runningTotal.getTotalSeconds());
            values.put(LeaderContract.LeaderEntry.COLUMN_DAILY_AVERAGE, runningTotal.getDailyAverage());

            //transform language list to languageMap and then to gsonString
            List<Language> languages = runningTotal.getLanguages();
            for (Language language : languages) {
                languageMap.put(language.getName(), language.getTotalSeconds());
            }
            String languageMapString = new Gson().toJson(languageMap, typeHashMap);
            values.put(LeaderContract.LeaderEntry.COLUMN_LANGUAGE_STATS, languageMapString);

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
