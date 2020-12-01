package no.ntnu.mobapp20g6.app1.ui.group.display;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.model.Group;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import no.ntnu.mobapp20g6.app1.data.model.User;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.repo.SharedNonCacheRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class DisplayGroupViewModel extends ViewModel {

    private SharedNonCacheRepository shareRepo;
    private LoginRepository loginRepo;

    private MutableLiveData<Boolean> groupOwner = new MutableLiveData<>();
    private MutableLiveData<List<Task>> groupTasks = new MutableLiveData<>();

    public DisplayGroupViewModel(SharedNonCacheRepository shareRepo, LoginRepository loginRepo) {
        this.shareRepo = shareRepo;
        this.loginRepo = loginRepo;
    }

    public User getLoggedInUser() {
        return loginRepo.getCurrentUser();
    }

    public Picasso loadPicasso(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", loginRepo.getToken()).build();
                    return chain.proceed(newRequest);
                }).build();
        return new Picasso.Builder(context).downloader(new OkHttp3Downloader(client)).build();
    }

    public void isOwnerOfGroup(Long groupID, Consumer<Boolean> isGroupOwnerCallBack) {
        shareRepo.isOwnerOfGroup(loginRepo.getToken(), groupID, (result -> {
            if (result instanceof Result.Success) {
                groupOwner.setValue(((Result.Success<Boolean>) result).getData());
            } else {
                groupOwner.setValue(null);
            }
        }));
    }

    public void getAllGroupTasks(Consumer<List<Task>> groupTaskCallBack) {
        Group group = loginRepo.getCurrentUser().getUserGroup();
        shareRepo.getAllGroupTasks(loginRepo.getToken(), group.getGroupId(), (listResult -> {
            if (listResult instanceof Result.Success) {
                groupTasks.setValue(((Result.Success<List<Task>>) listResult).getData());
                groupTaskCallBack.accept(((Result.Success<List<Task>>) listResult).getData());
            }
        }));
    }

    public LiveData<Boolean> getGroupOwner() {
        return groupOwner;
    }

    public LiveData<List<Task>> getGroupTasks() {
        return groupTasks;
    }
}