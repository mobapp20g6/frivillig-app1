package no.ntnu.mobapp20g6.app1.data.api;

import no.ntnu.mobapp20g6.app1.data.model.Picture;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface PictureApi {
    String PREFIX = "resources/image/";

    @GET(PREFIX + "getimage")
    Call<Picture> getPicture(
            @Query("name") String id,
            @Query("width") int width
    );

    @Multipart
    @POST(PREFIX + "settaskimage")
    Call<Task> setTaskPicture(
            @Part("taskid") Long taskId,
            @Part("image")RequestBody picture
            );

    @Multipart
    @POST(PREFIX + "setgrouplogo")
    Call<Task> setGroupPicture(
            @Part("groupid") Long groupId,
            @Part("image")RequestBody picture
    );
}
