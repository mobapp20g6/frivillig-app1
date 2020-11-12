package no.ntnu.mobapp20g6.app1.data.model;

public class Location {

    private String locationID;

    private String gpsLat;

    private String gpsLong;

    private String streetAddress;

    private String city;

    private Long postalCode;

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
