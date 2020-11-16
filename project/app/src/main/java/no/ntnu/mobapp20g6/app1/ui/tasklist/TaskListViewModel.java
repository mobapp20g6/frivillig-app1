package no.ntnu.mobapp20g6.app1.ui.tasklist;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.repo.TaskRepository;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TaskListViewModel extends ViewModel {
    private final TaskRepository taskRepo;
    private final LoginRepository loginRepo;
    private Picasso picasso;

    public TaskListViewModel(TaskRepository taskRepo, LoginRepository loginRepo) {
        this.taskRepo = taskRepo;
        this.loginRepo = loginRepo;
    }

    /**
     * Tries to load public tasks from the server and return them into local data.
     * @param loadResultCallback Return a callback
     */
    public void loadPublicTasks(Consumer<Result<List<Task>>> loadResultCallback) {
        taskRepo.getPublicTasks(loginRepo.getToken(), loadResultCallback);
    }

    /**
     * Tries to load own tasks from the server and return them into local data.
     * @param loadResultCallback Return a callback
     */
    public void loadOwnTasks(Consumer<Result<List<Task>>> loadResultCallback) {
        taskRepo.getOwnedTasks(loginRepo.getToken(), loadResultCallback);
    }

    /**
     * Tries to load assigned tasks from the server and return them into local data.
     * @param loadResultCallback Return a callback
     */
    public void loadAssignedTasks(Consumer<Result<List<Task>>> loadResultCallback) {
        taskRepo.getAssignedTasks(loginRepo.getToken(), loadResultCallback);
    }

    public Picasso loadPicasso(Context context) {
        System.out.println("Loading picasso.");
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", loginRepo.getToken()).build();
                    return chain.proceed(newRequest);
                }).build();
        return new Picasso.Builder(context).downloader(new OkHttp3Downloader(client)).build();
    }

    /**
     * Return public tasks from the local data.
     * @return public tasks from the local data.
     */
    public LiveData<List<Task>> getPublicTasks() {
        return taskRepo.getLiveDataTaskList();
    }

    /**
     * Return own tasks from the local data.
     * @return own tasks from the local data.
     */
    public LiveData<List<Task>> getOwnTasks() {
        return taskRepo.getLiveDataOwnedTasks();
    }

    /**
     * Return assigned tasks from the local data.
     * @return assigned tasks from the local data.
     */
    public LiveData<List<Task>> getAssignedTasks() {
        return taskRepo.getLiveDataAssignedTasks();
    }

    /**
     * Return true if user is still logged in.
     * @return true if user is still logged in.
     */
    public boolean isLoggedIn() {
        return loginRepo.isLoggedIn();
    }
}