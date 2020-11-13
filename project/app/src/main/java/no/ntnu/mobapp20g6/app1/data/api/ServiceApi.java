package no.ntnu.mobapp20g6.app1.data.api;

import androidx.annotation.Nullable;

import java.util.List;

import no.ntnu.mobapp20g6.app1.data.model.Task;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ServiceApi {
    String PREFIX = "resources/service/";

    @GET(PREFIX + "listtasks")
    Call<List<Task>> getAllTasks(
            @Header("Authorization") String token
    );

    /**
     * List all owned tasks of the user.
     * @param token Bearer token.
     * @param ownedTasks true if you want owned tasks or false if you want assigned tasks.
     * @return owned tasks if true and assigned tasks if false.
     */
    @FormUrlEncoded
    @GET(PREFIX + "listmytasks")
    Call<List<Task>> getMyTasks(
            @Header("Authorization") String token,
            @Field("ownedtasks") boolean ownedTasks
    );

    @GET(PREFIX + "gettask")
    Call<Task> getTask(
            @Header("Authorization") String token,
            @Query("id") Long taskId
    );

    @FormUrlEncoded
    @POST(PREFIX + "createtask")
    Call<Task> createTask(
            @Header("Authorization") String token,
            @Field("title") String title,
            @Nullable@Field("description") String description,
            @Field("maxusers") Long maxUsers,
            @Field("scheduledate") String scheduleDate,
            @Nullable@Field("group") Long groupId
    );

    @DELETE(PREFIX + "removetask")
    Call<Void> removeTask(
            @Header("Authorization") String token,
            @Query("id") Long taskId
    );

    @FormUrlEncoded
    @PUT(PREFIX + "updatetask")
    Call<Task> updateTask(
            @Header("Authorization") String token,
            @Nullable@Field("title") String title,
            @Nullable@Field("description") String description,
            @Nullable@Field("maxusers") Long maxUsers,
            @Nullable@Field("scheduledate") String scheduleDate,
            @Nullable@Field("group") Long groupId,
            @Query("id") Long taskId
    );

    @FormUrlEncoded
    @POST(PREFIX + "jointask")
    Call<Void> joinTask(
            @Header("Authorization") String token,
            @Field("id") Long taskId
    );
}