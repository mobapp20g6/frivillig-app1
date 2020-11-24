package no.ntnu.mobapp20g6.app1.ui.task;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.data.model.Task;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Display a full screen map with the location of the current task and user GPS location.
 * User GPS location will only show if permission is granted and GPS is turned on.
 * @author TrymV
 */
public class MapFragment extends Fragment implements LocationListener {

    private TaskViewModel taskViewModel;
    private MapView map;
    private Context context;
    protected LocationManager locationManager;
    private Marker myLocationMarker;
    private boolean countDownFinished = true;

    private static final long MIN_DISTANCE_FOR_GPS_UPDATE = 10;
    private static final long MIN_TIME_FOR_GPS_UPDATE = 1000L * 10; //10 sec

    private double latitude;
    private double longitude;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskViewModel = new ViewModelProvider(requireActivity(), new TaskViewModelFactory()).get(TaskViewModel.class);
        context = getContext();
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        //Ask for permission to use Fine Location.
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                99);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        Task currentActiveTask = taskViewModel.getActiveTaskLiveData().getValue();
        map = (MapView) root.findViewById(R.id.mapView);
        myLocationMarker = new Marker(map);

        //Making the map
        makeMap(currentActiveTask);

        if(getCurrentLocation()) {
            GeoPoint myLocation = new GeoPoint(latitude, longitude);
            myLocationMarker.setPosition(myLocation);
            myLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            myLocationMarker.setTitle("My position");
            myLocationMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_my_location_24, null));
            map.getOverlays().add(myLocationMarker);
        }

        return root;
    }

    /**
     * Makes a map in fragment if current active task got a GPS location.
     * If there are not a GPS location this will hide the map from the layout fragment.
     * @param currentActiveTask current active task.
     */
    private void makeMap(Task currentActiveTask) {
        //Adding osmdroid (open street map) to layout if Task got a GPS location.
        if (currentActiveTask.getLocation() != null && !currentActiveTask.getLocation().getGpsLat().isEmpty()) {
            Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setMultiTouchControls(true);
            IMapController mapController = map.getController();
            mapController.setZoom(13.5);
            GeoPoint startPoint = new GeoPoint(Double.parseDouble(currentActiveTask.getLocation().getGpsLat()), Double.parseDouble(currentActiveTask.getLocation().getGpsLong()));
            mapController.setCenter(startPoint);

            //Making marker
            Marker startMarker = new Marker(map);
            startMarker.setPosition(startPoint);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            startMarker.setTitle(currentActiveTask.getTitle());
            map.getOverlays().add(startMarker);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Sets the user location on the map.
        if(countDownFinished && getCurrentLocation()) {
            startCountDownTimer();
            GeoPoint myLocation = new GeoPoint(latitude, longitude);
            myLocationMarker.setPosition(myLocation);
            myLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            myLocationMarker.remove(map);
            map.getOverlays().add(myLocationMarker);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //To stop locationListener to listen from location updates.
        locationManager.removeUpdates(this);
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
     * Gets the current GPS location of the user if permission is granted and GPS is enabled.
     * @return true if GPS location was successfully received.
     */
    public boolean getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Location permission denied.");
            return false;
        } else {
            //Checks for GPS status.
            Location location;
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_GPS_UPDATE, MIN_DISTANCE_FOR_GPS_UPDATE, this);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                System.out.println("Getting GPS location");
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
            return true;
        }
    }

    /**
     * Starts a 3 seconds countdown and sets countDownFinished to false.
     * After 3 seconds countDownFinished will be set to true.
     */
    private void startCountDownTimer() {
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                countDownFinished = false;
            }

            public void onFinish() {
                countDownFinished = true;
            }
        }.start();
    }
}