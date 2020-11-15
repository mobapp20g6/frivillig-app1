package no.ntnu.mobapp20g6.app1.ui.tasklist;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.data.model.Task;

public class TaskListFragment extends Fragment {

    private TaskListViewModel taskListViewModel;
    private TaskListViewAdapter taskListViewAdapter;
    //TODO add field for TaskViewModel

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //To know what list recyclerView shall show.
        Bundle args = getArguments();
        String listToShow = args.getString("listToShow", "public");

        NavController navController = NavHostFragment.findNavController(getParentFragment());
        View root = inflater.inflate(R.layout.task_list_fragment, container, false);
        taskListViewModel = new ViewModelProvider(this, new TaskListViewModelFactory()).get(TaskListViewModel.class);
        taskListViewAdapter = new TaskListViewAdapter(new ArrayList<Task>(), onClick -> {
            //TODO Implement functionality when opening a task.
        });

        final FloatingActionButton newTaskFab = root.findViewById(R.id.task_list_fab);
        if(taskListViewModel.isLoggedIn()) {
            newTaskFab.setVisibility(View.VISIBLE);
            newTaskFab.setOnClickListener(onClick -> {
                //TODO Implement functionality when creating a task. Use navController to go to new fragment.
            });
        }

        final RecyclerView recyclerView = root.findViewById(R.id.recycler_view_task_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(taskListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        switch(listToShow) {

            case "public":
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
                break;

            case "own":
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
                break;

            case "assigned":
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
                break;

            default:
                Snackbar listResultMsg = Snackbar.make(root, "Went into default case when trying to load tasks.", Snackbar.LENGTH_LONG);
                listResultMsg.setTextColor(Color.YELLOW);
                listResultMsg.show();
        }

        return root;
    }
}