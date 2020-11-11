package no.ntnu.mobapp20g6.app1.data;

import android.util.Log;

import no.ntnu.mobapp20g6.app1.data.model.LoggedInUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.function.Consumer;

import javax.security.auth.login.LoginException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private Retrofit rest;
    private AuthApi authService;


    public LoginDataSource() {
        rest = RestService.getRetrofitClient();
        authService = rest.create(AuthApi.class);
    }

    /**
     * This function handles authentication when the user is not logged in
     * @param username the username to login with
     * @param password the password to login with
     * @param loginResultCallback A callback with a result of the authenticated user
     */
    public void login(
            String username,
            String password,
            Consumer<Result<LoggedInUser>> loginResultCallback
    ) {
        try {
            Call<Void> loginCall = authService.login(username,password);

            loginCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        String token = response.headers().get("Authorization");
                        Log.d("OK-AUTH","Login OK for:" + username);
                        getLoginUserInfo(token,loginResultCallback);
                    } else if(response.code() == 401) {
                        // Not authorized
                        Log.d("FAIL-AUTH","Login not correct for:" + username);
                        loginResultCallback.accept(new Result.Error(
                                new LoginException("Wrong username or password")
                        ));
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Server comms error
                    loginResultCallback.accept(new Result.Error(new IOException()));
                    Log.d("ERROR-AUTH","Failure to communicate : " + t.getMessage());

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createAccount(String username, String )


    public void logout() {
        // TODO: This is not implemented on server, as tokens expire automatically
        // FIXME: Remove this function if this shall not be implemented!
    }

    /**
     * Takes a LoggedInUser object and get a new auth token from the server. If successful
     * the new token overwrites the current token on the current user. The updated LoggedInUser
     * is returned with a callback - and the calling class should update the user with the return.
     *
     * User information is returned from a helper function
     * @param currentUser current user (holds name, email etc)
     * @param renewedUsrCallback the currentUser input object with a new token overwritten
     */
    public void renew(LoggedInUser currentUser, Consumer<Result<LoggedInUser>> renewedUsrCallback) {
        try {
            Call<Void> renewCall = authService.renewSession();
            renewCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        String token = response.headers().get("Authorization");
                        LoggedInUser user = currentUser;
                        user.setUserToken(token);
                        Log.d("OK-AUTH","Renew token OK for: " + user.getUserEmail());
                        renewedUsrCallback.accept(new Result.Success<LoggedInUser>(user));
                    } else if(response.code() == 403){
                        renewedUsrCallback.accept(new Result.Error(new Exception("Not allowed")));
                    } else if(response.code() == 401){
                        renewedUsrCallback.accept(new Result.Error(new Exception("Not authorized")));
                    } else if(response.code() == 401){
                        renewedUsrCallback.accept(new Result.Error(new Exception("Not authorized")));
                    } else {
                        renewedUsrCallback.accept(new Result.Error(new Exception("Server errror")));
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    renewedUsrCallback.accept(new Result.Error(new IOException("Communication error")));
                }
            });

        } catch (Exception e) {

        }
    }

    /**
     * This helper is used in login , and takes the input of an authenticated token
     * and username. It retrieves a User object from the server, and writes the information into
     * a LoggedInUser object. Only information about user is renewed - not the token.
     * @param token token to use to get access on server
     * @param result new LoggedInUser object with new information
     */
    private final void getLoginUserInfo(String token, Consumer<Result<LoggedInUser>> result) {
        Call<LoggedInUser> getUserInfoCall
                = authService.getCurrentUser(token);

        getUserInfoCall.enqueue(new Callback<LoggedInUser>() {
            @Override
            public void onResponse(
                    Call<LoggedInUser> call,
                    Response<LoggedInUser> response
            ) {
                LoggedInUser user = response.body();
                Log.d("OK-AUTH","Retrieved user info for:" + user.getUserEmail());
                result.accept(new Result.Success<>(response.body()));
            }

            @Override
            public void onFailure(
                    Call<LoggedInUser> call, Throwable t
            ) {
                result.accept(new Result.Error(new Exception("Unable to get user data")));
                Log.d("ERROR-AUTH","Unable to get user data after logging in");
            }
        });
    }
}