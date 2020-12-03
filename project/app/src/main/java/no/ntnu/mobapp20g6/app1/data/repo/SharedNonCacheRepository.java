package no.ntnu.mobapp20g6.app1.data.repo;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.ds.GroupDataSource;
import no.ntnu.mobapp20g6.app1.data.ds.LocationDataSource;
import no.ntnu.mobapp20g6.app1.data.ds.PictureDataSource;
import no.ntnu.mobapp20g6.app1.data.model.Group;
import no.ntnu.mobapp20g6.app1.data.model.Task;

/**
 * Requests information regarding Group and Location.
 * Does not hold caches of this information.
 * Is a singleton.
 * @author maardal, TrymV
 */
public class SharedNonCacheRepository {

    private static volatile SharedNonCacheRepository instance;

    private final GroupDataSource groupDataSource;
    private final LocationDataSource locationDataSource;
    private final PictureDataSource pictureDataSource;

    private SharedNonCacheRepository(GroupDataSource groupDataSource, LocationDataSource locationDataSource, PictureDataSource pictureDataSource) {
        this.groupDataSource = groupDataSource;
        this.locationDataSource = locationDataSource;
        this.pictureDataSource = pictureDataSource;
    }

    public static SharedNonCacheRepository getInstance(GroupDataSource groupDataSource, LocationDataSource locationDataSource, PictureDataSource pictureDataSource) {
        if (instance == null) {
            instance = new SharedNonCacheRepository(groupDataSource, locationDataSource, pictureDataSource);
        }
        return instance;
    }

    public void createGroup(String token, String title, String description, Long orgId,
                            Consumer<Result<Group>> createGroupCallBack) {
        groupDataSource.createGroup(token, title, description, orgId, (createGroupResult) -> {
            createGroupCallBack.accept(createGroupResult);
        });
    }

    public void updateGroup(String token, String title, String description, Long groupId,
                            Consumer<Result<Group>> updateGroupCallBack) {
        groupDataSource.updateGroup(token, title, description, groupId, (updateGroupResult) -> {
            updateGroupCallBack.accept(updateGroupResult);
        });
    }

    public void addUserToGroup(String token, String userId, Long groupId,
                               Consumer<Result<Boolean>> addUserToGroupCallBack) {
        groupDataSource.addUserToGroup(token, userId, groupId, (adduserToGroupResult) -> {
            addUserToGroupCallBack.accept(adduserToGroupResult);
        });
    }

    public void getAllGroupTasks(String token, Long groupId,
                                 Consumer<Result<List<Task>>> getAllGroupTasksCallBack) {
        groupDataSource.getAllGroupTasks(token, groupId, (getAllGroupTasksResult) -> {
            getAllGroupTasksCallBack.accept(getAllGroupTasksResult);
        });
    }

    public void isOwnerOfGroup(String token, Long groupId,
                               Consumer<Result<Boolean>> isOwnerOfGroupCallBack) {
        groupDataSource.isOwnerOfGroup(token, groupId, (isOwnerOfGroupResult) -> {
            isOwnerOfGroupCallBack.accept(isOwnerOfGroupResult);
        });
    }

    public void addLocationToTask(String token, Long taskId,
                                  Double latitude, Double longitude,
                                  String streetAddr, String city, Long postal, String country,
                                  Consumer<Result<Task>> addLocationToTaskCallBack) {
        locationDataSource.addLocationToTask(token, taskId,
                latitude, longitude,
                streetAddr, city, postal, country,
                (addLocationToTaskResult) -> {
            addLocationToTaskCallBack.accept(addLocationToTaskResult);
                });
    }

    public void addLocationToGroup(String token, Long groupId,
                                   String latitude, String longitude,
                                   String streetAddr, String city, Long postal, String country,
                                   Consumer<Result<Group>> addLocationToGroupCallBack) {
        locationDataSource.addLocationToGroup(token, groupId,
                latitude, longitude,
                streetAddr, city, postal, country,
                (addLocationToGroupResult) -> {
            addLocationToGroupCallBack.accept(addLocationToGroupResult);
                });
    }

    public void setTaskImage(String token, Long taskId, String picturePath, Consumer<Result<Task>>setTaskImageCallback) {
        pictureDataSource.setTaskImage(token, taskId, picturePath, setTaskImageCallback);
    }

    public void setGroupLogo(String token, Long groupId, String picturePath, Consumer<Result<Group>>setGroupLogoCallback) {
        pictureDataSource.setGroupLogo(token, groupId, picturePath, setGroupLogoCallback);
    }

    public void getVoluntaryBrregOrg(String orgid, Consumer<Result<JsonObject>> getVoluntaryBrreggOrgCallBack) {
        groupDataSource.getBrregOrg(orgid, brreggResult -> {
            getVoluntaryBrreggOrgCallBack.accept(brreggResult);
        });
    }
}
