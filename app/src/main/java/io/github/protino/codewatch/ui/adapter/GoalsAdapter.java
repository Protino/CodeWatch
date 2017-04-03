package io.github.protino.codewatch.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.GoalItem;
import io.github.protino.codewatch.utils.FormatUtils;

import static io.github.protino.codewatch.utils.Constants.LANGUAGE_GOAL;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DAILY_GOAL;
import static io.github.protino.codewatch.utils.Constants.PROJECT_DEADLINE_GOAL;

/**
 * @author Gurupad Mamadapur
 */

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalsViewHolder> {

    private final Context context;
    private List<GoalItem> dataList;
    private Map<String, String> projectNameMap;
    private OnGoalItemClickListener itemClickListener;

    public GoalsAdapter(Context context, List<GoalItem> dataList, Map<String, String> projectNameMap) {
        this.context = context;
        this.dataList = dataList;
        this.projectNameMap = projectNameMap;
    }

    @Override
    public GoalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_item, parent, false);
        return new GoalsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoalsViewHolder holder, int position) {
        GoalItem goalItem = dataList.get(position);
        long goalData = goalItem.getData();

        String resultText = null;
        String goalId = goalItem.getId();
        String projectName = projectNameMap.get(goalId); //null if not found

        switch (goalItem.getType()) {
            case LANGUAGE_GOAL:
                resultText = context.getString(R.string.language_goal, goalId, goalData);
                break;
            case PROJECT_DEADLINE_GOAL:
                resultText = FormatUtils.getDeadlineGoalText(context, projectName, goalData);
                break;
            case PROJECT_DAILY_GOAL:
                resultText = context.getString(R.string.project_daily_goal, goalData, projectName);
                break;
            default:
                break;
        }
        holder.goal.setText(resultText);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void addItem(GoalItem goalItem) {
        if (isDuplicate(goalItem)) {
            Toast.makeText(context, R.string.duplicate_goal_message, Toast.LENGTH_SHORT).show();
            return;
        }
        dataList.add(goalItem);
        notifyDataSetChanged();
    }

    private boolean isDuplicate(GoalItem goalItem) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        if (goalItem == null) {
            return false;
        }

        String goalId = goalItem.getId();
        int goalType = goalItem.getType();

        for (GoalItem item : dataList) {
            if (item.getId().equals(goalId) && item.getType() == goalType) {
                return true;
            }
        }
        return false;
    }

    public void setGoalItemClickListener(OnGoalItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void deleteItem(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
    }


    public interface OnGoalItemClickListener {
        void onGoalItemClicked(GoalItem goalItem);
    }

    public class GoalsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //@formatter:off
        @BindView(R.id.goal) public TextView goal;
        //@formatter:on

        public GoalsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            GoalItem goalItem = dataList.get(getAdapterPosition());
            if (itemClickListener != null) {
                itemClickListener.onGoalItemClicked(goalItem);
            }
        }
    }
}
