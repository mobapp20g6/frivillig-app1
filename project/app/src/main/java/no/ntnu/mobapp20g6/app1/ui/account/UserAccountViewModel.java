package no.ntnu.mobapp20g6.app1.ui.account;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.model.LoggedInUser;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;

public class UserAccountViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<UserAccountResetFormState> resetFormstate = new MutableLiveData<>();
    private MutableLiveData<Boolean> resetPasswordResult = new MutableLiveData<>();
    private MutableLiveData<LoggedInUser> currentUserLiveData;
    private LoginRepository loginRepository;

    public UserAccountViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        this.currentUserLiveData = new MutableLiveData<>();
    }

    public void loadLoggedInUser() {
        if (loginRepository.isLoggedIn()) {
            LoggedInUser loadedUser = loginRepository.getCurrentUser();
            if (loadedUser != null) {
                this.currentUserLiveData.setValue(loadedUser);
            }
        }
    }

    public Boolean isUserLoggedIn() {
        return loginRepository.isLoggedIn();

    }

    public void fetchUserFromServer() {
        loginRepository.updateLoggedInUser(success -> {
            if (success) {
                loadLoggedInUser();
            }
        });
    }

    public LiveData<LoggedInUser> getCurrentUserLiveData() {
        return this.currentUserLiveData;
    }

    public LiveData<UserAccountResetFormState> getResetPasswordFormState() {
        return this.resetFormstate;
    }

    public LiveData<Boolean> getResetPasswordResult() {
        return this.resetPasswordResult;
    }

    public void resetPasswordDataChanged(String oldpass, String newpass, String verifypass) {
        if (!(isPasswordValid(oldpass))) {
            resetFormstate.setValue(new UserAccountResetFormState(R.string.invalid_password, null,null));
        } else if (!(isPasswordValid(newpass))) {
            resetFormstate.setValue(new UserAccountResetFormState(null, R.string.invalid_password, null));
        } else if (!(isVerifyPasswordValid(newpass,verifypass))) {
            resetFormstate.setValue(new UserAccountResetFormState(null, null, R.string.mismatch_password));
        } else {
            resetFormstate.setValue(new UserAccountResetFormState(true));
        }

    }

    private Boolean isPasswordValid(@Nullable String oldpassword) {
        if (oldpassword == null || oldpassword.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean isVerifyPasswordValid(@Nullable String newpass,@Nullable String repeatpass) {
        return newpass.equals(repeatpass);
    }


    public void doPasswordReset(String oldpassword, String newpassword) {
        if (loginRepository.isLoggedIn()) {
            loginRepository.changePassword(loginRepository.getCurrentUser().getUserEmail(),oldpassword,newpassword,(callbackResult) -> {
                if (callbackResult instanceof Result.Success) {
                    this.resetPasswordResult.setValue(((Result.Success<Boolean>) callbackResult).getData());
                } else {
                    this.resetPasswordResult.setValue(false);
                }
            });
        } else {
            this.resetPasswordResult.setValue(false);
        }
    }
}