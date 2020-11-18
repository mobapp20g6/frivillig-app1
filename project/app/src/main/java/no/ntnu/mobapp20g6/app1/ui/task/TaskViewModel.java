package no.ntnu.mobapp20g6.app1.ui.task;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import no.ntnu.mobapp20g6.app1.data.model.Task;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.repo.TaskRepository;

public class TaskViewModel extends ViewModel {
    private TaskRepository taskRepository;
    private LoginRepository loginRepository;
    private MutableLiveData<Task> activeTaskLiveData;

    public TaskViewModel(TaskRepository taskRepository, LoginRepository loginRepository) {
        this.taskRepository = taskRepository;
        this.loginRepository = loginRepository;
        if(activeTaskLiveData == null) {
            activeTaskLiveData = new MutableLiveData<>();
        }
    }
}