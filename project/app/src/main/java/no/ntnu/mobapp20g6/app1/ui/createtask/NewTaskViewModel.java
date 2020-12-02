package no.ntnu.mobapp20g6.app1.ui.createtask;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;
import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.PhotoProvider;
import no.ntnu.mobapp20g6.app1.data.GPS;
import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;
import no.ntnu.mobapp20g6.app1.data.repo.SharedNonCacheRepository;
import no.ntnu.mobapp20g6.app1.data.repo.TaskRepository;

public class NewTaskViewModel extends ViewModel {

    private LiveData<Location> currentLocationLiveData;
    private MutableLiveData<String> currentLocationSetStateLiveData = new MutableLiveData<>();
    public MutableLiveData<String> currentImageBitmapUriLiveData = new MutableLiveData<>();
    public MutableLiveData<Date> currentDateLiveData = new MutableLiveData<>();
    private PhotoProvider pp;
    private GPS gps;
    private CountDownTimer gpsFailureTimer;

    LoginRepository loginRepository;
    TaskRepository taskRepository;
    SharedNonCacheRepository sharedNonCacheRepository;

    public NewTaskViewModel(LoginRepository loginRepository, TaskRepository taskRepository, SharedNonCacheRepository sharedNonCacheRepository) {
        this.loginRepository = loginRepository;
        this.taskRepository = taskRepository;
        this.sharedNonCacheRepository = sharedNonCacheRepository;

        // Populate with empty LiveData to avoid null-pointer in Fragment (if GPS fails to init)
        this.currentLocationLiveData = new MutableLiveData<>();

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

    /**
     *  Start a image capture intent with the PhotoProvider. Used to start the camera action
     * @param requestCode The request code used by this fragment to handle the camera intent
     * @param returnFragment The return fragment to goto after the picture was taken
     * @param context The context used to create the intent inside the PhotoProvider
     */
    public void startImageCaptureIntent(Integer requestCode, Fragment returnFragment, Context context){
        if (context == null || requestCode == null || returnFragment == null) {
            return;
        } else {
            pp = new PhotoProvider(context);
            pp.dispatchTakePictureIntent(requestCode,returnFragment);
        }
    }

    /**
     * This function is used to delete the image file on disk.
     * Used to delete file after upload + if user cancels the camera action
     * @return
     */
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

    /**
     * Initalizes the GPS/GMS (Google Play Services) location provider class
     * @param context the context is used to check and set the permissions intent
     * @param activity the activity is used to associate the listeners for returning a
     *                 position
     */
    public void initGps(Context context, Activity activity) {
        System.out.println("GPS init");
        this.gps = new GPS(context,activity);
        this.currentLocationLiveData = gps.getCurrentGPSLocationLiveData();
        gps.askForPermissionGPS();
        gps.getCurrentLocation();
        System.out.println("Asking GPS for permission");
        if (this.gps.hasGpsPermission()) {
            this.currentLocationSetStateLiveData.setValue("ready");
        } else {
            this.currentLocationSetStateLiveData.setValue("denied");
        }
    }

    /**
     *  Updates the current gps state if the GPS sends us a location via Live Data or an
     *  exception (not implemented at GPS class - possible not nessecary)
     * @param currentLocation The location object received from the GPS by Livedata
     * @param exception THe exception object received from the GPS by Livedata
     */
    public void onGpsResultUpdateSetState(@Nullable Location currentLocation, @Nullable Exception exception) {
        if (this.gps != null) {
            if (currentLocation == null && exception == null) {
                // Invalid invocation, both shall never be null - as we either have a error or location
            } else {
                // Switch based on the current known state
                switch (currentLocationSetStateLiveData.getValue()) {
                    case "ready":
                        break;
                    case "aquire":
                        cancelGpsFailureTimer();
                        if (currentLocation != null && exception == null) {
                            //Got location, therefore stopping
                            //TODO: Remove below if we need continious location updates
                            gps.stopLocationUpdates();
                            this.currentLocationSetStateLiveData.setValue("set");
                        } else {
                            // We got an exception, therefore denied to use GPS
                            this.currentLocationSetStateLiveData.setValue("denied");
                        }
                        break;
                    case "set":
                        break;
                    case "denied":
                        break;
                    case "timeout":
                        gps.stopLocationUpdates();
                        gps.askForPermissionGPS();
                        if (gps.hasGpsPermission() == false) {
                            this.currentLocationSetStateLiveData.setValue("failed");
                        }
                        //this.currentLocationSetStateLiveData.setValue("ready");
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + this.currentLocationSetStateLiveData.getValue());
                }
            }
        }
    }

