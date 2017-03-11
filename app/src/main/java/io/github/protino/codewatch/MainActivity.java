package io.github.protino.codewatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.Gson;

import java.io.IOException;

import io.github.protino.codewatch.remote.Constants;
import io.github.protino.codewatch.remote.FetchWakatimeData;
import io.github.protino.codewatch.remote.model.WakatimeData;
import timber.log.Timber;

import static io.github.protino.codewatch.remote.Constants.WAKATIME_DATA;

public class MainActivity extends AppCompatActivity {


    //Lifecycle start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
//Lifecycle end

    public void fetchData(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getString(WAKATIME_DATA, null) == null) {
            new FetchWakatimeDataTask(this).execute();
        }
    }

    private class FetchWakatimeDataTask extends AsyncTask<Void, Void, Void> {

        private Context context;

        public FetchWakatimeDataTask(Context context) {
            this.context = context;
        }

        @SuppressLint("ApplySharedPref")
        @Override
        protected Void doInBackground(Void... params) {
            Timber.i("Starting to fetch");
            WakatimeData wakatimeData;
            try {
                wakatimeData = new FetchWakatimeData(getApplicationContext()).execute();
            } catch (IOException e) {
                return null;
            }
            Timber.i("Data fetched");
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String dataString = gson.toJson(wakatimeData);
            Timber.i("Data converted");
            editor.putString(Constants.WAKATIME_DATA, dataString);
            editor.commit();
            Timber.i("Service completed successfully");
            return null;
        }
    }
}
