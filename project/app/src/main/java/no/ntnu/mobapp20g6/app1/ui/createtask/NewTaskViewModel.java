package no.ntnu.mobapp20g6.app1.ui.createtask;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;
import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.model.Location;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.repo.SharedNonCacheRepository;
import no.ntnu.mobapp20g6.app1.data.repo.TaskRepository;

public class NewTaskViewModel extends ViewModel {

    public MutableLiveData<Location> currentLocationLiveData = new MutableLiveData<>();
    public MutableLiveData<Bitmap> currentImageBitmapLiveData = new MutableLiveData<>();
    public MutableLiveData<Date> currentDateLiveData = new MutableLiveData<>();
    public MutableLiveData<Task> currentNewTaskLiveData = new MutableLiveData<>();

    LoginRepository loginRepository;
    TaskRepository taskRepository;
    SharedNonCacheRepository sharedNonCacheRepository;

    public NewTaskViewModel(LoginRepository loginRepository, TaskRepository taskRepository, SharedNonCacheRepository sharedNonCacheRepository) {
        this.loginRepository = loginRepository;
        this.taskRepository = taskRepository;
        this.sharedNonCacheRepository = sharedNonCacheRepository;
    }


    /**
     * Create a new task, returns a Result.Success<Task> if successful trough a callback
     * @param title title of the task
     * @param description description of the task
     * @param participantCount number of participants
     * @param isGroup true  = limit the visibility to the user's group, false = public task
     * @param resultCallback the result of the operation; either Result.Success or Result.Error
     */
    public void createTask(String title, String description, Long participantCount, boolean isGroup, Consumer<Result<Task>> resultCallback) {
        if (!loginRepository.isLoggedIn() || isDateSet() == false) {
           resultCallback.accept(new Result.Error(new Exception("Not loggedIn")));
        } else {
            Date date = currentDateLiveData.getValue();
            Long groupId;
            if (loginRepository.getCurrentUser().getUserGroup() != null && isGroup) {
                groupId = loginRepository.getCurrentUser().getUserGroup().getGroupId();
            } else {
                groupId = null;
            }
            taskRepository.createTask(loginRepository.getToken(), title, description, participantCount, date, groupId, resultCallback::accept);
        }
    }

    public void attachLocationToTask(Location location, Task task, Consumer<Result<Task>> attachResultCallback) {
        if (task != null && location != null && loginRepository.isLoggedIn()) {
            sharedNonCacheRepository.addLocationToTask(
                    loginRepository.getToken(),task.getId(),location.getGpsLat(),location.getGpsLong(),
                    null,null,null,null,
                    attachResultCallback::accept);
        }
    }

    private boolean isDateSet() {
        return currentDateLiveData.getValue() != null ? true : false;
    }
}
