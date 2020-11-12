package no.ntnu.mobapp20g6.app1.data.model;

import java.util.Date;

public class Group {

    private Long groupId;

    private String groupName;

    private Date created;

    private Long organizationID;

    private String description;

    public Group() {
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public Date getCreated() {
        return created;
    }

    public Long getOrganizationID() {
        return organizationID;
    }

    public String getDescription() {
        return description;
    }
}
