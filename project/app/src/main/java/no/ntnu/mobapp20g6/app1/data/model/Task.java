package no.ntnu.mobapp20g6.app1.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Task {

    @Expose
    @SerializedName("id")
    private Long id;

    @Expose
    @SerializedName("title")
    private String title;

    @Expose
    @SerializedName("description")
    private String description;

    @Expose
    @SerializedName("participantCount")
    private Long participantCount;

    @Expose
    @SerializedName("participantLimit")
    private Long participantLimit;

    @Expose
    @SerializedName("scheduleDate")
    private Date scheduleDate;

    @Expose
    @SerializedName("created")
    private Date created;

    @Expose
    @SerializedName("creatorUser")
    private User creator;

    @Expose
    @SerializedName("picture")
    private Picture picture;

    @Expose
    @SerializedName("users")
    private List<User> participants;

    @Expose
    @SerializedName("associatedGroup")
    private Group associatedGroup;

    @Expose
    @SerializedName("location")
    private Location location;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Long getParticipantCount() {
        return participantCount;
    }

    public Long getParticipantLimit() {
        return participantLimit;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public Date getCreated() {
        return created;
    }

    public User getCreator() {
        return creator;
    }

    public Picture getPicture() {
        return picture;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public Group getAssociatedGroup() {
        return associatedGroup;
    }

    public Location getLocation() {
        return location;
    }
}
