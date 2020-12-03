package no.ntnu.mobapp20g6.app1.data.ds;

import android.util.Log;

import no.ntnu.mobapp20g6.app1.data.RestService;
import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.api.AuthApi;
import no.ntnu.mobapp20g6.app1.data.model.LoggedInUser;
import no.ntnu.mobapp20g6.app1.data.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.function.Consumer;

import javax.security.auth.login.LoginException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 * @author nilsjha
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

    /**
     * Create a user account
     * @param username username of user
     * @param pwd password of user
     * @param firstname first name of user
     * @param lastname last name of user
     * @param creationResultCallback result of creation:
     *                               Result.Success(true) = Creation OK
     *                               Result.Success(false) = Creation STOPPED, bad input
     *                               Result.Error = Server gave other error code
     *
     */
    public void createAccount(
            String username, String pwd, String firstname, String lastname,
            Consumer<Result<Boolean>> creationResultCallback
    ) {
        try {
            Call<User> createUserCall = authService.createUser(firstname,lastname,pwd,username);
            createUserCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        // OK, return true
                        Log.d("OK-AUTH","Created user OK: " + username);
                        creationResultCallback.accept(new Result.Success<Boolean>(true));
                    } else if (response.code() == 400) {
                        Log.d("FAIL-AUTH","Invalid input");
                        creationResultCallback.accept(new Result.Success<Boolean>(false));
                    } else {
                        Log.d("FAIL-AUTH","Server error");
                        creationResultCallback.accept(new Result.Error(new Exception("Server")));
                    }

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d("FAIL-AUTH","No connection");
                    creationResultCallback.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));

                }
            });


        } catch (Exception e) {
            Log.d("FAIL-AUTH","Client error");
            creationResultCallback.accept(new Result.Error(new Exception("Client error")));
        }
    }

    public void changepassword(String token, String oldpass, String newpass, String username,Consumer<Result<Boolean>> validResult) {
        try {
            if (token == null) {
                validResult.accept(new Result.Error(new Exception("Token")));
                Log.d("FAIL-AUTH","Token cannot be null when trying to reset passwd");
            } else {
                Call<Void> changePwdCall = authService.changePassword(token,username,newpass,oldpass);
                changePwdCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            validResult.accept(new Result.Success<Boolean>(true));
                        } else if (response.code() == 403) {
                            // Forbidden, most likly old password is worng
                            validResult.accept(new Result.Success<Boolean>(false));
                        } else {
                            // Not allowed or not authenticated
                            validResult.accept(new Result.Error(new Exception("Bad request")));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("FAIL-AUTH","No connection");
                        validResult.accept(new Result.Error(new IOException("Connection fail " + t.getCause())));

                    }
                });
            }

        } catch (Exception e) {
            Log.d("FAIL-AUTH","Client error");
            validResult.accept(new Result.Error(new Exception("Client error")));
        }
    }


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
            String token = currentUser.getTokenWithBearer();
            if (token == null) {
                renewedUsrCallback.accept(new Result.Error(new Exception("Token")));
                Log.d("FAIL-AUTH","Token cannot be null when trying renew session");
            }
            else {
                Call<Void> renewCall = authService.renewSession(token);
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
                        } else {
                            renewedUsrCallback.accept(new Result.Error(new Exception("Server errror")));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        renewedUsrCallback.accept(new Result.Error(new IOException("Communication error")));
                    }
                });

            }

        } catch (Exception e) {
            Log.d("FAIL-AUTH","Client error");
            renewedUsrCallback.accept(new Result.Error(new Exception("Client error")));
        }
    }

    public void getUserInfo(LoggedInUser currentUser, Consumer<Result<LoggedInUser>> receivedUserResult) {
        try {
            String token = currentUser.getTokenWithBearer();
            if (token == null) {
                receivedUserResult.accept(new Result.Error(new Exception("Token")));
                Log.d("FAIL-AUTH","Token cannot be null when trying to get user info");
            } else {
                getLoginUserInfo(token,receivedUserResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                if(response.isSuccessful()) {
                    LoggedInUser user = response.body();
                    user.setUserToken(token);
                    Log.d("OK-AUTH","Retrieved user info for:" + user.getUserEmail());
                    result.accept(new Result.Success<>(response.body()));
                } else {
                    result.accept(new Result.Error(new Exception("Unable to get user data")));
                    Log.d("ERROR-AUTH","Unable to get user data after logging in");
                }
            }

            @Override
            public void onFailure(
                    Call<LoggedInUser> call, Throwable t
            ) {
                result.accept(new Result.Error(new Exception("Communication error")));
                Log.d("ERROR-AUTH","Unable to get user data, server responded with error");
            }
        });
    }
}