package no.ntnu.mobapp20g6.app1.ui.createtask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.internal.ContextUtils;

import java.io.File;
import java.util.Date;
import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.PhotoProvider;
import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.repo.SharedNonCacheRepository;
import no.ntnu.mobapp20g6.app1.data.repo.TaskRepository;

public class NewTaskViewModel extends ViewModel {

    public MutableLiveData<Location> currentLocationLiveData = new MutableLiveData<>();
    public MutableLiveData<String> currentImageBitmapUriLiveData = new MutableLiveData<>();
    public MutableLiveData<Date> currentDateLiveData = new MutableLiveData<>();
    private PhotoProvider pp;

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
                System.out.println("FOUND GROUPP FOR TASK " + groupId.toString());
            } else {
                groupId = null;
            }
            taskRepository.createTask(loginRepository.getToken(), title, description, participantCount, date, groupId, resultCallback::accept);
        }
    }

    public void attachLocationToTask(Task task, Consumer<Result<Task>> attachResultCallback) {
        if (task != null && isLocationSet() && loginRepository.isLoggedIn()) {
            Location location = currentLocationLiveData.getValue();
            sharedNonCacheRepository.addLocationToTask(
                    loginRepository.getToken(),task.getId(),location.getLatitude(),location.getLongitude(),
                    null,null,null,null,
                    attachResultCallback::accept);
        }
    }

    public void attachImageToTask(Task task, String currentPhotoPath, Consumer<Result<Task>> attachImageResultCallback){
        if (task != null && isImageSet() && !(currentPhotoPath.isEmpty())) {
            //TODO: 1. Implement storage of picture in file path
            //TODO: 2. Implement upload of stored picture (bitmap locally OK)
           sharedNonCacheRepository.setTaskImage(loginRepository.getToken(),task.getId(),currentPhotoPath,attachImageResultCallback::accept);
        }
    }

    public boolean setImageUriPathAfterCaptureIntent() {
        if (pp != null) {
            String imageUriPath = pp.currentPhotoPath;
            this.currentImageBitmapUriLiveData.setValue(imageUriPath);
            return imageUriPath != null;
        } else {
            return false;
        }
    }

    public void startImageCaptureIntent(Integer requestCode, Fragment returnFragment, Context context){
        if (context == null || requestCode == null || returnFragment == null) {
            return;
        } else {
            pp = new PhotoProvider(context);
            pp.dispatchTakePictureIntent(requestCode,returnFragment);
        }
    }

    public boolean deleteImageFileAfterCapture() {
       if (pp != null) {
          if (pp.deleteCurrentImageFile()) {
              this.currentImageBitmapUriLiveData.setValue(null);
              return true;
          } else {
              return false;
          }
       } else {
           return false;
       }
    }

    public boolean isDateSet() {
        return currentDateLiveData.getValue() != null ? true : false;
    }

    public boolean isLocationSet() {
        return currentLocationLiveData.getValue() != null ? true : false;
    }

    public boolean isImageSet() {
        return currentImageBitmapUriLiveData.getValue() != null ? true : false;
    }


    public LiveData<Location> getCurrentLocationLiveData() {
        return currentLocationLiveData;
    }

    public LiveData<String> getCurrentImageBitmapUriLiveData() {
        return currentImageBitmapUriLiveData;
    }

    public LiveData<Date> getCurrentDateLiveData() {
        return currentDateLiveData;
    }
}
