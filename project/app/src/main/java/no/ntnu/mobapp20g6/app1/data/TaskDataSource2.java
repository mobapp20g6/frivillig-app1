package no.ntnu.mobapp20g6.app1.data;

import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.data.api.ServiceApi;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TaskDataSource2 {
    private final ServiceApi serviceApi;

    //http response codes
    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;
    private static final int UNAUTHORISED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;

    public TaskDataSource2() {
        Retrofit rest = RestService.getRetrofitClient();
        serviceApi = rest.create(ServiceApi.class);
    }

    /**
     * List all tasks.
     * @param token Bearer Auth token.
     * @param listTasksCallback Results:
     *                          Result.OK = List of tasks
     */
    public void listTasks(
            String token,
            Consumer<Result<List<Task>>> listTasksCallback) {
        try {
            if (token != null) {
                Call<List<Task>> listTaskCall = serviceApi.getAllTasks(token);
                listTaskCall.enqueue(new Callback<List<Task>>() {
                    @Override
                    public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                        switch (response.code()) {
                            case OK:
                                Log.d("OK-LIST_TASK", "Listing tasks");
                                listTasksCallback.accept(new Result.Success<>(response.body()));
                                break;

                            case BAD_REQUEST:
                                Log.d("FAIL-LIST_TASK", "Invalid input");
                                listTasksCallback.accept(new Result.Success<>(null));
                                break;

                            case UNAUTHORISED:
                                Log.d("FAIL-LIST_TASK", "User not logged in");
                                listTasksCallback.accept(new Result.Success<>(null));
                                break;

                            case NOT_FOUND:
                                Log.d("FAIL-LIST_TASK", "Tasks not found");
                                listTasksCallback.accept(new Result.Success<>(null));
                                break;

                            default:
                                Log.d("FAIL-LIST_TASK", "Server error");
                                listTasksCallback.accept(new Result.Error(new Exception("Server")));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Task>> call, Throwable t) {
                        Log.d("FAIL-LIST_TASK", "No connection");
                        listTasksCallback.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                    }
                });
            } else {
                listTasksCallback.accept(new Result.Error(new Exception("Token")));
                Log.d("FAIL-LIST_TASK", "Token cannot be null when trying to get tasks");
            }
        } catch (Exception e) {
            Log.d("FAIL-LIST_TASK", "Client error");
            listTasksCallback.accept(new Result.Error(new Exception("Client error")));
        }
    }

    /**
     * List owned or assigned tasks.
     * @param token Bearer Auth token.
     * @param ownedTasks if true result is owned tasks, if false result is assigned tasks.
     * @param listOwnedTasksCallback
     */
    public void listMyTasks(
            String token,
            boolean ownedTasks,
            Consumer<Result<List<Task>>> listOwnedTasksCallback) {
        try {
            if (token != null) {
                Call<List<Task>> listMyTaskCall = serviceApi.getMyTasks(token, ownedTasks);
                listMyTaskCall.enqueue(new Callback<List<Task>>() {
                    @Override
                    public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                        switch (response.code()) {
                            case OK:
                                Log.d("OK-LIST_MY_TASK", "Listing tasks");
                                listOwnedTasksCallback.accept(new Result.Success<>(response.body()));
                                break;

                            case BAD_REQUEST:
                                Log.d("FAIL-LIST_MY_TASK", "Invalid input");
                                listOwnedTasksCallback.accept(new Result.Success<>(null));
                                break;

                            case UNAUTHORISED:
                                Log.d("FAIL-LIST_MY_TASK", "User not logged in");
                                listOwnedTasksCallback.accept(new Result.Success<>(null));
                                break;

                            case NOT_FOUND:
                                Log.d("FAIL-LIST_MY_TASK", "Tasks not found");
                                listOwnedTasksCallback.accept(new Result.Success<>(null));
                                break;

                            default:
                                Log.d("FAIL-LIST_MY_TASK", "Server error");
                                listOwnedTasksCallback.accept(new Result.Error(new Exception("Server")));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Task>> call, Throwable t) {
                        Log.d("FAIL-LIST_MY_TASK", "No connection");
                        listOwnedTasksCallback.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                    }
                });
            } else {
                listOwnedTasksCallback.accept(new Result.Error(new Exception("Token")));
                Log.d("FAIL-LIST_MY_TASK", "Token cannot be null when trying to get tasks");
            }
        } catch (Exception e) {
            Log.d("FAIL-LIST_MY_TASK", "Client error");
            listOwnedTasksCallback.accept(new Result.Error(new Exception("Client error")));
        }
    }

    public void getTask(
            String token,
            Long taskId,
            Consumer<Result<Task>> getTaskCallback) {
        try {
            if (token != null) {
                Call<Task> getTaskCall = serviceApi.getTask(token, taskId);
                getTaskCall.enqueue(new Callback<Task>() {
                    @Override
                    public void onResponse(Call<Task> call, Response<Task> response) {
                        switch (response.code()) {
                            case OK:
                                Log.d("OK-GET_TASK", "Returning task");
                                getTaskCallback.accept(new Result.Success<>(response.body()));
                                break;

                            case BAD_REQUEST:
                                Log.d("FAIL-GET_TASK", "Invalid input");
                                getTaskCallback.accept(new Result.Success<>(null));
                                break;

                            case UNAUTHORISED:
                                Log.d("FAIL-GET_TASK", "User not logged in");
                                getTaskCallback.accept(new Result.Success<>(null));
                                break;

                            case NOT_FOUND:
                                Log.d("FAIL-GET_TASK", "Task not found");
                                getTaskCallback.accept(new Result.Success<>(null));
                                break;

                            default:
                                Log.d("FAIL-GET_TASK", "Server error");
                                getTaskCallback.accept(new Result.Error(new Exception("Server")));
                        }
                    }

                    @Override
                    public void onFailure(Call<Task> call, Throwable t) {
                        Log.d("FAIL-GET_TASK", "No connection");
                        getTaskCallback.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                    }
                });
            } else {
                getTaskCallback.accept(new Result.Error(new Exception("Token")));
                Log.d("FAIL-GET_TASK", "Token cannot be null when trying to get task");
            }
        } catch (Exception e) {
            Log.d("FAIL-GET_TASK", "Client error");
            getTaskCallback.accept(new Result.Error(new Exception("Client error")));
        }
    }

    public void createTask(
            String token, String title, String description, Long maxUsers, String scheduleDate, Long groupId,
            Consumer<Result<Task>> createTaskCallback) {
        try {
            if (token != null) {
                Call<Task> createTaskCall = serviceApi.createTask(token, title, description, maxUsers, scheduleDate, groupId);
                createTaskCall.enqueue(new Callback<Task>() {
                    @Override
                    public void onResponse(Call<Task> call, Response<Task> response) {
                        switch (response.code()) {
                            case OK:
                                Log.d("OK-CREATE_TASK", "Returning task");
                                createTaskCallback.accept(new Result.Success<>(response.body()));
                                break;

                            case BAD_REQUEST:
                                Log.d("FAIL-CREATE_TASK", "Invalid input");
                                createTaskCallback.accept(new Result.Success<>(null));
                                break;

                            case UNAUTHORISED:
                                Log.d("FAIL-CREATE_TASK", "User not logged in");
                                createTaskCallback.accept(new Result.Success<>(null));
                                break;

                            case NOT_FOUND:
                                Log.d("FAIL-CREATE_TASK", "Group not found");
                                createTaskCallback.accept(new Result.Success<>(null));
                                break;

                            default:
                                Log.d("FAIL-CREATE_TASK", "Server error");
                                createTaskCallback.accept(new Result.Error(new Exception("Server")));
                        }
                    }

                    @Override
                    public void onFailure(Call<Task> call, Throwable t) {
                        Log.d("FAIL-CREATE_TASK", "No connection");
                        createTaskCallback.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                    }
                });
            } else {
                createTaskCallback.accept(new Result.Error(new Exception("Token")));
                Log.d("FAIL-CREATE_TASK", "Token cannot be null when trying to create task");
            }
        } catch (Exception e) {
            Log.d("FAIL-CREATE_TASK", "Client error");
            createTaskCallback.accept(new Result.Error(new Exception("Client error")));
        }
    }

    public void deleteTask(
            String token,
            Long taskId,
            Consumer<Result<Boolean>> deleteTaskCallback) {
        try {
            if (token != null) {
                Call<Void> deleteTaskCall = serviceApi.removeTask(token, taskId);
                deleteTaskCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        switch (response.code()) {
                            case OK:
                                Log.d("OK-DELETE_TASK", "Task was removed");
                                deleteTaskCallback.accept(new Result.Success<>(true));
                                break;

                            case BAD_REQUEST:
                                Log.d("FAIL-DELETE_TASK", "Invalid input");
                                deleteTaskCallback.accept(new Result.Success<>(false));
                                break;

                            case UNAUTHORISED:
                                Log.d("FAIL-DELETE_TASK", "User not logged in");
                                deleteTaskCallback.accept(new Result.Success<>(false));
                                break;

                            case FORBIDDEN:
                                Log.d("FAIL-DELETE_TASK", "User is not the owner of task");
                                deleteTaskCallback.accept(new Result.Success<>(false));
                                break;

                            case NOT_FOUND:
                                Log.d("FAIL-DELETE_TASK", "Task not found");
                                deleteTaskCallback.accept(new Result.Success<>(false));
                                break;

                            default:
                                Log.d("FAIL-DELETE_TASK", "Server error");
                                deleteTaskCallback.accept(new Result.Error(new Exception("Server")));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("FAIL-DELETE_TASK", "No connection");
                        deleteTaskCallback.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                    }
                });
            } else {
                deleteTaskCallback.accept(new Result.Error(new Exception("Token")));
                Log.d("FAIL-DELETE_TASK", "Token cannot be null when trying to delete task");
            }
        } catch (Exception e) {
            Log.d("FAIL-DELETE_TASK", "Client error");
            deleteTaskCallback.accept(new Result.Error(new Exception("Client error")));
        }
    }

    public void updateTask(
            String token, String title, String description, Long maxUsers, String scheduleDate, Long groupId, Long taskId,
            Consumer<Result<Task>> updateTaskCallback) {
        try {
            if (token != null) {
                Call<Task> updateTaskCall = serviceApi.updateTask(token, title, description, maxUsers, scheduleDate, groupId, taskId);
                updateTaskCall.enqueue(new Callback<Task>() {
                    @Override
                    public void onResponse(Call<Task> call, Response<Task> response) {
                        switch (response.code()) {
                            case OK:
                                Log.d("OK-UPDATE_TASK", "Returning task");
                                updateTaskCallback.accept(new Result.Success<>(response.body()));
                                break;

                            case BAD_REQUEST:
                                Log.d("FAIL-UPDATE_TASK", "Invalid input");
                                updateTaskCallback.accept(new Result.Success<>(null));
                                break;

                            case UNAUTHORISED:
                                Log.d("FAIL-UPDATE_TASK", "User not logged in");
                                updateTaskCallback.accept(new Result.Success<>(null));
                                break;

                            case FORBIDDEN:
                                Log.d("FAIL-UPDATE_TASK", "User is not the owner of task");
                                updateTaskCallback.accept(new Result.Success<>(null));
                                break;

                            case NOT_FOUND:
                                Log.d("FAIL-UPDATE_TASK", "Group or task not found");
                                updateTaskCallback.accept(new Result.Success<>(null));
                                break;

                            default:
                                Log.d("FAIL-UPDATE_TASK", "Server error");
                                updateTaskCallback.accept(new Result.Error(new Exception("Server")));
                        }
                    }

                    @Override
                    public void onFailure(Call<Task> call, Throwable t) {
                        Log.d("FAIL-UPDATE_TASK", "No connection");
                        updateTaskCallback.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                    }
                });
            } else {
                updateTaskCallback.accept(new Result.Error(new Exception("Token")));
                Log.d("FAIL-UPDATE_TASK", "Token cannot be null when trying to update task");
            }
        } catch (Exception e) {
            Log.d("FAIL-UPDATE_TASK", "Client error");
            updateTaskCallback.accept(new Result.Error(new Exception("Client error")));
        }
    }

    public void joinTask(
            String token, Long taskId,
            Consumer<Result<Boolean>> joinTaskCallback) {
        try {
            if (token != null) {
                Call<Void> joinTaskCall = serviceApi.joinTask(token, taskId);
                joinTaskCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        switch (response.code()) {
                            case OK:
                                Log.d("OK-JOIN_TASK", "User successfully joined task");
                                joinTaskCallback.accept(new Result.Success<>(true));
                                break;

                            case BAD_REQUEST:
                                Log.d("FAIL-JOIN_TASK", "Invalid input");
                                joinTaskCallback.accept(new Result.Success<>(false));
                                break;

                            case UNAUTHORISED:
                                Log.d("FAIL-JOIN_TASK", "User not logged in");
                                joinTaskCallback.accept(new Result.Success<>(false));
                                break;

                            case FORBIDDEN:
                                Log.d("FAIL-JOIN_TASK", "User is already member of task or task is full");
                                joinTaskCallback.accept(new Result.Success<>(false));
                                break;

                            case NOT_FOUND:
                                Log.d("FAIL-JOIN_TASK", "Task not found");
                                joinTaskCallback.accept(new Result.Success<>(false));
                                break;

                            default:
                                Log.d("FAIL-JOIN_TASK", "Server error");
                                joinTaskCallback.accept(new Result.Error(new Exception("Server")));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("FAIL-JOIN_TASK", "No connection");
                        joinTaskCallback.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                    }
                });
            } else {
                joinTaskCallback.accept(new Result.Error(new Exception("Token")));
                Log.d("FAIL-JOIN_TASK", "Token cannot be null when trying to join a task");
            }
        } catch (Exception e) {
            Log.d("FAIL-JOIN_TASK", "Client error");
            joinTaskCallback.accept(new Result.Error(new Exception("Client error")));
        }
    }
}
