package no.ntnu.mobapp20g6.app1.ui.createtask;

import androidx.annotation.Nullable;

/**
 * A validation form which receives errors to validate state for different fields.
 */
public class NewTaskFormState {
    @Nullable
    private Integer titleError;
    @Nullable
    private Integer descriptionError;
    @Nullable
    private Integer participantError;
    @Nullable
    private Integer visibilityError;
    @Nullable
    private Integer dateError;

    private boolean isDataValid;

    NewTaskFormState(@Nullable Integer titleError,
                     @Nullable Integer descriptionError,
                     @Nullable Integer participantError,
                     @Nullable Integer visibilityError,
                     @Nullable Integer dateError
    ) {
        this.titleError = titleError;
        this.descriptionError = descriptionError;
        this.participantError = participantError;
        this.visibilityError = visibilityError;
        this.dateError = dateError;
        this.isDataValid = false;
    }

    NewTaskFormState(boolean isDataValid) {
        this.titleError = null;
        this.descriptionError = null;
        this.participantError = null;
        this.visibilityError = null;
        this.dateError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getTitleError() {
        return titleError;
    }

    @Nullable
    Integer getDescriptionError() {
        return descriptionError;
    }

    @Nullable
    Integer getParticipantError() {
        return participantError;
    }

    @Nullable
    Integer getVisibilityError() {
        return visibilityError;
    }

    @Nullable
    Integer getDateError() {
        return dateError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
