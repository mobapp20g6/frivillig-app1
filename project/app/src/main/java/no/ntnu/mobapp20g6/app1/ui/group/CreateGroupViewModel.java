package no.ntnu.mobapp20g6.app1.ui.group;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.utilities.PhotoProvider;
import no.ntnu.mobapp20g6.app1.utilities.GPS;
import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.repo.SharedNonCacheRepository;
import no.ntnu.mobapp20g6.app1.data.model.Group;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;

public class CreateGroupViewModel extends ViewModel {

    private LiveData<Location> locationLiveData;
    private MutableLiveData<String> locationStateMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> pictureMutableLiveData = new MutableLiveData<>();
    private PhotoProvider photoProvider;
    private GPS gps;
    private CountDownTimer gpsFailureTimer;

    private SharedNonCacheRepository sharedRepo;
    private LoginRepository loginRepository;

    public CreateGroupViewModel(SharedNonCacheRepository sharedRepo, LoginRepository loginRepository) {
        this.sharedRepo = sharedRepo;
        this.loginRepository = loginRepository;
    }

    public void getBrregOrg(String groupOrgId, Consumer<JsonObject> brregCallBack) {
        sharedRepo.getVoluntaryBrregOrg(groupOrgId, brregResult -> {
            if (brregResult instanceof Result.Success) {
                JsonObject voluntaryOrg = (JsonObject) ((Result.Success) brregResult).getData();
                brregCallBack.accept(voluntaryOrg);
            }
        });
    }

    public void addLocToGroup(Group group,
                              Consumer<Group> addLocationToGroupCallBack) {
        Location location = locationLiveData.getValue();
        sharedRepo.addLocationToGroup(loginRepository.getToken(), group.getGroupId(),
                String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),
                                      null, null, null, null,
                                      addLocToGroupResult -> {
            if (addLocToGroupResult instanceof Result.Success) {
                Group updatedGroup = (Group) ((Result.Success) addLocToGroupResult).getData();
                addLocationToGroupCallBack.accept(updatedGroup);
            } else {
                addLocationToGroupCallBack.accept(null);
            }
        });
    }

    public void addPicToGroup(Long groupID, String picturePath, Consumer<Group> setGroupPictureCallBack) {
        sharedRepo.setGroupLogo(loginRepository.getToken(), groupID, picturePath, groupResult -> {
            if (groupResult instanceof Result.Success) {
                Group updatedGroup = (Group) ((Result.Success) groupResult).getData();
                setGroupPictureCallBack.accept(updatedGroup);
            } else {
                setGroupPictureCallBack.accept(null);
            }
        });
    }

    public void createGroup(String name, String desc, String orgID, Consumer<Group> createGroupCallBack) {
        Long orgIDnum = null;
        if (!orgID.equals("")) {
            orgIDnum = Long.parseLong(orgID);
        }
        sharedRepo.createGroup(loginRepository.getToken(), name, desc, orgIDnum, (groupResult -> {
            if (groupResult instanceof Result.Success) {
                Group createdGroup = (Group) ((Result.Success) groupResult).getData();
                createGroupCallBack.accept(createdGroup);
            } else {
                createGroupCallBack.accept(null);
            }
        }) );
    }

    public boolean isLocationSet() {
        if (this.locationStateMutableLiveData.getValue().equals("set") && this.locationStateMutableLiveData != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPictureSet() {
        return pictureMutableLiveData.getValue() != null;
    }

    public MutableLiveData<String> getLocationStateMutableLiveData() {
        return locationStateMutableLiveData;
    }

    public LiveData<Location> getLocationLiveData() {
        if (this.gps != null ) {
            return this.gps.getCurrentGPSLocationLiveData();
        } else {
            return this.locationLiveData;
        }
    }

    public MutableLiveData<String> getPictureMutableLiveData() {
        return pictureMutableLiveData;
    }

    public boolean setImageUriPathAfterCaptureIntent() {
        if (photoProvider == null) {
            return false;
        }
        String imageUriPath = photoProvider.currentPhotoPath;
        this.pictureMutableLiveData.setValue(imageUriPath);
        return imageUriPath != null;
    }

    public void startImageCaptureIntent(Integer requestCode, Fragment returnFragment, Context context){
        if (context == null || requestCode == null || returnFragment == null) {
            return;
        } else {
            photoProvider = new PhotoProvider(context);
            photoProvider.dispatchTakePictureIntent(requestCode,returnFragment);
        }
    }

    public boolean deleteImageFileAfterCapture() {
        if (photoProvider == null) {
            return false;
        }
        if (photoProvider.deleteCurrentImageFile()) {
            this.pictureMutableLiveData.setValue(null);
            return true;
        } else return false;
    }

    public void initGps(Context context, Activity activity) {
        System.out.println("GPS init");
        this.gps = new GPS(context,activity);
        this.locationLiveData = gps.getCurrentGPSLocationLiveData();
        gps.askForPermissionGPS();
        gps.getCurrentLocation();
        System.out.println("Asking GPS for permission");
        if (this.gps.hasGpsPermission()) {
            this.locationStateMutableLiveData.setValue("ready");
        } else {
            this.locationStateMutableLiveData.setValue("denied");
        }
    }

    public void startGpsFailureTimer() {
        System.out.println("Failure timer started");
        this.gpsFailureTimer = new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                // Update the UI
                locationStateMutableLiveData.setValue("timeout");
                // Tell GPS to abort operation and stop
                onGpsResultUpdateSetState(null,new Exception("timeout"));
            }
        }.start();
    }

    public void onButtonPressRunGpsBasedOnSetState() {
        if (this.gps != null) {
            System.out.println("Current SetState : " + this.locationStateMutableLiveData.getValue());
            switch (this.locationStateMutableLiveData.getValue()) {
                case "ready":
                case "timeout":
                    if (this.gps.hasGpsPermission()) {
                        this.locationStateMutableLiveData.setValue("aquire");
                        gps.getCurrentLocation();
                        gps.startLocationUpdates();
                    } else {
                        this.locationStateMutableLiveData.setValue("denied");
                    }
                    break;
                case "aquire":
                    break;
                case "set":
                    break;
                case "denied":
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + this.locationStateMutableLiveData.getValue());
            }
        }
    }

    public void onGpsResultUpdateSetState(@Nullable Location currentLocation, @Nullable Exception exception) {
        if (this.gps != null) {
            if (currentLocation == null && exception == null) {
                // Invalid invocation, both shall never be null - as we either have a error or location
            } else {
                // Switch based on the current known state
                switch (locationStateMutableLiveData.getValue()) {
                    case "ready":
                        break;
                    case "aquire":
                        cancelGpsFailureTimer();
                        if (currentLocation != null && exception == null) {
                            //Got location, therefore stopping
                            //TODO: Remove below if we need continious location updates
                            gps.stopLocationUpdates();
                            this.locationStateMutableLiveData.setValue("set");
                        } else {
                            // We got an exception, therefore denied to use GPS
                            this.locationStateMutableLiveData.setValue("denied");
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
                            this.locationStateMutableLiveData.setValue("failed");
                        }
                        //this.currentLocationSetStateLiveData.setValue("ready");
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + this.locationStateMutableLiveData.getValue());
                }
            }
        }
    }

    public void removeGpsAndLiveData() {
        if (this.gps != null) {
            System.out.println("GPS remove");
            this.locationStateMutableLiveData.setValue("ready");
        }
    }

    public void cancelGpsFailureTimer() {
        System.out.println("Failure timer cancelled");
        this.gpsFailureTimer.cancel();
    }
}