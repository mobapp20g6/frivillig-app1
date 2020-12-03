package no.ntnu.mobapp20g6.app1.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * @author nilsjha
 */
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

    @Expose
    @SerializedName("memberOfGroup")
    private Group userGroup;


    public User() {
    }


    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Date getUserCreated() {
        return userCreated;
    }

    public String getUserState() {
        return userState;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public Group getUserGroup() {
        return userGroup;
    }

    public String getUserId() {
        return userId;
    }
}
