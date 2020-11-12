package no.ntnu.mobapp20g6.app1.data.model;

import com.auth0.android.jwt.JWT;

import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser extends User{

    private JWT userToken;

    public LoggedInUser(String jsonwebtoken, String email) {
        super();
        this.userToken = new JWT(jsonwebtoken);
        super.setUserEmail(email);
    }

    // Used in runtime by serialization
    public LoggedInUser() {};

    public String getUserToken() {
        return userToken.toString();
    }

    public void setUserToken(String token) {
        this.userToken = new JWT(token);

    }

    public Date getExpireTime() {
        return userToken.getExpiresAt();
    }

    public Boolean isTokenExpired() {
        return userToken.isExpired(3600);
    }

}