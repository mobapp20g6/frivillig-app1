package no.ntnu.mobapp20g6.app1.data.model;

import android.util.Log;

import com.auth0.android.jwt.JWT;

import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 * @author nilsjha
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

    public String getTokenWithBearer() {
        return  "Bearer " + userToken.toString();
    }
    public String getUserToken() {
        return userToken.toString();
    }
    public String getUserTokenIssuer() {
        return userToken.getIssuer();
    }

    public void setUserToken(String token) {
        Log.d("OK-AUTH",": Wrote token to user " + super.getUserEmail());
        try {
            token = token.replace("Bearer ", "");
            this.userToken = new JWT(token);
        } catch (Exception e) {
            Log.d("ERR-AUTH",": Unable to parse token from Bearer header");
        }
    }

    public Date getExpireTime() {
        return userToken.getExpiresAt();
    }

    public Boolean isTokenExpired() {
        return userToken.isExpired(3600);
    }

}