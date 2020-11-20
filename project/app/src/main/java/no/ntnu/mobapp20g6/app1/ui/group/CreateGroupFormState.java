package no.ntnu.mobapp20g6.app1.ui.group;

import androidx.annotation.Nullable;

public class CreateGroupFormState {
    @Nullable
    private Integer groupNameError;
    @Nullable
    private Integer groupDescError;
    @Nullable
    private Integer groupOrgIdError;
    private boolean isDataValid;

    public CreateGroupFormState(@Nullable Integer groupNameError, @Nullable Integer groupDescError, @Nullable Integer groupOrgIdError) {
        this.groupNameError = groupNameError;
        this.groupDescError = groupDescError;
        this.groupOrgIdError = groupOrgIdError;
    }

    public CreateGroupFormState(boolean isDataValid) {
        this.groupNameError = null;
        this.groupDescError = null;
        this.groupOrgIdError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getGroupNameError() {
        return groupNameError;
    }

    @Nullable
    public Integer getGroupDescError() {
        return groupDescError;
    }

    @Nullable
    public Integer getGroupOrgIdError() {
        return groupOrgIdError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
