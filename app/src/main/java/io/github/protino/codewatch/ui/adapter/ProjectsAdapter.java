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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.ProjectItem;
import io.github.protino.codewatch.utils.FormatUtils;

/**
 * @author Gurupad Mamadapur
 */

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder> implements Filterable {

    private final Context context;
    private List<ProjectItem> projectItemList;
    private List<ProjectItem> projectItemListCopy;
    private OnItemSelectedListener onItemSelectedListener;


    public ProjectsAdapter(Context context, List<ProjectItem> items) {
        this.context = context;
        projectItemList = items;
        projectItemListCopy = items;  //Inefficient memory wise, but effective while searching
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder holder, int position) {
        ProjectItem projectItem = projectItemList.get(position);

        holder.projectName.setText(projectItem.getName());
        holder.projectTimeSpent.setText(FormatUtils.getFormattedTime(context, projectItem.getTotalSeconds()));
    }

    @Override
    public int getItemCount() {
        return projectItemList.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ProjectItem> filteredResults;

                if (constraint.length() == 0) {
                    filteredResults = projectItemListCopy;
                } else {
                    filteredResults = getFilteredResults(constraint);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredResults;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                projectItemList = (List<ProjectItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    /**
     * @param constraint on project name
     * @return List of projectItems that match the constraint
     */
    public List<ProjectItem> getFilteredResults(CharSequence constraint) {
        List<ProjectItem> results = new ArrayList<>();
        for (ProjectItem projectItem : projectItemList) {
            if (projectItem.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                results.add(projectItem);
            }
        }
        return results;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public void swapData(List<ProjectItem> projectItemList) {
        this.projectItemList = projectItemList;
        this.projectItemListCopy = projectItemList;
        notifyDataSetChanged();
    }

    public interface OnItemSelectedListener {
        void onItemSelected(String projectName);
    }

    public class ProjectViewHolder extends RecyclerView.ViewHolder {

        //@formatter:off
        @BindView(R.id.project_name) TextView projectName;
        @BindView(R.id.project_time_spent) TextView projectTimeSpent;
        //@formatter:on
        public ProjectViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String projectName = projectItemList.get(getAdapterPosition()).getName();
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.onItemSelected(projectName);
                    }
                }
            });
        }
    }
}
