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
     * Create user account, same signatur as login
     * @param creationCallback Result.Success(true) = OK, (false) = bad input - Result.Error = ERR
     */
    public void create(String username, String password, String firstname, String lastname, Consumer<Result<Boolean>> creationCallback) {
        dataSource.createAccount(username,password,firstname,lastname,(creationResult)->{
            creationCallback.accept(creationResult);
        });
    }

    /**
     * Reset the password for the provided username. Token provided by currently logged in user
     * will be used to try to change the password. The user is logged out if SUCCESS. Result
     * can be used to display a message etc. MUST BE LOGGED IN! TODO: NOT TESTED
     * @param username the username (email)
     * @param oldpass the old password needed for normal users or a bad input is thrown
     * @param newpass the new password
     * @param resetCallbackResult Result.Success(true=OK|false=bad input) Result.Error = ERR
     */
    public void changePassword(String username, String oldpass, String newpass, Consumer<Result<Boolean>> resetCallbackResult) {
        dataSource.changepassword(user.getUserToken(),oldpass,newpass,username, (Result<Boolean> validInputResult)->{
            if (validInputResult instanceof Result.Success) {
               logout();
            }
            resetCallbackResult.accept(validInputResult);
        });
    }

    /**
     * This function checks is the current session is expired, and renews the session
     * @param isRenewedResult This callback returns true if the session was renewed
     *                        If not nessecary or failed it returns false and logs out the user.
     *                        MUST BE LOGGED IN! TODO: not tested
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