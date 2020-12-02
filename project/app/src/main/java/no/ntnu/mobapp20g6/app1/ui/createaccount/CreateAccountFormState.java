package no.ntnu.mobapp20g6.app1.ui.createaccount;

import androidx.annotation.Nullable;

public class CreateAccountFormState {
    @Nullable
    private Integer firstNameError;
    @Nullable
    private Integer lastNameError;
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer passwordVerifyError;

    private boolean isDataValid;

    CreateAccountFormState(@Nullable Integer firstNameError, @Nullable Integer lastNameError, @Nullable Integer emailError,
                           @Nullable Integer passwordError, @Nullable Integer passwordVerifyError) {
        this.firstNameError = firstNameError;
        this.lastNameError = lastNameError;
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.passwordVerifyError = passwordVerifyError;
        this.isDataValid = false;
    }

    CreateAccountFormState(boolean isDataValid) {
        this.firstNameError = null;
        this.lastNameError = null;
        this.emailError = null;
        this.passwordError = null;
        this.passwordVerifyError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getFirstNameError() {
        return firstNameError;
    }

    @Nullable
    public Integer getLastNameError() {
        return lastNameError;
    }

    @Nullable
    public Integer getEmailError() {
        return emailError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    public Integer getPasswordVerifyError() {
        return passwordVerifyError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
