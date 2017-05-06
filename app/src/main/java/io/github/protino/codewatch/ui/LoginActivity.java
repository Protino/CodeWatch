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

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import io.github.protino.codewatch.BuildConfig;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.event.LoginEvent;
import io.github.protino.codewatch.model.AccessToken;
import io.github.protino.codewatch.remote.interfaces.ApiInterface;
import io.github.protino.codewatch.remote.retrofit.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.github.protino.codewatch.utils.Constants.PREF_ACCESS_CODE;
import static io.github.protino.codewatch.utils.Constants.PREF_ACCESS_TOKEN;
import static io.github.protino.codewatch.utils.Constants.PREF_CODE_STATE;

/**
 * @author Gurupad Mamadapur
 */
public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String originalHashState;
    private LoginEvent loginEvent = new LoginEvent();
    private String redirectUri;

    public LoginActivity() {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            String random = String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
            originalHashState = String.valueOf(Arrays.hashCode(messageDigest.digest(random.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //Lifecycle start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String appScheme = getString(R.string.app_scheme);
        redirectUri = new Uri.Builder()
                .scheme(appScheme)
                .authority(getString(R.string.login_host))
                .build().toString();

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null
                && intent.getScheme().equals(appScheme)) {
            // User has been authorized, now fetch the access token
            fetchAccessToken(intent);
        } else {

            // Launch browser and authenticate the user

            /*Build authorization url */
            Uri loginUrl = Uri.parse(ServiceGenerator.BASE_URL)
                    .buildUpon()
                    .appendPath("oauth")
                    .appendPath("authorize")
                    .appendQueryParameter("client_id", BuildConfig.CLIENT_ID)
                    .appendQueryParameter("response_type", "code")
                    .appendQueryParameter("redirect_uri", redirectUri)
                    .appendQueryParameter("scope", buildScopeList())
                    .appendQueryParameter("state", originalHashState)
                    .build();

            /*Store state to verify later*/
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putString(PREF_CODE_STATE, originalHashState);
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
        originalHashState = sharedPreferences.getString(PREF_CODE_STATE, null);
        if (code != null && state != null && state.equals(originalHashState)) {
            if (state.equals(originalHashState)) {
                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putString(PREF_ACCESS_CODE, code);
                sharedPreferencesEditor.apply();
                requestAccessToken(code);
            } else {
                FirebaseCrash.log("Invalid state received. Possible security attack");
                EventBus.getDefault().postSticky(loginEvent.setLoginSuccess(false));
                finish();
            }
        } else {
            FirebaseCrash.log("No access_code received");
            EventBus.getDefault().postSticky(loginEvent.setLoginSuccess(false));
            finish();
        }
    }

    private void requestAccessToken(String code) {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<AccessToken> call = apiInterface.getAccessToken(
                BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET,
                redirectUri, "authorization_code", code);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                SharedPreferences.Editor sharedPreferencesEditor;
                sharedPreferencesEditor = sharedPreferences.edit();
                AccessToken accessToken = response.body();
                accessToken.setRetrievalTime((long) (System.currentTimeMillis() / 1e3));
                Gson gson = new Gson();
                String gsonString = gson.toJson(accessToken);
                sharedPreferencesEditor.putString(PREF_ACCESS_TOKEN, gsonString);
                sharedPreferencesEditor.apply();
                EventBus.getDefault().postSticky(loginEvent.setLoginSuccess(true));
                finish();
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                FirebaseCrash.report(t);
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
