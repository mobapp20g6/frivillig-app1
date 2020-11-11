package no.ntnu.mobapp20g6.app1.data;

import no.ntnu.mobapp20g6.app1.data.model.LoggedInUser;
import no.ntnu.mobapp20g6.app1.data.model.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthApi {
    public static String PREFIX = "resources/auth";

    @GET(PREFIX + "/currentuser")
    Call<LoggedInUser> getCurrentUser(
            @Header("Authorization") String auth
    );

    @FormUrlEncoded
    @POST(PREFIX + "/createuser")
    Call<User> createUser(
            @Field("firstname") String firstName,
            @Field("lastname") String lastName,
            @Field("password") String pwd,
            @Field("email") String email
    );


    @FormUrlEncoded
    @POST(PREFIX + "/changepwd")
    Call<Void> changePassword(
            @Field("email") String email,
            @Field("pwd") String pwd,
            @Field("oldpwd") String oldpwd
    );

    @FormUrlEncoded
    @POST(PREFIX + "/login")
    Call<Void> login(
            @Field("email") String email,
            @Field("pwd") String pwd
    );

    @GET(PREFIX + "/renew")
    Call<Void> renewSession();

}
