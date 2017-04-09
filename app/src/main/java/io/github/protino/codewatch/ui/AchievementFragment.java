package io.github.protino.codewatch.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.ui.widget.BadgeView;

/**
 * @author Gurupad Mamadapur
 */

public class AchievementFragment extends Fragment {

    //@formatter:off
    @BindView(R.id.sample) BadgeView badgeView;
    //@formatter:on

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_badge, container, false);
        ButterKnife.bind(this, rootView);
        badgeView.setBadgeColor(getResources().getColor(R.color.silver));
        badgeView.setText("Whatever");
        badgeView.setTextSize(16);
        badgeView.requestLayout();
        badgeView.invalidate();
        return rootView;
    }
}
