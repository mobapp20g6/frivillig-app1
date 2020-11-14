package no.ntnu.mobapp20g6.app1.data;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.data.api.PictureApi;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PictureDataSource {
    private final PictureApi pictureApi;

    //http response codes
    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;
    private static final int UNAUTHORISED = 401;
    private static final int NOT_FOUND = 404;

    public PictureDataSource() {
        Retrofit rest = RestService.getRetrofitClient();
        pictureApi = rest.create(PictureApi.class);
    }

    public void setTaskImage(String token, Long taskId, String picturePath,
    Consumer<Result<Task>>setTaskImageCallback) {
        try {
            if (token != null) {
                Call<Task> setTaskImageCall = pictureApi.setTaskPicture(token, taskId, preparePicture(picturePath, "task_image"));
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
            setTaskImageCallback.accept(new Result.Error(new Exception("Client error")));
        }
    }

    public void setGroupLogo(String token, Long groupId, String picturePath,
                             Consumer<Result<Task>>setGroupLogoCallback) {
        try {
            if (token != null) {
                Call<Task> setGroupLogoCall = pictureApi.setTaskPicture(token, groupId, preparePicture(picturePath, "group_logo"));
                setGroupLogoCall.enqueue(new Callback<Task>() {
                    @Override
                    public void onResponse(Call<Task> call, Response<Task> response) {
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
                    public void onFailure(Call<Task> call, Throwable t) {
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
     * @param imageName name of picture.
     * @return picture as MultipartBody.part.
     */
    private MultipartBody.Part preparePicture(String picturePath, String imageName) {
        MultipartBody.Part picture = null;
        if(picturePath != null) {
            File file = new File(picturePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            picture = MultipartBody.Part.createFormData(imageName, file.getName(), requestFile);
        }
        return picture;
    }
}
