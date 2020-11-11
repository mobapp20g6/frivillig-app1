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

    public void login(
            String username,
            String password,
            Consumer<Result<LoggedInUser>> loginResultCallback
    ) {

        Retrofit rest = RestService.getRetrofitClient();

        try {
            AuthApi authService = rest.create(AuthApi.class);
            Call<Void> loginCall = authService.login(username,password);

            loginCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        String token = response.headers().get("Authorization");
                        Call<LoggedInUser> getUserInfoCall
                                = authService.getCurrentUser(token);

                        getUserInfoCall.enqueue(new Callback<LoggedInUser>() {
                            @Override
                            public void onResponse(
                                    Call<LoggedInUser> call,
                                    Response<LoggedInUser> response
                            ) {
                                loginResultCallback.accept(new Result.Success<>(response.body()));
                            }

                            @Override
                            public void onFailure(
                                    Call<LoggedInUser> call, Throwable t
                            ) {
                                loginResultCallback.accept(
                                        new Result.Success<>(new LoggedInUser(token, username))
                                );
                            }
                        });
                    } else if(response.code() == 401) {
                        // Not authorized
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

    public void logout() {
        // TODO: revoke authentication ( not implemented on server, therefore not impl! )
    }
}