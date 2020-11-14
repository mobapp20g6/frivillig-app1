package no.ntnu.mobapp20g6.app1.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Group {

    @Expose
    @SerializedName("id")
    private Long groupId;

    @Expose
    @SerializedName("name")
    private String groupName;

    @Expose
    @SerializedName("created")
    private Date groupCreated;

    @Expose
    @SerializedName("originationId")
    private Long groupOrganizationId;

    @Expose
    @SerializedName("description")
    private String groupDescription;

    @Expose
    @SerializedName("location")
    private Location location;

    @Expose
    @SerializedName("picture")
    private Picture picture;

    public Group() {
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public Long getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public Date getGroupCreated() {
        return groupCreated;
    }

    public Long getGroupOrganizationId() {
        return groupOrganizationId;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public Location getLocation() {
        return location;
    }

    public Picture getPicture() {
        return picture;
    }
}
