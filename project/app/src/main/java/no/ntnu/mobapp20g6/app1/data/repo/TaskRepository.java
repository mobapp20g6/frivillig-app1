package no.ntnu.mobapp20g6.app1.data.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.ds.TaskDataSource;
import no.ntnu.mobapp20g6.app1.data.model.Task;

public class TaskRepository {

    private static volatile TaskRepository instance;
    private final TaskDataSource taskDataSource;

    private final MutableLiveData<List<Task>> liveDataTaskList;
    private final MutableLiveData<List<Task>> liveDataAssignedTasks;
    private final MutableLiveData<List<Task>> liveDataOwnedTasks;

    //For singleton access
    private TaskRepository(TaskDataSource taskDataSource) {
        this.taskDataSource = taskDataSource;
        liveDataTaskList = new MutableLiveData<>();
        liveDataAssignedTasks = new MutableLiveData<>();
        liveDataOwnedTasks = new MutableLiveData<>();
    }

    public LiveData<List<Task>> getLiveDataTaskList() {
        return liveDataTaskList;
    }

    public LiveData<List<Task>> getLiveDataAssignedTasks() {
        return liveDataAssignedTasks;
    }

    public LiveData<List<Task>> getLiveDataOwnedTasks() {
        return liveDataOwnedTasks;
    }

    public static TaskRepository getInstance(TaskDataSource taskDataSource) {
        if(instance == null) {
            instance = new TaskRepository(taskDataSource);
        }
        return instance;
    }

    /**
     * Return a list of all public tasks as a callback.
     * @param token Auth Bearer token.
     * @param listTasksCallback
     */
    public void getPublicTasks(String token, Consumer<Result<List<Task>>> listTasksCallback) {
        taskDataSource.listTasks(token, (Result<List<Task>> result)-> {
            if(result instanceof Result.Success) {
                liveDataTaskList.postValue(((Result.Success<List<Task>>) result).getData());
            }
            listTasksCallback.accept(result);
        });
    }

    /**
     * Return a list of assigned tasks as a callback.
     * @param token Auth Bearer token.
     * @param assignedTasksCallback
     */
    public void getAssignedTasks(String token, Consumer<Result<List<Task>>> assignedTasksCallback) {
        taskDataSource.listMyTasks(token, false, (Result<List<Task>> result)-> {
           if(result instanceof Result.Success) {
               liveDataAssignedTasks.postValue(((Result.Success<List<Task>>) result).getData());
           }
           assignedTasksCallback.accept(result);
        });
    }

    /**
     * Return a list of owned tasks as a callback.
     * @param token Auth Bearer token.
     * @param ownedTasksCallback
     */
    public void getOwnedTasks(String token, Consumer<Result<List<Task>>> ownedTasksCallback) {
        taskDataSource.listMyTasks(token, true, (Result<List<Task>> result)-> {
            if(result instanceof Result.Success) {
                liveDataOwnedTasks.postValue(((Result.Success<List<Task>>) result).getData());
            }
            ownedTasksCallback.accept(result);
        });
    }

    /**
     * Creates a new task.
     * @param token Auth Bearer token.
     * @param title title of task.
     * @param description description of task.
     * @param maxUsers limit of user which can join a task.
     * @param scheduleDate date when task is gonna happen.
     * @param groupId id of the group task is associated with. Public group if id is null.
     * @param createdTaskCallback
     */
    //TODO Test if createdTaskCallback works without lambda
    public void createTask(String token, String title, String description, Long maxUsers, Date scheduleDate,
                           Long groupId, Consumer<Result<Task>> createdTaskCallback) {
        taskDataSource.createTask(token, title, description, maxUsers,
                parseDateToString(scheduleDate), groupId, createdTaskCallback);
    }

    /**
     * Delete a task from the database
     * @param token Auth Bearer token
     * @param taskId id of task to be removed.
     * @param removedTaskCallback
     */
    public void deleteTask(String token, Long taskId, Consumer<Result<Boolean>> removedTaskCallback) {
        taskDataSource.deleteTask(token, taskId, removedTaskCallback);
    }

    /**
     * Update an existing task. Only works for the creator of the task.
     * @param token Auth Bearer token.
     * @param title title of task.
     * @param description description of task.
     * @param maxUsers limit of user which can join the task.
     * @param scheduleDate date when task is gonna happen. Can't be before today's date.
     * @param groupId id of the group task is associated with. Public group if id is null.
     * @param taskId id of task.
     * @param updatedTaskCallback
     */
    public void updateTask(String token, String title, String description, Long maxUsers, Date scheduleDate,
                           Long groupId, Long taskId, Consumer<Result<Task>> updatedTaskCallback) {
        taskDataSource.updateTask(token, title, description, maxUsers,
                parseDateToString(scheduleDate), groupId, taskId, updatedTaskCallback);
    }

    /**
     * Join a task.
     * @param token Auth Bearer token.
     * @param taskId id of task to join.
     * @param joinedTaskCallback
     */
    public void joinTask(String token, Long taskId, Consumer<Result<Boolean>> joinedTaskCallback) {
        taskDataSource.joinTask(token, taskId, joinedTaskCallback);
    }

    /**
     * Parse a date to String format the server expect to get.
     * @param date date to parse to String.
     * @return date as a String in the format the server except to get.
     */
    //TODO Test if String return correctly.
    private String parseDateToString(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Oslo"));
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH) +1; //Return first day of month as 0.
        int hour = cal.get(Calendar.HOUR);
        int minutes = cal.get(Calendar.MINUTE);
        System.out.println("Parsed date is: " + day + "/" + month + "/" + year + " " + hour + ":" + minutes + ":00");
        return day + "/" + month + "/" + year + " " + hour + ":" + minutes + ":00";
    }
}
