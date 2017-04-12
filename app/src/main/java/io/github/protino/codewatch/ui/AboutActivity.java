package io.github.protino.codewatch.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.protino.codewatch.R;
import timber.log.Timber;

/**
 * @author Gurupad Mamadapur
 */

public class AboutActivity extends AppCompatActivity {

    //@formatter:off
    @BindView(R.id.developed_by) TextView developedBy;
    @BindView(R.id.powered_by) TextView poweredBy;
    //@formatter:on

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        poweredBy.setMovementMethod(LinkMovementMethod.getInstance());

        //Linkify.addLinks(poweredBy,Linkify.WEB_URLS);

        developedBy.setText(getString(R.string.developed_by, "Gurupad Mamadapur"));//todo add action intent

    }

    @OnClick({R.id.up_caret, R.id.action_share, R.id.developed_by})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.up_caret:
                onBackPressed();
                break;
            case R.id.action_share:
                Timber.d("Share clicked");
                break;
            case R.id.developed_by:
                Timber.d("Developed by clicked");
                break;
            default:
                break;
        }
    }
}
