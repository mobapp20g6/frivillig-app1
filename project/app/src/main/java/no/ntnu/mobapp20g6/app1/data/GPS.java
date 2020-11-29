package no.ntnu.mobapp20g6.app1.data;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static android.content.Context.LOCATION_SERVICE;

/**
 * The purpose of this class is to get the current location of the user.
 * The current location will be set to a live data object so it can be observed and handled
 * when the location changes.
 * Its important to stop LocationListener to listen for location updates when this class is not used anymore.
 * For this use function "stopLocationUpdates" in "onDestroy" in the fragment this class is used in.
 * @author TrymV
 */
public class GPS implements LocationListener {
    private boolean countdownFinished = true;
    private boolean disabling = false;
    private final MutableLiveData<Location> currentGPSLocationLiveData;
    private final Context currentContext;
    protected LocationManager locationManager;

    public GPS(Context currentContext) {
        currentGPSLocationLiveData = new MutableLiveData<>();
        this.currentContext = currentContext;
        locationManager = (LocationManager) currentContext.getSystemService(LOCATION_SERVICE);
    }

    /**
     * Return the current GPS location as live data object.
     * @return current GPS location as live data object.
     */
    public LiveData<Location> getCurrentGPSLocationLiveData() {
        return currentGPSLocationLiveData;
    }

    /**
     * Ask user for permission to use phone GPS.
     * @param currentActivity activity where call is called from. Cannot be null.
     */
    public void askForPermissionGPS(Activity currentActivity) {
        if(currentActivity != null) {
            ActivityCompat.requestPermissions(currentActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    99);
        }
    }

    /**
     * Stop location manager to listen for location updates.
     * This must be called in onDestroy from fragments which is using this class.
     */
    public void stopLocationUpdates() {
        locationManager.removeUpdates(this);
    }

    public void stopLocationUpdatesAfterDelay() {
        disabling = true;

    }
    private void forceStopLocationManager() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(countdownFinished) {
            Location currentLocation = getCurrentLocation();
            startCountDownTimer();
            if(currentLocation != null) {
                currentGPSLocationLiveData.setValue(location);
            }
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Required so an exception is not thrown.
    }

    /**
     * An off-delay to stop the listener slightly after the last location was
     * retrieved, as the normal stop doesn't always work with buttons
     */
    private void startOffDelayTimer() {
        new CountDownTimer(100, 10) {

            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                disabling = false;
                System.out.println("Off-delay reached, killed LocationManager listener");
                forceStopLocationManager();
            }
        }.start();
    }

    /**
     * Starts a 3 seconds countdown and sets countDownFinished to false.
     * After 3 seconds countDownFinished will be set to true.
     */
    private void startCountDownTimer() {
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                countdownFinished = false;
            }

            public void onFinish() {
                countdownFinished = true;
            }
        }.start();
    }

    /**
     * Gets the current GPS location of the user if permission is granted and GPS is enabled.
     * @return true if GPS location was successfully received else this will return null.
     */
    public Location getCurrentLocation() {
        if (disabling) {
            startOffDelayTimer();
            System.out.println("Stopping due to user request");
        }
        if (ActivityCompat.checkSelfPermission(currentContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(currentContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Location permission denied.");
        } else {
            //Checks for GPS status.
            Location location;
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                System.out.println("Getting GPS location");
                return location;
            }
        }
        return null;
    }
}
