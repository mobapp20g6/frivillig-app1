package no.ntnu.mobapp20g6.app1.data.ds;

import android.util.Log;

import java.io.IOException;
import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.data.RestService;
import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.api.ServiceApi;
import no.ntnu.mobapp20g6.app1.data.model.Group;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LocationDataSource {

        private final ServiceApi serviceApi;

        //http response codes
        private static final int OK = 200;
        private static final int BAD_REQUEST = 400;
        private static final int UNAUTHORISED = 401;
        private static final int FORBIDDEN = 403;
        private static final int NOT_FOUND = 404;

        public LocationDataSource() {
            Retrofit rest = RestService.getRetrofitClient();
            serviceApi = rest.create(ServiceApi.class);
        }

        public void addLocationToTask(
                String token, Long taskId,
                Double latitude, Double longitude,
                String streetAddr, String city, Long postal, String country,
                Consumer<Result<Task>> addLocationToTaskCallBack) {
            try {
                if (token == null) {
                    Log.d("FAIL_ADD_LOCATION_TO_TASK", "Token cannot be null when trying add location to task");
                    addLocationToTaskCallBack.accept(new Result.Error(new Exception("Token")));
                } else {
                    // Conversion to string to not break REST API
                    String latString = latitude.toString();
                    String lonString = longitude.toString();
                    Call<Task> addLocationToTaskCall = serviceApi.addLocationToTask(token, taskId,
                            latString, lonString, streetAddr, city, postal, country);
                    addLocationToTaskCall.enqueue(new Callback<Task>() {
                        @Override
                        public void onResponse(Call<Task> call, Response<Task> response) {
                            switch (response.code()) {
                                case OK:
                                    Log.d("OK_ADD_LOCATION_TO_TASK", "Successfully added location to task");
                                    addLocationToTaskCallBack.accept(new Result.Success<>(response.body()));
                                    break;

                                case BAD_REQUEST:
                                    Log.d("FAIL_ADD_LOCATION_TO_TASK", "Task id is missing");
                                    addLocationToTaskCallBack.accept(new Result.Success<>(null));
                                    break;

                                case NOT_FOUND:
                                    Log.d("FAIL_ADD_LOCATION_TO_TASK", "Task was not found.");
                                    addLocationToTaskCallBack.accept(new Result.Success<>(null));
                                    break;

                                case FORBIDDEN:
                                    Log.d("FAIL_ADD_LOCATION_TO_TASK", "User who wants to add location to task is not owner of task");
                                    addLocationToTaskCallBack.accept(new Result.Success<>(null));
                                    break;

                                case UNAUTHORISED:
                                    Log.d("FAIL_ADD_LOCATION_TO_TASK", "User not logged in.");
                                    addLocationToTaskCallBack.accept(new Result.Success<>(null));

                                default:
                                    Log.d("FAIL_ADD_LOCATION_TO_TASK", "Server error");
                                    addLocationToTaskCallBack.accept(new Result.Success<>(null));
                            }
                        }

                        @Override
                        public void onFailure(Call<Task> call, Throwable t) {
                            Log.d("FAIL_ADD_LOCATION_TO_TASK", "No connection");
                            addLocationToTaskCallBack.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                        }
                    });
                }
            } catch (Exception e) {
                Log.d("FAIL_ADD_LOCATION_TO_TASK", "Client error");
                addLocationToTaskCallBack.accept(new Result.Error(new Exception("Client error")));
            }
        }

        public void addLocationToGroup(
                String token, Long groupId,
                String latitude, String longitude,
                String streetAddr, String city, Long postal, String country,
                Consumer<Result<Group>> addLocationToGroupCallBack) {
            try {
                if (token == null) {
                    Log.d("FAIL_ADD_LOCATION_TO_GROUP", "Token cannot be null when trying add location to task");
                    addLocationToGroupCallBack.accept(new Result.Error(new Exception("Token")));
                } else {

                    Call<Group> addLocationToGroupCall = serviceApi.addLocationToGroup(token, groupId,
                            latitude, longitude, streetAddr, city, postal, country);
                    addLocationToGroupCall.enqueue(new Callback<Group>() {
                        @Override
                        public void onResponse(Call<Group> call, Response<Group> response) {
                            switch (response.code()) {
                                case OK:
                                    Log.d("OK_ADD_LOCATION_TO_GROUP", "Successfully added location to task");
                                    addLocationToGroupCallBack.accept(new Result.Success<>(response.body()));
                                    break;

                                case BAD_REQUEST:
                                    Log.d("FAIL_ADD_LOCATION_TO_GROUP", "Task id is missing");
                                    addLocationToGroupCallBack.accept(new Result.Success<>(null));
                                    break;

                                case NOT_FOUND:
                                    Log.d("FAIL_ADD_LOCATION_TO_GROUP", "Task was not found.");
                                    addLocationToGroupCallBack.accept(new Result.Success<>(null));
                                    break;

                                case FORBIDDEN:
                                    Log.d("FAIL_ADD_LOCATION_TO_GROUP", "User who wants to add location to task is not owner of task");
                                    addLocationToGroupCallBack.accept(new Result.Success<>(null));
                                    break;

                                case UNAUTHORISED:
                                    Log.d("FAIL_ADD_LOCATION_TO_GROUP", "User not logged in.");
                                    addLocationToGroupCallBack.accept(new Result.Success<>(null));

                                default:
                                    Log.d("FAIL_ADD_LOCATION_TO_GROUP", "Server error");
                                    addLocationToGroupCallBack.accept(new Result.Success<>(null));
                            }
                        }

                        @Override
                        public void onFailure(Call<Group> call, Throwable t) {
                            Log.d("FAIL_ADD_LOCATION_TO_GROUP", "No connection");
                            addLocationToGroupCallBack.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                        }
                    });
                }
            } catch (Exception e) {
                Log.d("FAIL_ADD_LOCATION_TO_GROUP", "Client error");
                addLocationToGroupCallBack.accept(new Result.Error(new Exception("Client error")));
            }
        }

}
