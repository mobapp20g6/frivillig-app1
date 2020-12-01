package no.ntnu.mobapp20g6.app1.ui.group.display;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.data.RestService;
import no.ntnu.mobapp20g6.app1.data.api.PictureApi;
import no.ntnu.mobapp20g6.app1.data.model.Group;
import no.ntnu.mobapp20g6.app1.data.model.Location;
import no.ntnu.mobapp20g6.app1.data.model.Picture;
import no.ntnu.mobapp20g6.app1.data.model.Task;
import no.ntnu.mobapp20g6.app1.data.model.User;

public class DisplayGroupFragment extends Fragment {

    private DisplayGroupViewModel dgViewModel;

    public static DisplayGroupFragment newInstance() {
        return new DisplayGroupFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_display_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dgViewModel = new ViewModelProvider(this, new DisplayGroupViewModelFactory())
                .get(DisplayGroupViewModel.class);

        Picasso picasso = dgViewModel.loadPicasso(getContext());

        dgViewModel.getGroupOwner().observe(getViewLifecycleOwner(), new
                Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        initOwnerButtons(aBoolean);
                    }
                });
        initOwnerButtons(dgViewModel.getGroupOwner().getValue());
        displayGroup(view, picasso);

        final Button fullScreenBtn = view.findViewById(R.id.display_group_map_button);

        fullScreenBtn.setOnClickListener(v-> {
            NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_nav_display_group_to_groupMapFragment);
        });
    }

    private void initOwnerButtons(Boolean aBoolean) {

        User user = dgViewModel.getLoggedInUser();
        Group group = user.getUserGroup();
        dgViewModel.isOwnerOfGroup(group.getGroupId(), booleanResult -> {
            final Button updateBtn = getView().findViewById(R.id.display_group_btn_update);
            final Button deleteBtn = getView().findViewById(R.id.display_group_btn_delete);
            if (booleanResult.equals(Boolean.TRUE)) {
                updateBtn.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.VISIBLE);
            } else {
                updateBtn.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.GONE);
            }
        });
    }

    private void displayGroup(@NonNull View view, Picasso picasso) {
        final TextView groupName = view.findViewById(R.id.display_group_title_text);
        final TextView groupDesc = view.findViewById(R.id.display_group_description_data);
        final ImageView groupPic = view.findViewById(R.id.display_group_image_view);
        final Button fullscreenMapBtn = view.findViewById(R.id.display_group_map_button);
        final MapView groupMap = view.findViewById(R.id.display_group_map_view);

        User user = dgViewModel.getLoggedInUser();
        if (user != null) {
            Group userGroup = user.getUserGroup();
            if (userGroup!= null) {
                groupName.setText(user.getUserGroup().getGroupName());
                groupDesc.setText(user.getUserGroup().getGroupDescription());
                Location loc = userGroup.getLocation();
                if (loc == null) {
                    groupMap.setVisibility(View.GONE);
                    fullscreenMapBtn.setVisibility(View.GONE);
                } else {
                    makeMap(getView(), loc);
                }
                Picture picture = userGroup.getPicture();
                if (picture != null) {
                    String pictureID = picture.getId();
                    picasso.load(RestService.DOMAIN + PictureApi.PREFIX + "getimage?name=" +
                            pictureID + "&width=" + "480").into(groupPic);
                }
            }
        }
    }

    private void makeMap(View root, Location loc) {
        Context ctx = getContext();
        assert ctx != null;
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        MapView map = (MapView) root.findViewById(R.id.display_group_map_view);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(13.5);
        GeoPoint startPoint = new GeoPoint(Double.parseDouble(loc.getGpsLat()), Double.parseDouble(loc.getGpsLong()));
        mapController.setCenter(startPoint);

        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);
    }
}