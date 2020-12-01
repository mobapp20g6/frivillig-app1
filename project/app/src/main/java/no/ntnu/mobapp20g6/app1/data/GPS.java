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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.Executor;

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
    private final MutableLiveData<Location> currentGPSLocationLiveData;
    private final Context currentContext;
    private final Activity mainActivity;
    protected LocationManager locationManager;
    FusedLocationProviderClient fusedLocationClient;

    public GPS(Context currentContext, Activity mainActivity) {
        currentGPSLocationLiveData = new MutableLiveData<>();
        this.currentContext = currentContext;
        this.mainActivity = mainActivity;
        locationManager = (LocationManager) currentContext.getSystemService(LOCATION_SERVICE);
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity);
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
     */
    public void askForPermissionGPS() {
        if(mainActivity != null) {
            ActivityCompat.requestPermissions(mainActivity,
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

    /**
     * Start location manager to listen for location updates.
     * Only works if permission for "ACCESS_FINE_LOCATION" is granted.
     */
    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(currentContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(currentContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Location permission denied.");
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(countdownFinished) {
            startCountDownTimer();
            getCurrentLocation();
        }
    }

    public boolean hasGpsPermission() {
        if (ActivityCompat.checkSelfPermission(currentContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(currentContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  true;
        } else {
            return  false;
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
     * Gets the current GPS location of the user if permission is granted and GPS is enabled
     * and places it in the Live data object "currentGPSLocationLiveData".
     */
    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(currentContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(currentContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Location permission denied.");
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(mainActivity, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            System.out.println("Location found!");
                            currentGPSLocationLiveData.setValue(location);
                        }
                    });
        }
    }
}
