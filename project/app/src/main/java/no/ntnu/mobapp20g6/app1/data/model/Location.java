package no.ntnu.mobapp20g6.app1.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {

    @Expose
    @SerializedName("id")
    private String locationID;

    @Expose
    @SerializedName("gpsLat")
    private String gpsLat;

    @Expose
    @SerializedName("gpsLong")
    private String gpsLong;

    @Expose
    @SerializedName("streetAddress")
    private String streetAddress;

    @Expose
    @SerializedName("city")
    private String city;

    @Expose
    @SerializedName("postalCode")
    private Long postalCode;

    @Expose
    @SerializedName("country")
    private String country;

    public Location() {
    }

    public String getLocationID() {
        return locationID;
    }

    public String getGpsLat() {
        return gpsLat;
    }

    public String getGpsLong() {
        return gpsLong;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public Long getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }
}
