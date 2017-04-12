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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.protino.codewatch.R;
import io.github.protino.codewatch.model.ProjectItem;
import io.github.protino.codewatch.model.firebase.Project;
import io.github.protino.codewatch.ui.adapter.ProjectsAdapter;
import io.github.protino.codewatch.utils.CacheUtils;

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
    private AtomicInteger latchCount = new AtomicInteger(); //naive synchronization

    private MenuItem sortByTimeSpent;
    private MenuItem sortByName;

    private DatabaseReference projectsDatabaseRef;
    private DatabaseReference projectTimeSpentRef;
    private ValueEventListener projectValueEventListener;
    private ValueEventListener timeSpentValueEventListner;
    private HashMap<String, Long> timeSpentMap;

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
        hideProgressBar(false);
        context = getActivity();
        String firebaseUid = CacheUtils.getFirebaseUserId(context);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        projectsDatabaseRef = firebaseDatabase.getReference()
                .child("users").child(firebaseUid).child("projects");

        projectTimeSpentRef = firebaseDatabase.getReference()
                .child("users").child(firebaseUid).child("timeSpentOnProjects"); // case

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
    public void onResume() {
        super.onResume();
        attachValueEventListener();
    }


    @Override
    public void onDestroyView() {
        detachValueEventListener();
        super.onDestroyView();
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

    private void attachValueEventListener() {
        if (projectValueEventListener == null) {
            projectValueEventListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    projectItemList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Project project = snapshot.getValue(Project.class);
                        projectItemList.add(new ProjectItem(project.getId(), project.getName(), 0));
                    }
                    latchCount.incrementAndGet();
                    onDownloadComplete();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        if (timeSpentValueEventListner == null) {
            timeSpentValueEventListner = new ValueEventListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    timeSpentMap = (HashMap<String, Long>) dataSnapshot.getValue();
                    latchCount.incrementAndGet();
                    onDownloadComplete();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        projectTimeSpentRef.addValueEventListener(timeSpentValueEventListner);
        projectsDatabaseRef.addValueEventListener(projectValueEventListener);

    }

    private void onDownloadComplete() {
        if (latchCount.get() == 2) {
            for (ProjectItem item : projectItemList) {
                Long totalSeconds = timeSpentMap.get(item.getName());
                if (totalSeconds != null) {
                    item.setTotalSeconds(totalSeconds.intValue());
                }
            }
            sortData();
            projectsAdapter.swapData(projectItemList);
            hideProgressBar(true);
            if (projectItemList.isEmpty()) {
                displayErrorText(context.getString(R.string.empty_project_list));
            }
        }
    }

    private void detachValueEventListener() {
        if (projectValueEventListener != null) {
            projectsDatabaseRef.removeEventListener(projectValueEventListener);
            projectValueEventListener = null;
        }
        if (timeSpentValueEventListner != null) {
            projectTimeSpentRef.removeEventListener(timeSpentValueEventListner);
            timeSpentValueEventListner = null;
        }
    }


    public void sortData() {
        Collections.sort(projectItemList,
                sortByName.isChecked() ? new NameComparator() : new TimeComparator());
        projectsAdapter.notifyDataSetChanged();
    }

    private void initializeData() {
        projectItemList = new ArrayList<>();
        timeSpentMap = new HashMap<>();
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

