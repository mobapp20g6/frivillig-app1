package no.ntnu.mobapp20g6.app1.ui.task;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import no.ntnu.mobapp20g6.app1.data.ds.LoginDataSource;
import no.ntnu.mobapp20g6.app1.data.ds.TaskDataSource;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.repo.TaskRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class TaskViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TaskViewModel.class)) {
            return (T) new TaskViewModel(
                    TaskRepository.getInstance(new TaskDataSource()),
                    LoginRepository.getInstance(new LoginDataSource())
            );
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
