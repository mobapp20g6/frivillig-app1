package no.ntnu.mobapp20g6.app1.ui.tasklist;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import no.ntnu.mobapp20g6.app1.data.ds.LoginDataSource;
import no.ntnu.mobapp20g6.app1.data.ds.TaskDataSource;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.repo.TaskRepository;

public class TaskListViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(TaskListViewModel.class)) {
            return (T) new TaskListViewModel(TaskRepository.getInstance(new TaskDataSource()), LoginRepository.getInstance(new LoginDataSource()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
