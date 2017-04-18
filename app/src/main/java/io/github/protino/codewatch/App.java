package io.github.protino.codewatch;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.FirebaseDatabase;

import io.github.protino.codewatch.utils.CacheUtils;
import timber.log.Timber;

/**
 * @author Gurupad Mamadapur
 */

public class App extends Application {
    //Lifecycle start
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        MobileAds.initialize(getApplicationContext(),getString(R.string.banner_ad_unit_id));

        CacheUtils.updateAppUsage(this);
    }
    //Lifecycle end
}
