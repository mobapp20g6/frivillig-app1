package no.ntnu.mobapp20g6.app1.ui.group;

import android.content.Context;
import android.location.Location;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.PhotoProvider;
import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.repo.SharedNonCacheRepository;
import no.ntnu.mobapp20g6.app1.data.model.Group;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;

public class CreateGroupViewModel extends ViewModel {

    private MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> pictureMutableLiveData = new MutableLiveData<>();
    private PhotoProvider photoProvider;

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

    public void addLocToGroup(Long groupID,
                              String latitude, String longitude,
                              String streetAddr, String city, Long postal, String country,
                              Consumer<Group> addLocationToGroupCallBack) {
        sharedRepo.addLocationToGroup(loginRepository.getToken(), groupID,
                                      latitude, longitude,
                                      streetAddr, city, postal, country,
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
        return locationMutableLiveData.getValue() != null;
    }

    public boolean isPictureSet() {
        return pictureMutableLiveData.getValue() != null;
    }

    public MutableLiveData<Location> getLocationMutableLiveData() {
        return locationMutableLiveData;
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
}