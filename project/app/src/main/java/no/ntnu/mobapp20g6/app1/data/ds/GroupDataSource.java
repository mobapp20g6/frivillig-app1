package no.ntnu.mobapp20g6.app1.data.ds;

import android.util.Log;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.data.BrregService;
import no.ntnu.mobapp20g6.app1.data.RestService;
import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.api.BrregAPI;
import no.ntnu.mobapp20g6.app1.data.api.ServiceApi;
import no.ntnu.mobapp20g6.app1.data.model.Group;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GroupDataSource {
    private final ServiceApi serviceApi;
    private final BrregAPI brregApi;

    //http response codes
    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;
    private static final int UNAUTHORISED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;

    public GroupDataSource() {
        Retrofit rest = RestService.getRetrofitClient();
        serviceApi = rest.create(ServiceApi.class);

        Retrofit brreg = BrregService.getRetrofit();
        brregApi = brreg.create(BrregAPI.class);

    }

    public void createGroup(
            String token, String title, String description, Long orgId,
            Consumer<Result<Group>> createGroupCallBack) {
        try {
            if (token == null) {
                Log.d("FAIL-CREATE GROUP", "Token cannot be null when trying to create a group");
                createGroupCallBack.accept(new Result.Error(new Exception("Token")));
            } else {

                Call<Group> createGroupCall = serviceApi.createGroup(token, title, description, orgId);
                createGroupCall.enqueue(new Callback<Group>() {
                    @Override
                    public void onResponse(Call<Group> call, Response<Group> response) {
                        switch (response.code()) {
                            case OK:
                                Log.d("OK-CREATE-GROUP", "User successfully created a group");
                                createGroupCallBack.accept(new Result.Success<>(response.body()));
                                break;

                            case BAD_REQUEST:
                                Log.d("FAIL_CREATE_GROUP", "Group title is missing");
                                createGroupCallBack.accept(new Result.Success<>(null));
                                break;

                            case UNAUTHORISED:
                                Log.d("FAIL_CREATE_GROUP", "User not logged in");
                                createGroupCallBack.accept(new Result.Success<>(null));
                                break;

                            default:
                                Log.d("FAIL_CREATE_GROUP", "Server error");
                                createGroupCallBack.accept(new Result.Success<>(null));
                        }
                    }

                    @Override
                    public void onFailure(Call<Group> call, Throwable t) {
                        Log.d("FAIL_CREATE_GROUP", "No connection");
                        createGroupCallBack.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                    }
                });
            }
        } catch (Exception e) {
            Log.d("FAIL-CREATE-GROUP", "Client error");
            createGroupCallBack.accept(new Result.Error(new Exception("Client error")));
        }
    }

    public void updateGroup(
            String token, String title, String description, Long groupId,
            Consumer<Result<Group>> updateGroupCallBack) {
        try {
            if (token == null) {
                Log.d("FAIL-UPDATE_GROUP", "Token cannot be null when trying to update a group");
                updateGroupCallBack.accept(new Result.Error(new Exception("Token")));
            } else {

                Call<Group> updateGroupCall = serviceApi.updateGroup(token, title, description, groupId);
                updateGroupCall.enqueue(new Callback<Group>() {
                    @Override
                    public void onResponse(Call<Group> call, Response<Group> response) {
                        switch (response.code()) {
                            case OK:
                                Log.d("OK_UPDATE_GROUP", "User successfully update group");
                                updateGroupCallBack.accept(new Result.Success<>(response.body()));
                                break;

                            case BAD_REQUEST:
                                Log.d("FAIL_UPDATE_GROUP", "Group id is missing");
                                updateGroupCallBack.accept(new Result.Success<>(null));
                                break;

                            case NOT_FOUND:
                                Log.d("FAIL_UPDATE_GROUP", "Group id not found");
                                updateGroupCallBack.accept(new Result.Success<>(null));
                                break;

                            case FORBIDDEN:
                                Log.d("FAIL_UPDATE_GROUP", "User is not owner of group");
                                updateGroupCallBack.accept(new Result.Success<>(null));
                                break;

                            case UNAUTHORISED:
                                Log.d("FAIL_UPDATE_GROUP", "User not logged in.");
                                updateGroupCallBack.accept(new Result.Success<>(null));
                                break;

                            default:
                                Log.d("FAIL_UPDATE_GROUP", "Server error");
                                updateGroupCallBack.accept(new Result.Success<>(null));
                        }
                    }

                    @Override
                    public void onFailure(Call<Group> call, Throwable t) {
                        Log.d("FAIL_UPDATE_GROUP", "No connection");
                        updateGroupCallBack.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                    }
                });
            }
        } catch (Exception e) {
            Log.d("FAIL-UPDATE-GROUP", "Client error");
            updateGroupCallBack.accept(new Result.Error(new Exception("Client error")));
        }
    }

    public void addUserToGroup(
            String token, String userId, Long groupId,
            Consumer<Result<Boolean>> addUserToGroupCallBack) {
        try {
            if (token == null) {
                Log.d("FAIL_ADD_USER_TO_GROUP", "Token cannot be null when trying to add a user to a group");
                addUserToGroupCallBack.accept(new Result.Error(new Exception("Token")));
            } else {

                Call<Void> addUserToGroupCall = serviceApi.addUserToGroup(token, userId, groupId);
                addUserToGroupCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        switch (response.code()) {
                            case OK:
                                Log.d("OK_ADD_USER_TO_GROUP", "User successfully added to group");
                                addUserToGroupCallBack.accept(new Result.Success<>(true));
                                break;

                            case BAD_REQUEST:
                                Log.d("FAIL_ADD_USER_TO_GROUP", "Either UserID or GroupID is missing");
                                addUserToGroupCallBack.accept(new Result.Success<>(false));
                                break;

                            case NOT_FOUND:
                                Log.d("FAIL_ADD_USER_TO_GROUP", "Either User or Group not found.");
                                addUserToGroupCallBack.accept(new Result.Success<>(false));
                                break;

                            case FORBIDDEN:
                                Log.d("FAIL_ADD_USER_TO_GROUP", "Either User adding user to group is not owner or User to be add is already added");
                                addUserToGroupCallBack.accept(new Result.Success<>(false));
                                break;

                            case UNAUTHORISED:
                                Log.d("FAIL_ADD_USER_TO_GROUP", "User not logged in.");
                                addUserToGroupCallBack.accept(new Result.Success<>(false));

                            default:
                                Log.d("FAIL_UPDATE_GROUP", "Server error");
                                addUserToGroupCallBack.accept(new Result.Success<>(null));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("FAIL_ADD_USER_TO_GROUP", "No connection");
                        addUserToGroupCallBack.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                    }
                });
            }
        } catch (Exception e) {
            Log.d("FAIL_ADD_USER_TO_GROUP", "Client error");
            addUserToGroupCallBack.accept(new Result.Error(new Exception("Client error")));
        }
    }

    public void getAllGroupTasks(
            String token, Long groupId,
            Consumer<Result<List<Task>>> getAllGroupTasksCallBack) {
        try {
            if (token == null) {
                Log.d("FAIL_GET_ALL_GROUP_TASKS", "Token cannot be null when trying to get all group tasks");
                getAllGroupTasksCallBack.accept(new Result.Error(new Exception("Token")));
            } else {

                Call<List<Task>> getAllGroupTasksCall = serviceApi.getAllGroupTasks(token, groupId);
                getAllGroupTasksCall.enqueue(new Callback<List<Task>>() {
                    @Override
                    public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                        switch (response.code()) {
                            case OK:
                                Log.d("OK_GET_ALL_GROUP_TASKS", "Successfully retrieved all tasks added to group");
                                getAllGroupTasksCallBack.accept(new Result.Success<>(response.body()));
                                break;

                            case BAD_REQUEST:
                                Log.d("FAIL_GET_ALL_GROUP_TASKS", "Group id is missing");
                                getAllGroupTasksCallBack.accept(new Result.Success<>(null));
                                break;

                            case NOT_FOUND:
                                Log.d("FAIL_GET_ALL_GROUP_TASKS", "Group was not found.");
                                getAllGroupTasksCallBack.accept(new Result.Success<>(null));
                                break;

                            case FORBIDDEN:
                                Log.d("FAIL_GET_ALL_GROUP_TASKS", "User who wants to see group tasks is not a member of the group");
                                getAllGroupTasksCallBack.accept(new Result.Success<>(null));
                                break;

                            case UNAUTHORISED:
                                Log.d("FAIL_GET_ALL_GROUP_TASKS", "User not logged in.");
                                getAllGroupTasksCallBack.accept(new Result.Success<>(null));

                            default:
                                Log.d("FAIL_GET_ALL_GROUP_TASKS", "Server error");
                                Log.d("FAIL_GET_ALL_GROUP_TASKS", "Response code: " + response.code());
                                getAllGroupTasksCallBack.accept(new Result.Success<>(null));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Task>> call, Throwable t) {
                        Log.d("FAIL_GET_ALL_GROUP_TASKS", "No connection");
                        getAllGroupTasksCallBack.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                    }
                });
            }
        } catch (Exception e) {
            Log.d("FAIL_GET_ALL_GROUP_TASKS", "Client error");
            Log.d("FAIL_GET_ALL_GROUP_TASKS", e.getMessage());
            getAllGroupTasksCallBack.accept(new Result.Error(new Exception("Client error")));
        }
    }

    public void isOwnerOfGroup(
            String token, Long groupId,
            Consumer<Result<Boolean>> isOwnerOfGroupCallBack) {
        try {
            if (token == null) {
                Log.d("FAIL_IS_OWNER_OF_GROUP", "Token cannot be null when trying see if user is owner of group");
                isOwnerOfGroupCallBack.accept(new Result.Error(new Exception("Token")));
            } else {

                Call<Void> isOwnerOfGroupCall = serviceApi.isOwnerOfGroup(token, groupId);
                isOwnerOfGroupCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        switch (response.code()) {
                            case OK:
                                Log.d("OK_IS_OWNER_OF_GROUP", "User is owner of Group");
                                isOwnerOfGroupCallBack.accept(new Result.Success<>(true));
                                break;

                            case BAD_REQUEST:
                                Log.d("FAIL_IS_OWNER_OF_GROUP", "Group id is missing");
                                isOwnerOfGroupCallBack.accept(new Result.Success<>(false));
                                break;

                            case NOT_FOUND:
                                Log.d("FAIL_IS_OWNER_OF_GROUP", "Group was not found.");
                                isOwnerOfGroupCallBack.accept(new Result.Success<>(false));
                                break;

                            case FORBIDDEN:
                                Log.d("FAIL_IS_OWNER_OF_GROUP", "User is not the owner of the group");
                                isOwnerOfGroupCallBack.accept(new Result.Success<>(false));
                                break;

                            case UNAUTHORISED:
                                Log.d("FAIL_IS_OWNER_OF_GROUP", "User not logged in.");
                                isOwnerOfGroupCallBack.accept(new Result.Success<>(false));

                            default:
                                Log.d("FAIL_IS_OWNER_OF_GROUP", "Server error");
                                isOwnerOfGroupCallBack.accept(new Result.Success<>(false));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("FAIL_IS_OWNER_OF_GROUP", "No connection");
                        isOwnerOfGroupCallBack.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                    }
                });
            }
        } catch (Exception e) {
            Log.d("FAIL_IS_OWNER_OF_GROUP", "Client error");
            Log.d("FAIL_IS_OWNER_OF_GROUP", e.getMessage());
            isOwnerOfGroupCallBack.accept(new Result.Error(new Exception("Client error")));
        }
    }

    public void getBrregOrg(
            String orgId,
            Consumer<Result<JsonObject>> brregCallback) {
        try {
            Call<JsonObject> brregCall = brregApi.getVoluntaryBrregOrg(orgId, true);
            brregCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    switch (response.code()) {
                        case OK:
                            Log.d("OK_VOLUNTARY_ORG_FOUND", "Voluntary Org Found");
                            brregCallback.accept(new Result.Success<>(response.body()));
                            break;

                        case BAD_REQUEST:
                            Log.d("FAIL_VOLUNTARY_ORG_FOUND", "Voluntary Org Bad Request");
                            brregCallback.accept(new Result.Success<>(null));
                            break;

                        case NOT_FOUND:
                            Log.d("FAIL_VOLUNTARY_ORG_FOUND", "Voluntary Org Not Found.");
                            brregCallback.accept(new Result.Success<>(null));
                            break;

                        default:
                            Log.d("FAIL_VOLUNTARY_ORG_FOUND", "Server error");
                            brregCallback.accept(new Result.Success<>(null));
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d("Failure", t.getMessage());
                    Log.d("FAIL_IS_OWNER_OF_GROUP", "No connection");
                    brregCallback.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                }
            });
        } catch (Exception e) {
            Log.d("FAIL_VOLUNTARY_ORG_FOUND", "Client error");
            Log.d("Failure", e.getMessage());
            brregCallback.accept(new Result.Error(new Exception("Client error")));
        }
    }
}
