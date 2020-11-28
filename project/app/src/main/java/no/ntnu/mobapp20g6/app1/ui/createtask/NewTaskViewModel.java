package no.ntnu.mobapp20g6.app1.ui.createtask;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;

import java.util.Date;
import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.model.Location;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.repo.TaskRepository;

public class NewTaskViewModel extends ViewModel {

    public MutableLiveData<Location> currentLocationLiveData = new MutableLiveData<>();
    public MutableLiveData<Bitmap> currentImageBitmapLiveData = new MutableLiveData<>();
    public MutableLiveData<Date> currentDateLiveData = new MutableLiveData<>();
    public MutableLiveData<Task> currentNewTaskLiveData = new MutableLiveData<>();

    LoginRepository loginRepository;
    TaskRepository taskRepository;

    public NewTaskViewModel(LoginRepository loginRepository, TaskRepository taskRepository) {
        this.loginRepository = loginRepository;
        this.taskRepository = taskRepository;
    }


    public void createTask(Consumer<Result> resultCallback) {
        if (loginRepository.isLoggedIn() == false) {
           resultCallback.accept(new Result.Error(new Exception("Not loggedIn")));
           return;
        }

        String title;
        String description;
        Long noUsers;
        noUsers = new Long(10);
        Date date = currentDateLiveData.getValue();
        Long groupId;
        if (loginRepository.getCurrentUser().getUserGroup() != null) {
            groupId = loginRepository.getCurrentUser().getUserGroup().getGroupId();
        } else {
            groupId = null;
        }

        taskRepository.createTask(loginRepository.getToken(), title, description, noUsers, date, groupId, (createTaskResult) -> {
            resultCallback.accept(createTaskResult);
        });
    }
}
