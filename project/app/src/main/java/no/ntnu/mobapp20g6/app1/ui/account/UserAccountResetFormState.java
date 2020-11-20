package no.ntnu.mobapp20g6.app1.ui.account;

import androidx.annotation.Nullable;

public class UserAccountResetFormState {
    @Nullable
    private Integer passwordOldError;
    @Nullable
    private Integer passwordNewError;
    @Nullable
    private Integer passwordVerifyError;

    private boolean isDataValid;

    UserAccountResetFormState(@Nullable Integer passwordOldError,
                              @Nullable Integer passwordNewError,
                              @Nullable Integer passwordVerifyError
    ) {
        this.passwordOldError = passwordOldError;
        this.passwordNewError = passwordNewError;
        this.passwordVerifyError = passwordVerifyError;
        this.isDataValid = false;
    }

    UserAccountResetFormState(boolean isDataValid) {
        this.passwordOldError = null;
        this.passwordNewError = null;
        this.passwordVerifyError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getPasswordOldError() {
        return passwordOldError;
    }

    @Nullable
    Integer getPasswordNewError() {
        return passwordNewError;
    }

    @Nullable
    Integer getPasswordVerifyError() {
        return passwordVerifyError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
