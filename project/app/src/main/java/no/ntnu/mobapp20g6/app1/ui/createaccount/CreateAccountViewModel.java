package no.ntnu.mobapp20g6.app1.ui.createaccount;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;

public class CreateAccountViewModel extends ViewModel {
    private final LoginRepository loginRepository;
    private MutableLiveData<CreateAccountFormState> createFormState = new MutableLiveData<>();

    CreateAccountViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    /**
     * Create a new user account.
     * @param email user email.
     * @param pwd user password.
     * @param firstName first name of user.
     * @param lastName last name of user.
     * @param creationCallback return true if result was true and success.
     */
    public void createAccount(String email, String pwd, String firstName, String lastName, Consumer<Boolean> creationCallback) {
        loginRepository.create(email, pwd, firstName, lastName, creationResult->{
            creationCallback.accept(creationResult instanceof Result.Success &&
                    ((Result.Success) creationResult).getData().equals(true));
        });
    }

    public LiveData<CreateAccountFormState> getCreateFormStateLiveData() {
        return this.createFormState;
    }

    /**
     * Checks if each parameter is valid and return true in "createFormState" if all parameters is valid.
     * If a parameter is not valid error will be returned in "createFormState".
     * @param firstName first name.
     * @param lastName last name.
     * @param email email.
     * @param pwd password must be 6 or more characters.
     * @param pwdVerify verify password must be equal to password.
     */
    public void isFieldsValid(String firstName, String lastName, String email, String pwd, String pwdVerify) {
        if(!isNameValid(firstName)) {
            createFormState.setValue(new CreateAccountFormState(R.string.invalid_first_name, null, null, null, null));
        } else if(!isNameValid(lastName)) {
            createFormState.setValue(new CreateAccountFormState(null, R.string.invalid_last_name, null, null, null));
        } else if(!isEmailValid(email)) {
            createFormState.setValue(new CreateAccountFormState(null, null, R.string.invalid_email, null, null));
        } else if(!isPasswordValid(pwd)) {
            createFormState.setValue(new CreateAccountFormState(null, null, null, R.string.invalid_password, null));
        } else if(!isVerifyPasswordValid(pwd, pwdVerify)) {
            createFormState.setValue(new CreateAccountFormState(null, null, null, null, R.string.invalid_password));
        } else {
            createFormState.setValue(new CreateAccountFormState(true));
        }
    }

    /**
     * Return true if parameter is a valid email.
     * @param email to check if valid.
     * @return true if email is valid.
     */
    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return !email.trim().isEmpty();
        }
    }

    /**
     * Return true if parameter is valid.
     * @param name to check if valid.
     * @return true if name is valid.
     */
    private boolean isNameValid(String name) {
        if(name == null) {
            return false;
        } else {
            return !name.trim().isEmpty();
        }
    }

    /**
     * Return true if password is valid.
     * @param password to check if valid.
     * @return true if password is valid.
     */
    private boolean isPasswordValid(String password) {
        return password != null && !password.trim().isEmpty() && password.length() >= 6;
    }

    /**
     * Return true if password is equal to verify password.
     * @param password password.
     * @param verifyPassword verify password.
     * @return true if password is equal to verify password.
     */
    private boolean isVerifyPasswordValid(String password, String verifyPassword) {
        return password.equals(verifyPassword);
    }
}
