package io.github.protino.codewatch.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.ProjectItem;
import io.github.protino.codewatch.ui.adapter.ProjectsAdapter;

/**
 * @author Gurupad Mamadapur
 */

public class ProjectsFragment extends Fragment implements SearchView.OnQueryTextListener, ProjectsAdapter.OnItemSelectedListener {

    //@formatter:off
    @BindView(R.id.projects_list) RecyclerView recyclerView;
    @BindView(R.id.progressBarLayout) View progressBarLayout;
    @BindView(R.id.error_text) TextView errorTextView;
    //@formatter:on

    private Context context;
    private ProjectsAdapter projectsAdapter;
    private List<ProjectItem> projectItemList;

    private MenuItem sortByTimeSpent;
    private MenuItem sortByName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_projects, container, false);
        ButterKnife.bind(this, rootView);

        context = getActivity();
        initializeData();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        projectsAdapter = new ProjectsAdapter(context, projectItemList);
        projectsAdapter.setOnItemSelectedListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(projectsAdapter);
        recyclerView.addItemDecoration(dividerItemDecoration);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hideProgressBar(true);
        if (projectItemList.isEmpty()) {
            displayErrorText(context.getString(R.string.empty_project_list));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.project_menu, menu);

        sortByName = menu.findItem(R.id.menu_sort_by_name);
        sortByTimeSpent = menu.findItem(R.id.menu_sort_by_time);

        sortData();

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(context.getString(R.string.search_hint));
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_sort_by_name:
                sortByName.setChecked(true);
                sortData();
                return true;
            case R.id.menu_sort_by_time:
                sortByTimeSpent.setChecked(true);
                sortData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        projectsAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        projectsAdapter.getFilter().filter(newText);
        return false;
    }

    public void sortData() {
        Collections.sort(projectItemList,
                sortByName.isChecked() ? new NameComparator() : new TimeComparator());
        projectsAdapter.notifyDataSetChanged();
    }

    private void initializeData() {
        projectItemList = new ArrayList<>();
        projectItemList.add(new ProjectItem(UUID.randomUUID().toString(), "CodeWatch", 3665));
        projectItemList.add(new ProjectItem(UUID.randomUUID().toString(), "Fad-Flicks", 55678));
        projectItemList.add(new ProjectItem(UUID.randomUUID().toString(), "StockHawk", 36549));
        projectItemList.add(new ProjectItem(UUID.randomUUID().toString(), "MPAndroidChart-Master", 354654));
        projectItemList.add(new ProjectItem(UUID.randomUUID().toString(), "MaterialDrawer", 23165));
        projectItemList.add(new ProjectItem(UUID.randomUUID().toString(), "Sunshine-V2", 987531));
        projectItemList.add(new ProjectItem(UUID.randomUUID().toString(), "Quizzes", 4568));
    }

    private void hideProgressBar(boolean hide) {
        progressBarLayout.setVisibility(hide ? View.GONE : View.VISIBLE);
        recyclerView.setVisibility(hide ? View.VISIBLE : View.GONE);
        errorTextView.setVisibility(View.GONE);
    }

    public void displayErrorText(String text) {
        recyclerView.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.GONE);
        errorTextView.setText(text);
        errorTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemSelected(String projectName) {
        Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, projectName);
        getActivity().startActivity(intent);
    }

    private class NameComparator implements Comparator<ProjectItem> {

        @Override
        public int compare(ProjectItem o1, ProjectItem o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    private class TimeComparator implements Comparator<ProjectItem> {
        @Override
        public int compare(ProjectItem o1, ProjectItem o2) {
            return o2.getTotalSeconds() - o1.getTotalSeconds();
        }
    }

}

