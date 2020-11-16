package no.ntnu.mobapp20g6.app1.ui.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.model.Group;
import no.ntnu.mobapp20g6.app1.data.model.LoggedInUser;
import no.ntnu.mobapp20g6.app1.data.model.User;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;

public class UserAccountViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<UserAccountResetFormState> resetFormstate;
    private MutableLiveData<Boolean> resetPasswordResult;
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

    public void refreshCurrentUser() {
        loginRepository.updateLoggedInUser(success -> {
            if (success) {
                loadLoggedInUser();
            }
        });
    }

    public LiveData<LoggedInUser> getCurrentUserLiveData() {
        return this.currentUserLiveData;
    }

    public LiveData getResetPasswordResult() {
        return this.resetPasswordResult;
    }


    private void doPasswordReset(String oldpassword, String newpassword) {
        if (loginRepository.isLoggedIn()) {
            loginRepository.changePassword(loginRepository.getCurrentUser().getUserEmail(),oldpassword,newpassword,(callbackResult) -> {
                if (callbackResult instanceof Result.Success) {
                    this.resetPasswordResult.setValue(true);
                } else {
                    this.resetPasswordResult.setValue(false);
                }
            });
        }
    }
}