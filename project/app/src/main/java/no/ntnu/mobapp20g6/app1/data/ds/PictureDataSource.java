package no.ntnu.mobapp20g6.app1.data.ds;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.data.RestService;
import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.api.PictureApi;
import no.ntnu.mobapp20g6.app1.data.model.Group;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author TrymV
 */
public class PictureDataSource {
    private final PictureApi pictureApi;

    //http response codes
    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;
    private static final int UNAUTHORISED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;

    public PictureDataSource() {
        Retrofit rest = RestService.getRetrofitClient();
        pictureApi = rest.create(PictureApi.class);
    }

    public void setTaskImage(String token, Long taskId, String picturePath,
    Consumer<Result<Task>>setTaskImageCallback) {
        try {
            if (token != null) {
                Call<Task> setTaskImageCall = pictureApi.setTaskPicture(token, taskId, preparePicture(picturePath));
                setTaskImageCall.enqueue(new Callback<Task>() {
                    @Override
                    public void onResponse(Call<Task> call, Response<Task> response) {
                        switch (response.code()) {
                            case OK:
                                Log.d("OK-SET_TASK_IMAGE", "Task picture was set.");
                                setTaskImageCallback.accept(new Result.Success<>(response.body()));
                                break;

                            case BAD_REQUEST:
                                Log.d("FAIL-SET_TASK_IMAGE", "Invalid input");
                                setTaskImageCallback.accept(new Result.Success<>(null));
                                break;

                            case UNAUTHORISED:
                                Log.d("FAIL-SET_TASK_IMAGE", "User not logged in");
                                setTaskImageCallback.accept(new Result.Success<>(null));
                                break;

                            case FORBIDDEN:
                                Log.d("FAIL-SET_TASK_IMAGE", "User not owner of Task");
                                setTaskImageCallback.accept(new Result.Success<>(null));
                                break;

                            case NOT_FOUND:
                                Log.d("FAIL-SET_TASK_IMAGE", "Task not found");
                                setTaskImageCallback.accept(new Result.Success<>(null));
                                break;

                            default:
                                Log.d("FAIL-SET_TASK_IMAGE", "Server error");
                                setTaskImageCallback.accept(new Result.Error(new Exception("Server")));
                        }
                    }

                    @Override
                    public void onFailure(Call<Task> call, Throwable t) {
                        Log.d("FAIL-SET_TASK_IMAGE", "No connection");
                        setTaskImageCallback.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                    }
                });
            } else {
                setTaskImageCallback.accept(new Result.Error(new Exception("Token")));
                Log.d("FAIL-SET_TASK_IMAGE", "Token cannot be null when trying to set task picture");
            }
        } catch (Exception e) {
            Log.d("FAIL-SET_TASK_IMAGE", "Client error");
            System.out.println("Client error stacktrace: " + e);
            setTaskImageCallback.accept(new Result.Error(new Exception("Client error")));
        }
    }

    public void setGroupLogo(String token, Long groupId, String picturePath,
                             Consumer<Result<Group>>setGroupLogoCallback) {
        try {
            if (token != null) {
                Call<Group> setGroupLogoCall = pictureApi.setGroupPicture(token, groupId, preparePicture(picturePath));
                setGroupLogoCall.enqueue(new Callback<Group>() {
                    @Override
                    public void onResponse(Call<Group> call, Response<Group> response) {
                        switch (response.code()) {
                            case OK:
                                Log.d("OK-SET_GROUP_LOGO", "Group logo was set.");
                                setGroupLogoCallback.accept(new Result.Success<>(response.body()));
                                break;

                            case BAD_REQUEST:
                                Log.d("FAIL-SET_GROUP_LOGO", "Invalid input");
                                setGroupLogoCallback.accept(new Result.Success<>(null));
                                break;

                            case UNAUTHORISED:
                                Log.d("FAIL-SET_GROUP_LOGO", "User not logged in");
                                setGroupLogoCallback.accept(new Result.Success<>(null));
                                break;

                            case FORBIDDEN:
                                Log.d("FAIL-SET_GROUP_LOGO", "User not owner of Group");
                                setGroupLogoCallback.accept(new Result.Success<>(null));
                                break;

                            case NOT_FOUND:
                                Log.d("FAIL-SET_GROUP_LOGO", "Group not found");
                                setGroupLogoCallback.accept(new Result.Success<>(null));
                                break;

                            default:
                                Log.d("FAIL-SET_GROUP_LOGO", "Server error");
                                setGroupLogoCallback.accept(new Result.Error(new Exception("Server")));
                        }
                    }

                    @Override
                    public void onFailure(Call<Group> call, Throwable t) {
                        Log.d("FAIL-SET_GROUP_LOGO", "No connection");
                        setGroupLogoCallback.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));
                    }
                });
            } else {
                setGroupLogoCallback.accept(new Result.Error(new Exception("Token")));
                Log.d("FAIL-SET_GROUP_LOGO", "Token cannot be null when trying to set group logo");
            }
        } catch (Exception e) {
            Log.d("FAIL-SET_GROUP_LOGO", "Client error");
            setGroupLogoCallback.accept(new Result.Error(new Exception("Client error")));
        }
    }

    /**
     * Pack a picture into a MultipartBody.Part.
     * @param picturePath path to the picture.
     * @return picture as MultipartBody.part.
     */
    private MultipartBody.Part preparePicture(String picturePath) {
        MultipartBody.Part picture = null;
        if(picturePath != null) {
            File file = new File(picturePath);
            String multiPartHeaderName = "image";
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            picture = MultipartBody.Part.createFormData(multiPartHeaderName, file.getName(), requestFile);
        }
        return picture;
    }
}
