package io.github.protino.codewatch;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by Gurupad Mamadapur on 11-03-2017.
 */

public class App extends Application {
    //Lifecycle start
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
//Lifecycle end
}
