package io.github.protino.codewatch.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import io.github.protino.codewatch.R;
import io.github.protino.codewatch.utils.CacheUtils;
import io.github.protino.codewatch.utils.Constants;
import timber.log.Timber;

/**
 * @author Gurupad Mamadapur
 */

public class PreChecksActivity extends AppCompatActivity {

    private FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Login checks
        if (!CacheUtils.isLoggedIn(this) || !CacheUtils.isFireBaseSetup(this)) {
            Intent intent = new Intent(this, OnBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }

        //Now check if app update is required
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setDefaults(R.xml.config);
        testIfAppUpdateIsRequired();
    }


    private void testIfAppUpdateIsRequired() {
        firebaseRemoteConfig.fetch()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activateFetched();
                        } else {
                            FirebaseCrash.report(task.getException());
                        }

                        if (firebaseRemoteConfig.getBoolean(Constants.APP_UPDATE_KEY)) {
                            showUpdateDialog();
                        } else {
                            startActivity(new Intent(PreChecksActivity.this, NavigationDrawerActivity.class));
                            finish();
                        }
                    }
                });
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.update_required)
                .setMessage(R.string.app_update_message)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Timber.d("Update clicked");
                        //Launch PlayStore ...
                        finish();
                    }
                });
        builder.create().show();
    }
}
