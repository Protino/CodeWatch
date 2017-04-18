package io.github.protino.codewatch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.utils.Constants;

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

        String developedByText = getString(R.string.developed_by);
        SpannableString spannableString = new SpannableString(developedByText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                openMyProfile();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(
                clickableSpan,
                developedByText.indexOf('G'), developedByText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        developedBy.setText(spannableString);
        developedBy.setMovementMethod(LinkMovementMethod.getInstance());
        developedBy.setHighlightColor(getResources().getColor(R.color.colorAccent));
    }

    @OnClick({R.id.up_caret, R.id.action_share, R.id.developed_by})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.up_caret:
                onBackPressed();
                break;
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_share_text));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            default:
                break;
        }
    }

    private void openMyProfile() {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        profileIntent.putExtra(Intent.EXTRA_TEXT, Constants.DEVELOPER_ID);
        startActivity(profileIntent);
    }
}
