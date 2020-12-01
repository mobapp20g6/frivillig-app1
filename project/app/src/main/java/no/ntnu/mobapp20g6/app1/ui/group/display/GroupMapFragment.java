package no.ntnu.mobapp20g6.app1.ui.group.display;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.data.GPS;
import no.ntnu.mobapp20g6.app1.data.ds.LoginDataSource;
import no.ntnu.mobapp20g6.app1.data.model.Group;
import no.ntnu.mobapp20g6.app1.data.repo.LoginRepository;

public class GroupMapFragment extends Fragment {
    private LoginRepository loginRepo;
    private MapView map;
    private Context context;
    private GPS gps;

    public GroupMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        if(context != null) {
            gps = new GPS(context);
        }
        loginRepo = LoginRepository.getInstance(new LoginDataSource());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        Group group = loginRepo.getCurrentUser().getUserGroup();
        map = (MapView) root.findViewById(R.id.mapView);

        //GPS marker
        Marker myLocationMarker = new Marker(map);
        myLocationMarker.setTitle("My position");
        myLocationMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_my_location_24, null));
        gps.askForPermissionGPS(getActivity());

        //Making the map
        if(group != null) {
            makeMap(group);
        }

        Location location = gps.getCurrentLocation();
        if(location != null) {
            GeoPoint myLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
            myLocationMarker.setPosition(myLocation);
            myLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(myLocationMarker);
        }

        //Observes the location live data. When a change happens the GPS marker will be updated in the map.
        gps.getCurrentGPSLocationLiveData().observe(getViewLifecycleOwner(), observer->{
            Location currentLocation = gps.getCurrentGPSLocationLiveData().getValue();
            if(currentLocation != null) {
                GeoPoint myLocation = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                myLocationMarker.setPosition(myLocation);
                myLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                myLocationMarker.remove(map);
                map.getOverlays().add(myLocationMarker);
            }
        });

        return root;
    }

    /**
     * Makes a map in fragment if current active task got a GPS location.
     * If there are not a GPS location this will hide the map from the layout fragment.
     * @param group current active task.
     */
    private void makeMap(Group group) {
        //Adding osmdroid (open street map) to layout if Task got a GPS location.
        if (group.getLocation() != null && !group.getLocation().getGpsLat().isEmpty()) {
            Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setMultiTouchControls(true);
            IMapController mapController = map.getController();
            mapController.setZoom(13.5);
            GeoPoint startPoint = new GeoPoint(Double.parseDouble(group.getLocation().getGpsLat()), Double.parseDouble(group.getLocation().getGpsLong()));
            mapController.setCenter(startPoint);

            //Making marker
            Marker startMarker = new Marker(map);
            startMarker.setPosition(startPoint);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            startMarker.setTitle(group.getGroupName());
            map.getOverlays().add(startMarker);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //To stop locationListener to listen for location updates.
        gps.stopLocationUpdates();
    }
}