    /**
     *  Run a sensible GPS action based on the current state
     *  if the user presses a button to get GPS position
     */
    public void onButtonPressRunGpsBasedOnSetState() {
        if (this.gps != null) {
            System.out.println("Current SetState : " + this.currentLocationSetStateLiveData.getValue());
            switch (this.currentLocationSetStateLiveData.getValue()) {
                case "ready":
                case "timeout":
                    if (this.gps.hasGpsPermission()) {
                        this.currentLocationSetStateLiveData.setValue("aquire");
                        gps.getCurrentLocation();
                        gps.startLocationUpdates();
                    } else {
                        this.currentLocationSetStateLiveData.setValue("denied");
                    }
                    break;
                case "aquire":
                    break;
                case "set":
                    break;
                case "denied":
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + this.currentLocationSetStateLiveData.getValue());
            }
        }
    }

    /**
     * Tell GPS to stop to aquire position
     */
    public void stopGetGpsPosition() {
        if (this.gps != null) {
            this.gps.stopLocationUpdates();
        }
    }

    /**
     * Set the current GPS state to "ready" as then it won't be sent
     * to the server. We cannot remove the GPS read-only
     * data object - therefore this is needed
     */
    public void removeGpsAndLiveData() {
        if (this.gps != null) {
            System.out.println("GPS remove");
            this.currentLocationSetStateLiveData.setValue("ready");
        }

    }

    public boolean isDateSet() {
        return currentDateLiveData.getValue() != null ? true : false;
    }


    /**
     * Used to attach the location when creating a task.
     * If the location is percieved as SET by both this class and the GPS
     * we will return true
     * @return true if the location is SET, false if no data or unset by failure/user input
     */
    public boolean isLocationSet() {
        if (this.currentLocationSetStateLiveData.getValue().equals("set") && this.currentLocationLiveData != null) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isImageSet() {
        return currentImageBitmapUriLiveData.getValue() != null ? true : false;
    }


    public LiveData<Location> getCurrentLocationLiveData() {
        if (this.gps != null ) {
            return this.gps.getCurrentGPSLocationLiveData();
        } else {
            return this.currentLocationLiveData;
        }
    }

    public LiveData<String> getCurrentLocationSetStateLiveData() {
        return currentLocationSetStateLiveData;
    }

    public LiveData<String> getCurrentImageBitmapUriLiveData() {
        return currentImageBitmapUriLiveData;
    }

    public LiveData<Date> getCurrentDateLiveData() {
        return currentDateLiveData;
    }

    /**
     *  This timer updates the UI if the GPS/GMS (Google Play Srvs) is not able to get a
     *  position fix within a sensible amount of time. Currently this is set to 5 secounds
     */
   public void startGpsFailureTimer() {
       System.out.println("Failure timer started");
       this.gpsFailureTimer = new CountDownTimer(5000, 1000) {

           @Override
           public void onTick(long l) {
           }

           @Override
           public void onFinish() {
               // Update the UI
               currentLocationSetStateLiveData.setValue("timeout");
               // Tell GPS to abort operation and stop
               onGpsResultUpdateSetState(null,new Exception("timeout"));
           }
       }.start();
   }

    /**
     * This will cancel the failure timer if a position was received within the timeout
     */
   public void cancelGpsFailureTimer() {
       System.out.println("Failure timer cancelled");
        this.gpsFailureTimer.cancel();
   }
}
