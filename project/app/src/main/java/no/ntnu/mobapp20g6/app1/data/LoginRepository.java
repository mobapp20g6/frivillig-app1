package no.ntnu.mobapp20g6.app1.data;

import java.util.function.Consumer;

import no.ntnu.mobapp20g6.app1.data.model.LoggedInUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    /**
     * Other classes can use this with isLoggedIn() to get the auth token
     * @return if logged in a token, else null
     */
    public String getToken() {
        return this.user.getUserToken();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }



    public void login(String username, String password, Consumer<Result<LoggedInUser>> resultCallback) {
        // handle login
        dataSource.login(username, password,(Result<LoggedInUser> result)->{
            if (result instanceof Result.Success) {
                setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
            }
            resultCallback.accept(result);

        } );
    }

    /**
     * This function checks is the current session is expired, and renews the session
     * @param isRenewedResult This callback returns true if the session was renewed
     *                        If not nessecary or failed it returns false and logs out the user.
     *                        TODO: not tested
     */
    private void checkAndRenewSessionIfNeeded(Consumer<Boolean> isRenewedResult) {
        if (user.isTokenExpired()) {
            dataSource.renew(user, (Result<LoggedInUser> userWithNewTokenResult)->{
                if (userWithNewTokenResult instanceof Result.Success) {
                    setLoggedInUser(((Result.Success<LoggedInUser>) userWithNewTokenResult).getData());
                    isRenewedResult.accept(true);
                } else {
                    isRenewedResult.accept(false);
                    setLoggedInUser(null);
                }
            });
        } else {
            isRenewedResult.accept(false);
        }
    }

}