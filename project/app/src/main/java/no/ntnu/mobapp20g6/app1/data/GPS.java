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
import androidx.lifecycle.MutableLiveData;

import static android.content.Context.LOCATION_SERVICE;

public class GPS implements LocationListener {
    private boolean countdownFinished = true;
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
    public MutableLiveData<Location> getCurrentGPSLocationLiveData() {
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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(countdownFinished) {
            Location currentLocation = getCurrentLocation();
            if(currentLocation != null) {
                startCountDownTimer();
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
