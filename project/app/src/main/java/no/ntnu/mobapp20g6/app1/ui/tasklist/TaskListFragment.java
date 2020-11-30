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
import no.ntnu.mobapp20g6.app1.ui.task.TaskViewModel;
import no.ntnu.mobapp20g6.app1.ui.task.TaskViewModelFactory;

public class TaskListFragment extends Fragment {

    private TaskListViewModel taskListViewModel;
    private TaskListViewAdapter taskListViewAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        NavController navController = NavHostFragment.findNavController(getParentFragment());
        int lastUsedNav = navController.getCurrentDestination().getId();
        View root = inflater.inflate(R.layout.task_list_fragment, container, false);
        View view = container.getRootView();
        TaskViewModel taskViewModel = new ViewModelProvider(requireActivity(), new TaskViewModelFactory()).get(TaskViewModel.class);
        taskListViewModel = new ViewModelProvider(this, new TaskListViewModelFactory()).get(TaskListViewModel.class);
        taskListViewAdapter = new TaskListViewAdapter(new ArrayList<>(), taskListViewModel.loadPicasso(getContext()),
                onClick -> {
            //When clicking on a task.
            taskViewModel.setActiveTask(onClick);
            navController.navigate(R.id.nav_task);
        });


        final FloatingActionButton newTaskFab = root.findViewById(R.id.task_list_fab);
        if(taskListViewModel.isLoggedIn()) {
            newTaskFab.setVisibility(View.VISIBLE);
            newTaskFab.setOnClickListener(onClick -> {
                System.out.println("New task fab was clicked.");
                navController.navigate(R.id.nav_createtask);
            });
        }

        final RecyclerView recyclerView = root.findViewById(R.id.recycler_view_task_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(taskListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(taskListViewModel.isLoggedIn()) {
            //Set title of the view.
            setDisplayName(root, lastUsedNav);
            setRecyclerViewList(view, lastUsedNav);
        } else {
            if(view != null) {
                setSnackbarText("You have to be logged in to see tasks!", view).show();
            }
        }

        return root;
    }

    /**
     * Sets the display name of fragment.
     * @param root view root.
     * @param lastUsedNav id of last navigation button which was pressed.
     */
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

    /**
     * Sets the list for the recyclerview depending on the last navigation bar button which was pressed.
     * @param view view the container is holding on.
     * @param lastUsedNav id of last navigation button which was pressed.
     */
    @SuppressLint("NonConstantResourceId")
    private void setRecyclerViewList(View view, int lastUsedNav) {
        switch (lastUsedNav) {
            case R.id.nav_public_tasks:
                taskListViewModel.loadPublicTasks(listResult -> {
                    if(listResult == null && view != null) {
                        setSnackbarText("Unable to load public tasks.", view).show();
                    }
                });

                taskListViewModel.getPublicTasks().observe(getViewLifecycleOwner(), tasks -> taskListViewAdapter.setTaskList(tasks));
                break;
            case R.id.nav_assigned_tasks:
                taskListViewModel.loadAssignedTasks(listResult -> {
                    if(listResult == null && view != null) {
                        setSnackbarText("Unable to load assigned tasks.", view).show();
                    }
                });

                taskListViewModel.getAssignedTasks().observe(getViewLifecycleOwner(), tasks -> taskListViewAdapter.setTaskList(tasks));
                break;

            case R.id.nav_own_tasks:
                taskListViewModel.loadOwnTasks(listResult -> {
                    if(listResult == null && view != null) {
                        setSnackbarText("Unable to load own tasks.", view).show();
                    }
                });

                taskListViewModel.getOwnTasks().observe(getViewLifecycleOwner(), tasks -> taskListViewAdapter.setTaskList(tasks));
                break;

            default:
        }
    }

    /**
     * Makes a small bar with text on the screen.
     * @param msg text to be shown on the screen.
     * @param view view the container is holding on.
     * @return Snackbar with text and yellow color.
     */
    private Snackbar setSnackbarText(String msg, View view) {
        if(view != null) {
            Snackbar listResultMsg = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
            listResultMsg.setTextColor(Color.YELLOW);
            return listResultMsg;
        }
        return null;
    }
}