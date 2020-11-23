package no.ntnu.mobapp20g6.app1.ui.task;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

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

public class MapFragment extends Fragment {

    private TaskViewModel taskViewModel;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskViewModel = new ViewModelProvider(requireActivity(), new TaskViewModelFactory()).get(TaskViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        Task currentActiveTask = taskViewModel.getActiveTaskLiveData().getValue();

        makeMap(root, currentActiveTask);

        return root;
    }

    /**
     * Makes a map in fragment if current active task got a GPS location.
     * If there are not a GPS location this will hide the map from the layout fragment.
     * @param root root view.
     * @param currentActiveTask current active task.
     */
    private void makeMap(View root, Task currentActiveTask) {
        //Adding osmdroid (open street map) to layout if Task got a GPS location.
        if(currentActiveTask.getLocation() != null && !currentActiveTask.getLocation().getGpsLat().isEmpty()) {
            Context ctx = getContext();
            assert ctx != null;
            Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
            MapView map = (MapView) root.findViewById(R.id.mapView);
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
            map.getOverlays().add(startMarker);
        }
    }
}