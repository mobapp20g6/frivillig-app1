package no.ntnu.mobapp20g6.app1.data.api;

import no.ntnu.mobapp20g6.app1.data.model.Group;
import no.ntnu.mobapp20g6.app1.data.model.Picture;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface PictureApi {
    String PREFIX = "resources/image/";

    @GET(PREFIX + "getimage")
    Call<Picture> getPicture(
            @Header("Authorization") String token,
            @Query("name") String id,
            @Query("width") int width
    );

    @Multipart
    @POST(PREFIX + "settaskimage")
    Call<Task> setTaskPicture(
            @Header("Authorization") String token,
            @Part("taskid") Long taskId,
            @Part MultipartBody.Part picture
            );

    @Multipart
    @POST(PREFIX + "setgrouplogo")
    Call<Group> setGroupPicture(
            @Header("Authorization") String token,
            @Part("groupid") Long groupId,
            @Part MultipartBody.Part picture
    );
}
