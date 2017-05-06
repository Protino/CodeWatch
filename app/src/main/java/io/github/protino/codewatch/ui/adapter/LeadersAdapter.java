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
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.DefaultLeaderItem;
import io.github.protino.codewatch.model.TopperItem;
import io.github.protino.codewatch.utils.FormatUtils;

/**
 * @author Gurupad Mamadapur
 */

public class LeadersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TOPPER_VIEW_THRESHOLD = 6;
    private static final int TOPPER_ITEM_TYPE = 0;
    private static final int DEFAULT_ITEM_TYPE = 1;
    private final Context context;
    private List<Object> dataList;

    /**
     * true, if the number of items to display is greater than TOPPER_VIEW_THRESHOLD
     */
    private boolean isTopperViewNeeded;

    private int rankOffset;
    private OnItemSelectedListener onItemSelectedListener;

    public LeadersAdapter(Context context, List<Object> dataList) {
        this.context = context;
        this.dataList = dataList;
        isTopperViewNeeded = dataList.size() >= TOPPER_VIEW_THRESHOLD;

        //rankOffset decides rank value, +3 if toppers are display else +1
        rankOffset = (isTopperViewNeeded) ? 3 : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (!isTopperViewNeeded) {
            return DEFAULT_ITEM_TYPE;
        }
        if (position == 0) {
            return TOPPER_ITEM_TYPE;
        } else {
            return DEFAULT_ITEM_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TOPPER_ITEM_TYPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_toppers, parent, false);
            return new TopperViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaders, parent, false);
            return new DefaultLeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TOPPER_ITEM_TYPE) {
            TopperViewHolder viewHolder = (TopperViewHolder) holder;
            TopperItem topperItem = (TopperItem) dataList.get(position);

            List<DefaultLeaderItem> defaultLeaderItem = topperItem.getDefaultLeaderItems();

            DefaultLeaderItem leaderItem = defaultLeaderItem.get(0);
            viewHolder.name1.setText(leaderItem.getDisplayName());
            viewHolder.totalSeconds1.setText(FormatUtils.getFormattedTime(context, leaderItem.getTotalSeconds()));
            setCircularAvatar(viewHolder.imageView1, leaderItem.getPhotoUrl());

            leaderItem = defaultLeaderItem.get(1);
            viewHolder.name2.setText(leaderItem.getDisplayName());
            viewHolder.totalSeconds2.setText(FormatUtils.getFormattedTime(context, leaderItem.getTotalSeconds()));
            setCircularAvatar(viewHolder.imageView2, leaderItem.getPhotoUrl());

            leaderItem = defaultLeaderItem.get(2);
            viewHolder.name3.setText(leaderItem.getDisplayName());
            viewHolder.totalSeconds3.setText(FormatUtils.getFormattedTime(context, leaderItem.getTotalSeconds()));
            setCircularAvatar(viewHolder.imageView3, leaderItem.getPhotoUrl());
        } else {
            DefaultLeaderViewHolder viewHolder = (DefaultLeaderViewHolder) holder;
            DefaultLeaderItem leaderItem = (DefaultLeaderItem) dataList.get(position);
            viewHolder.name.setText(leaderItem.getDisplayName());
            viewHolder.totalSeconds.setText(FormatUtils.getFormattedTime(context, leaderItem.getTotalSeconds()));
            viewHolder.rank.setText(String.valueOf(position + rankOffset));
            setCircularAvatar(viewHolder.avatar, leaderItem.getPhotoUrl());
        }
    }

    private void setCircularAvatar(final ImageView avatar, String photoUrl) {
        Glide.with(context)
                .load(photoUrl)
                .asBitmap()
                .placeholder(R.drawable.ic_account_circle_white_24dp)
                .into(new BitmapImageViewTarget(avatar) {
                    @Override
                    protected void setResource(Bitmap resource) {

                        RoundedBitmapDrawable drawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        drawable.setCircular(true);
                        avatar.setImageDrawable(drawable);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void swapData(List<Object> dataList) {
        this.dataList = dataList;
        isTopperViewNeeded = dataList.size() >= TOPPER_VIEW_THRESHOLD;
        rankOffset = (isTopperViewNeeded) ? 3 : 1;
        notifyDataSetChanged();
    }

    /**
     * @param keyUserId
     * @return Position of the item
     */
    public int getItemPositionById(String keyUserId) {
        int beginIndex = 0;
        if (isTopperViewNeeded) {
            beginIndex = 1;
            TopperItem topperItem = (TopperItem) dataList.get(0);
            for (DefaultLeaderItem leaderItem : topperItem.getDefaultLeaderItems()) {
                if (leaderItem.getUserId().equals(keyUserId)) {
                    return 0;
                }
            }
        }
        for (int i = beginIndex; i < dataList.size(); i++) {
            if (((DefaultLeaderItem) dataList.get(i)).getUserId().equals(keyUserId)) {
                return i;
            }
        }
        return -1;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(String userId);
    }

    public class TopperViewHolder extends RecyclerView.ViewHolder {

        //@formatter:off
        @BindView(R.id.avatar) public ImageView imageView1;
        @BindView(R.id.imgAvatar2) public ImageView imageView2;
        @BindView(R.id.imgAvatar3) public ImageView imageView3;
        @BindView(R.id.total_seconds) public TextView totalSeconds1;
        @BindView(R.id.total_seconds2) public TextView totalSeconds2;
        @BindView(R.id.total_seconds3) public TextView totalSeconds3;
        @BindView(R.id.name) public TextView name1;
        @BindView(R.id.name2) public TextView name2;
        @BindView(R.id.name3) public TextView name3;
        //@formatter:on

        public TopperViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.first, R.id.second, R.id.third})
        public void onProfileClick(View view) {
            TopperItem topperItem = (TopperItem) dataList.get(0);
            List<DefaultLeaderItem> leaderItems = topperItem.getDefaultLeaderItems();
            DefaultLeaderItem leaderItem = null;
            switch (view.getId()) {
                case R.id.first:
                    leaderItem = leaderItems.get(0);
                    break;
                case R.id.second:
                    leaderItem = leaderItems.get(1);
                    break;
                case R.id.third:
                    leaderItem = leaderItems.get(2);
                    break;
                default:
                    break;
            }
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(leaderItem.getUserId());
            }
        }
    }

    public class DefaultLeaderViewHolder extends RecyclerView.ViewHolder {

        //@formatter:off
        @BindView(R.id.name) public TextView name;
        @BindView(R.id.total_seconds) public TextView totalSeconds;
        @BindView(R.id.avatar) public ImageView avatar;
        @BindView(R.id.rank) public TextView rank;
        //@formatter:on


        public DefaultLeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.item_leader_root)
        public void onClick() {
            DefaultLeaderItem leaderItem = (DefaultLeaderItem) dataList.get(getAdapterPosition());
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(leaderItem.getUserId());
            }
        }
    }
}
