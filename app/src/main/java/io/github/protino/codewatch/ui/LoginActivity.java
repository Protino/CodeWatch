package io.github.protino.codewatch.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import io.github.protino.codewatch.BuildConfig;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.event.LoginEvent;
import io.github.protino.codewatch.remote.interfaces.ApiInterface;
import io.github.protino.codewatch.remote.retrofit.ServiceGenerator;
import io.github.protino.codewatch.remote.model.AccessToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.github.protino.codewatch.utils.Constants.ACCESS_CODE_PREF_KEY;
import static io.github.protino.codewatch.utils.Constants.ACCESS_TOKEN_PREF_KEY;
import static io.github.protino.codewatch.utils.Constants.REDIRECT_URI;
import static io.github.protino.codewatch.utils.Constants.SCHEME;
import static io.github.protino.codewatch.utils.Constants.STATE_PREF_KEY;

public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private String originalHashState;
    private LoginEvent loginEvent = new LoginEvent();

    public LoginActivity() {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            String random = String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
            originalHashState = String.valueOf(Arrays.hashCode(messageDigest.digest(random.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            Log.e(LOG_TAG, "MainActivity: ", e);
        }
    }

    //Lifecycle start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null && intent.getScheme().equals(SCHEME)) {
            fetchAccessToken(intent);
        } else {
        /*Build authorization url */
            Uri loginUrl = Uri.parse(ServiceGenerator.BASE_URL)
                    .buildUpon()
                    .appendPath("oauth")
                    .appendPath("authorize")
                    .appendQueryParameter("client_id", BuildConfig.CLIENT_ID)
                    .appendQueryParameter("response_type", "code")
                    .appendQueryParameter("redirect_uri", REDIRECT_URI)
                    .appendQueryParameter("scope", buildScopeList())
                    .appendQueryParameter("state", originalHashState)
                    .build();

            /*Store state to verify later*/
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putString(STATE_PREF_KEY, originalHashState);
            sharedPreferencesEditor.apply();

            /* Launch browser */
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, loginUrl)
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                            | Intent.FLAG_ACTIVITY_NO_HISTORY
                            | Intent.FLAG_FROM_BACKGROUND);
            startActivity(browserIntent);
        }
    }
    //Lifecycle end

    private void fetchAccessToken(Intent intent) {
        Uri uri = intent.getData();
        String code = uri.getQueryParameter("code");
        String state = uri.getQueryParameter("state");
        originalHashState = sharedPreferences.getString(STATE_PREF_KEY, null);
        if (code != null && state != null && state.equals(originalHashState)) {
            if (state.equals(originalHashState)) {
                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putString(ACCESS_CODE_PREF_KEY, code);
                sharedPreferencesEditor.apply();
                requestAccessToken(code);
            } else {
                Log.e(LOG_TAG, "Invalid state received. Possible security attack");
                EventBus.getDefault().postSticky(loginEvent.setLoginSuccess(false));
                finish();
            }
        } else {
            Log.e(LOG_TAG, "No access_code received");
            EventBus.getDefault().postSticky(loginEvent.setLoginSuccess(false));
            finish();
        }
    }

    private void requestAccessToken(String code) {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<AccessToken> call = apiInterface.getAccessToken(
                BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET,
                REDIRECT_URI, "authorization_code", code);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                SharedPreferences.Editor sharedPreferencesEditor;
                sharedPreferencesEditor = sharedPreferences.edit();
                AccessToken accessToken = response.body();
                accessToken.setRetrievalTime((long) (System.currentTimeMillis() / 1e3));
                Log.d(LOG_TAG, "onResponse: " + accessToken.getAccessToken());
                Gson gson = new Gson();
                String gsonString = gson.toJson(accessToken);
                sharedPreferencesEditor.putString(ACCESS_TOKEN_PREF_KEY, gsonString);
                sharedPreferencesEditor.apply();
                EventBus.getDefault().postSticky(loginEvent.setLoginSuccess(true));
                finish();
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.e(LOG_TAG, "onFailure: ", t);
                EventBus.getDefault().postSticky(loginEvent.setLoginSuccess(false));
                finish();
            }
        });
    }

    private String buildScopeList() {
        String scopeList[] = {"email", "read_logged_time", "read_stats", "read_teams"};
        return TextUtils.join(",", scopeList);
    }
}
