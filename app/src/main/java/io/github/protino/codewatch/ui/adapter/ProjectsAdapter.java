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

    public List<ProjectItem> getFilteredResults(CharSequence constraint) {
        List<ProjectItem> results = new ArrayList<>();
        for (ProjectItem projectItem : projectItemList) {
            if (projectItem.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                results.add(projectItem);
            }
        }
        return results;
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
                    //display project id
                }
            });
        }
    }
}
