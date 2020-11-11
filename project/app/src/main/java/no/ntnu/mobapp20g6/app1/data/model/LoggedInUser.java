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
    private String displayName;

    public LoggedInUser() {
        super();
    }

    public String getUserToken() {
        return userToken.toString();
    }

    public String getDisplayName() {
        return displayName;
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