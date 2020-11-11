package no.ntnu.mobapp20g6.app1.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class User {

    @Expose
    @SerializedName("id")
    private String userId;

    @Expose
    @SerializedName("email")
    private String userEmail;

    @Expose
    @SerializedName("created")
    private Date userCreated;

    @Expose
    @SerializedName("currentState")
    private String userState;

    @Expose
    @SerializedName("firstName")
    private String userFirstName;

    @Expose
    @SerializedName("lastName")
    private String userLastName;


    public User() {
    }

}
