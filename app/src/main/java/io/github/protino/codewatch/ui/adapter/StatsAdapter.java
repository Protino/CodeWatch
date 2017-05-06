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
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.PieChartItem;
import io.github.protino.codewatch.utils.FormatUtils;

/**
 * @author Gurupad Mamadapur
 */

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.ViewHolder> {

    private Context context;
    private List<PieChartItem> itemList;
    private String highlightedName;

    public StatsAdapter(Context context, List<PieChartItem> itemList, String highlightedName) {
        this.context = context;
        this.itemList = itemList;
        this.highlightedName = highlightedName;
    }

    public List<PieChartItem> getItemList() {
        return itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.expanded_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PieChartItem item = itemList.get(position);
        holder.name.setText(item.getName());
        holder.time.setText(FormatUtils.getFormattedTime(context, item.getTime()));
        holder.percent.setText(context.getString(R.string.percent_format, item.getPercent()));

        //change the type face to bold if highlight is needed
        boolean mustHighlight = item.getName().contentEquals(highlightedName);
        holder.name.setTypeface(holder.name.getTypeface(), mustHighlight ? Typeface.BOLD : Typeface.NORMAL);
        holder.time.setTypeface(holder.time.getTypeface(), mustHighlight ? Typeface.BOLD : Typeface.NORMAL);
        holder.percent.setTypeface(holder.percent.getTypeface(), mustHighlight ? Typeface.BOLD : Typeface.NORMAL);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //@formatter:off
        @BindView(R.id.name) TextView name;
        @BindView(R.id.time) TextView time;
        @BindView(R.id.percent) TextView percent;
        //@formatter:on
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
