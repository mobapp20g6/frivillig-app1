package no.ntnu.mobapp20g6.app1.ui.tasklist;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import no.ntnu.mobapp20g6.app1.R;

public class TaskListFragment extends Fragment {

    private TaskListViewModel taskListViewModel;
    private TaskListViewAdapter taskListViewAdapter;
    //TODO add field for TaskViewModel

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        NavController navController = NavHostFragment.findNavController(getParentFragment());
        int lastUsedNav = navController.getCurrentDestination().getId();
        View root = inflater.inflate(R.layout.task_list_fragment, container, false);
        taskListViewModel = new ViewModelProvider(this, new TaskListViewModelFactory()).get(TaskListViewModel.class);
        taskListViewAdapter = new TaskListViewAdapter(new ArrayList<>(), taskListViewModel.loadPicasso(getContext()),
                onClick -> {
            //TODO Implement functionality when opening a task.
            System.out.println("A task was clicked.");
        });

        //Set title of the view.
        setDisplayName(root, lastUsedNav);

        final FloatingActionButton newTaskFab = root.findViewById(R.id.task_list_fab);
        if(taskListViewModel.isLoggedIn()) {
            newTaskFab.setVisibility(View.VISIBLE);
            newTaskFab.setOnClickListener(onClick -> {
                //TODO Implement functionality when creating a task. Use navController to go to new fragment.
                System.out.println("New task fab was clicked.");
            });
        }

        final RecyclerView recyclerView = root.findViewById(R.id.recycler_view_task_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(taskListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setRecyclerViewList(root, lastUsedNav);

        return root;
    }

    @SuppressLint("NonConstantResourceId")
    private void setDisplayName(View root, int lastUsedNav) {
        TextView title;
        switch (lastUsedNav) {
            case R.id.nav_public_tasks:
                title = root.findViewById(R.id.task_list_title);
                title.setText(R.string.ic_menu_public_tasks);
                break;

            case R.id.nav_assigned_tasks:
                title = root.findViewById(R.id.task_list_title);
                title.setText(R.string.ic_menu_assigned_tasks);
                break;

            case R.id.nav_own_tasks:
                title = root.findViewById(R.id.task_list_title);
                title.setText(R.string.ic_menu_own_tasks);
                break;

            default:
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void setRecyclerViewList(View root, int lastUsedNav) {
        switch (lastUsedNav) {
            case R.id.nav_public_tasks:
                taskListViewModel.loadPublicTasks(listResult -> {
                    //Snackbar is a small text view.
                    Snackbar listResultMsg = Snackbar.make(root, "Unable to load public tasks.", Snackbar.LENGTH_LONG);
                    listResultMsg.setTextColor(Color.YELLOW);
                    if(listResult == null) {
                        listResultMsg.show();
                    } else {
                        listResultMsg.dismiss();
                    }
                });

                taskListViewModel.getPublicTasks().observe(getViewLifecycleOwner(), tasks -> taskListViewAdapter.setTaskList(tasks));
                break;

            case R.id.nav_assigned_tasks:
                taskListViewModel.loadAssignedTasks(listResult -> {
                    //Snackbar is a small text view.
                    Snackbar listResultMsg = Snackbar.make(root, "Unable to load assigned tasks.", Snackbar.LENGTH_LONG);
                    listResultMsg.setTextColor(Color.YELLOW);
                    if(listResult == null) {
                        listResultMsg.show();
                    } else {
                        listResultMsg.dismiss();
                    }
                });

                taskListViewModel.getAssignedTasks().observe(getViewLifecycleOwner(), tasks -> taskListViewAdapter.setTaskList(tasks));
                break;

            case R.id.nav_own_tasks:
                taskListViewModel.loadOwnTasks(listResult -> {
                    //Snackbar is a small text view.
                    Snackbar listResultMsg = Snackbar.make(root, "Unable to load own tasks.", Snackbar.LENGTH_LONG);
                    listResultMsg.setTextColor(Color.YELLOW);
                    if(listResult == null) {
                        listResultMsg.show();
                    } else {
                        listResultMsg.dismiss();
                    }
                });

                taskListViewModel.getOwnTasks().observe(getViewLifecycleOwner(), tasks -> taskListViewAdapter.setTaskList(tasks));
                break;

            default:
        }
    }
}