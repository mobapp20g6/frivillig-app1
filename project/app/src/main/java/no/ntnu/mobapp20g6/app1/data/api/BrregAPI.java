package no.ntnu.mobapp20g6.app1.data.api;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BrregAPI {
    String PREFIX = "enhetsregisteret/api/";

    @GET(PREFIX + "enheter")
    Call<JsonObject> getVoluntaryBrregOrg(
            @Query("organisasjonsnummer") String orgId,
            @Query("registrertIFrivillighetsregisteret") boolean voluntary
    );
}
