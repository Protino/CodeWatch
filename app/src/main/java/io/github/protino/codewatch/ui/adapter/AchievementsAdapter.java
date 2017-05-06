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

package io.github.protino.codewatch.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.BadgeItem;
import io.github.protino.codewatch.ui.widget.BadgeView;

import static io.github.protino.codewatch.utils.Constants.BRONZE_BADGE;
import static io.github.protino.codewatch.utils.Constants.GOLD_BADGE;
import static io.github.protino.codewatch.utils.Constants.SILVER_BADGE;

/**
 * @author Gurupad Mamadapur
 */

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder> {

    private List<BadgeItem> dataList;
    private Context context;

    public AchievementsAdapter(Context context, List<BadgeItem> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public AchievementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_badge, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AchievementViewHolder holder, int position) {
        BadgeItem badgeItem = dataList.get(position);
        holder.reqText.setText(badgeItem.getRequirement());

        int badgeColor;
        switch (badgeItem.getType()) {
            case GOLD_BADGE:
                badgeColor = R.color.gold;
                break;
            case SILVER_BADGE:
                badgeColor = R.color.silver;
                break;
            case BRONZE_BADGE:
                badgeColor = R.color.bronze;
                break;
            default:
                throw new UnsupportedOperationException("Invalid badge type");
        }
        holder.badgeView.setBadgeColor(context.getResources().getColor(badgeColor));
        holder.badgeView.setTextSize(16);
        holder.badgeView.setText(badgeItem.getName());

        // Dim the locked badges
        holder.rootView.setAlpha(badgeItem.getIsUnlocked() ? 1f : 0.3f);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void swapData(List<BadgeItem> badgeItemList) {
        dataList = badgeItemList;
        notifyDataSetChanged();
    }

    public class AchievementViewHolder extends RecyclerView.ViewHolder {

        //@formatter:off
        @BindView(R.id.badge) public BadgeView badgeView;
        @BindView(R.id.req_text) public TextView reqText;
        //@formatter:on
        private View rootView;

        public AchievementViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
