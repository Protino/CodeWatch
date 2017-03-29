package io.github.protino.codewatch.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.GoalItem;
import io.github.protino.codewatch.utils.FormatUtils;
import timber.log.Timber;

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
        int goalData = goalItem.getData();

        String resultText = null;
        String goalId = goalItem.getId();
        String projectName = projectNameMap.get(goalId); //null if not found
        String formattedTime = FormatUtils.getFormattedTime(context, goalData);

        switch (goalItem.getType()) {
            case LANGUAGE_GOAL:
                resultText = context.getString(R.string.language_goal, goalItem, formattedTime);
                break;
            case PROJECT_DEADLINE_GOAL:
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
                String date = simpleDateFormat.format(new Date(goalData * 1000));
                resultText = context.getString(R.string.project_deadline_goal, projectName, date);
                break;
            case PROJECT_DAILY_GOAL:
                resultText = context.getString(R.string.project_daily_goal, goalItem, formattedTime);
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
            Timber.d(goalItem.toString());
        }
    }
}
