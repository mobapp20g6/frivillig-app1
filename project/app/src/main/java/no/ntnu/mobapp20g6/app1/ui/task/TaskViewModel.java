package no.ntnu.mobapp20g6.app1.ui.task;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.data.model.Task;
import no.ntnu.mobapp20g6.app1.data.model.User;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.repo.TaskRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author TrymV
 */
public class TaskViewModel extends ViewModel {
    private final TaskRepository taskRepository;
    private final LoginRepository loginRepository;
    private MutableLiveData<Task> activeTaskLiveData;
    private Long forceLoadSelectedTaskId;

    public TaskViewModel(TaskRepository taskRepository, LoginRepository loginRepository) {
        this.taskRepository = taskRepository;
        this.loginRepository = loginRepository;
        if(activeTaskLiveData == null) {
            activeTaskLiveData = new MutableLiveData<>();
        }
    }

    /**
     * Return true if user is still logged in.
     * @return true if user is still logged in.
     */
    public boolean isLoggedIn() {
        return loginRepository.isLoggedIn();
    }

    /**
     * Return true if the current logged in user is the creator of the current viewed task.
     * @return true if the current logged in user is the creator of the current viewed task.
     */
    public boolean isUserOwnerOfTask() {
        return loginRepository.getCurrentUser().getUserId().equals(activeTaskLiveData.getValue().getCreator().getUserId());
    }

    /**
     * Return true if user is a participant of the current task.
     * @return true if user is a participant of the current task.
     */
    public boolean isUserMemberOfTask() {
        for(User user: activeTaskLiveData.getValue().getParticipants()) {
            if(user.getUserId().equals(loginRepository.getCurrentUser().getUserId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the current active task live data object.
     * @return the current active task live data object.
     */
    public MutableLiveData<Task> getActiveTaskLiveData() {
        return activeTaskLiveData;
    }

    /**
     * Sets the current active task in TaskViewModel.
     * @param task task to be set as current active.
     */
    public void setActiveTask(Task task) {
        activeTaskLiveData.setValue(task);
    }

    public void loadActiveTask(Long id) {
        taskRepository.getTask(loginRepository.getToken(), id, this::setActiveTask);
    }

    /**
     * Builds the picasso with Authorization Bearer <token>.
     * @param context current context of app.
     * @return Picasso with build in Authorization Bearer <token>.
     */
    public Picasso loadPicasso(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", loginRepository.getToken()).build();
                    return chain.proceed(newRequest);
                }).build();
        return new Picasso.Builder(context).downloader(new OkHttp3Downloader(client)).build();
    }

    /**
     * Add a user to the current active task if task is not full.
     * @param joinTaskCallbackResult callback return true if user successfully joined the task.
     */
    public void joinActiveTask(Consumer<Boolean> joinTaskCallbackResult) {
        Task task = activeTaskLiveData.getValue();
        if(task != null && !isTaskFull()) {
            taskRepository.joinTask(loginRepository.getToken(), task.getId(), taskToJoin -> {
                loadActiveTask(task.getId());
                if(activeTaskLiveData.getValue() != null && !isTaskFull()) {
                    //User successfully joined the task.
                    joinTaskCallbackResult.accept(true);
                } else {
                    joinTaskCallbackResult.accept(false);
                }
            });
        } else {
            joinTaskCallbackResult.accept(false);
        }
    }

    /**
     * Delete the current active task from the database.
     * @param deleteTaskCallbackResult callback return true if task was successfully removed.
     */
    public void deleteActiveTask(Consumer<Boolean> deleteTaskCallbackResult) {
        if(activeTaskLiveData != null && isUserOwnerOfTask()) {
            taskRepository.deleteTask(loginRepository.getToken(), activeTaskLiveData.getValue().getId(), wasTaskRemoved -> {
                loadActiveTask(activeTaskLiveData.getValue().getId());
                deleteTaskCallbackResult.accept(activeTaskLiveData == null);
            });
        } else {
            deleteTaskCallbackResult.accept(false);
        }
    }

    /**
     * Return true if current active task is full.
     * @return true if current active task is full.
     */
    public boolean isTaskFull() {
        return activeTaskLiveData.getValue().getParticipantCount() >= activeTaskLiveData.getValue().getParticipantLimit();
    }

    public void setForceLoadSelectedTaskId(Long forceLoadSelectedTaskId) {
        this.forceLoadSelectedTaskId = forceLoadSelectedTaskId;
    }

    public Long getForceLoadSelectedTaskId() {
        return forceLoadSelectedTaskId;
    }
}