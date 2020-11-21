package no.ntnu.mobapp20g6.app1.ui.group;

import androidx.annotation.Nullable;

import no.ntnu.mobapp20g6.app1.data.model.Group;

/**
 * Group creation result
 */
public class CreateGroupResult {
    @Nullable
    private Group success;
    @Nullable
    private Integer error;

    public CreateGroupResult(@Nullable Group success) {
        this.success = success;
    }

    public CreateGroupResult(@Nullable Integer error) {
        this.error = error;
    }

    @Nullable
    public Group getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}
