package no.ntnu.mobapp20g6.app1.ui.task;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.text.SimpleDateFormat;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.data.RestService;
import no.ntnu.mobapp20g6.app1.data.api.PictureApi;
import no.ntnu.mobapp20g6.app1.data.model.Task;

public class TaskFragment extends Fragment {

    private TaskViewModel taskViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskViewModel = new ViewModelProvider(requireActivity(), new TaskViewModelFactory()).get(TaskViewModel.class);
    }

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_task, container, false);
        Picasso picasso = taskViewModel.loadPicasso(getContext());
        Task currentActiveTask = taskViewModel.getActiveTaskLiveData().getValue();
        View view = container.getRootView();

        if(currentActiveTask != null) {
            makeMap(root, currentActiveTask);
        }

        //Buttons
        final Button buttonShowParticipants = root.findViewById(R.id.button_task_show_participants);
        final Button buttonAddToCal = root.findViewById(R.id.button_task_add_to_cal);
        final Button buttonJoinTask = root.findViewById(R.id.button_task_join);
        final Button buttonUpdateTask = root.findViewById(R.id.button_task_update);
        final Button buttonDeleteTask = root.findViewById(R.id.button_task_delete);

        final ImageView taskImage = root.findViewById(R.id.task_image);
        final TextView title = root.findViewById(R.id.task_title);
        final TextView description = root.findViewById(R.id.task_description);
        final TextView taskDate = root.findViewById(R.id.task_date);
        final TextView participantCount = root.findViewById(R.id.task_participants);
        final TextView taskGroup = root.findViewById(R.id.task_group);

        //Disable update and delete button if user is not owner of task.
        if(!taskViewModel.isUserOwnerOfTask()) {
            buttonUpdateTask.setEnabled(false);
            buttonDeleteTask.setEnabled(false);
        }
        //Disable join task button if user already is member of task and disable add to calendar button if user is not member of task.
        if(taskViewModel.isUserMemberOfTask()) {
            buttonJoinTask.setEnabled(false);
        } else {
            buttonAddToCal.setEnabled(false);
        }

        //Disable join task button if task is full.
        if(taskViewModel.isTaskFull()) {
            buttonJoinTask.setEnabled(false);
        }

        //Load the task image.
        if(currentActiveTask.getPicture() != null) {
            picasso.load(RestService.DOMAIN + PictureApi.PREFIX + "getimage?name=" + currentActiveTask.getPicture().getId() + "&width=" + "480").into(taskImage);
        }

        title.setText(currentActiveTask.getTitle());
        participantCount.setText("Participants: " + currentActiveTask.getParticipantCount() + "/" + currentActiveTask.getParticipantLimit());
        taskDate.setText("Date: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(currentActiveTask.getScheduleDate()));

        //Sets task description if the task got one.
        if(currentActiveTask.getDescription() != null || !currentActiveTask.getDescription().isEmpty()) {
            description.setText(currentActiveTask.getDescription());
        }

        //Sets the name of group the task is associated with else it will be "Public"
        if(currentActiveTask.getAssociatedGroup() != null) {
            taskGroup.setText(currentActiveTask.getAssociatedGroup().getGroupName());
        }

        //Buttons
        buttonShowParticipants.setOnClickListener(button ->{
            //TODO add functionality to show all participants of task.
        });
        buttonAddToCal.setOnClickListener(button ->{
            //Adds the task schedule date to the phones calendar.
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", taskViewModel.getActiveTaskLiveData().getValue().getScheduleDate().getTime());
            intent.putExtra("allDay", false);
            intent.putExtra("title", taskViewModel.getActiveTaskLiveData().getValue().getTitle());
            startActivity(intent);
        });
        buttonJoinTask.setOnClickListener(button ->{
            //Add the user as a participant of the task.
            if(!taskViewModel.isUserMemberOfTask()) {
                taskViewModel.joinActiveTask(success-> {
                    if(success) {
                        buttonJoinTask.setEnabled(false);
                        setSnackbarText("You successfully join the task!", view, true).show();
                    } else {
                        setSnackbarText("Joining task failed.", view, false);
                    }
                });
            } else {
                setSnackbarText("Joining task failed. You are already member of the task.", view, false);
            }
        });
        buttonUpdateTask.setOnClickListener(button ->{
            //TODO add functionality to update a task.
        });
        buttonDeleteTask.setOnClickListener(button ->{
            //Delete the current active task after conformation.
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(true);
            builder.setTitle("Conformation");
            builder.setMessage("Are you sure you want to delete \"" + currentActiveTask.getTitle() + "\"?");
            builder.setPositiveButton(R.string.alert_confirm, (dialog, which) -> {
                taskViewModel.deleteActiveTask(success ->{
                });
            });
            builder.setNegativeButton(R.string.alert_cancel, (dialog, which) -> {
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        //Observe the task and if there are any changes.
        taskViewModel.getActiveTaskLiveData().observe(getViewLifecycleOwner(), observer->{
            if(taskViewModel.getActiveTaskLiveData().getValue() == null) {
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.nav_home);
            } else {
                participantCount.setText("Participants: " + taskViewModel.getActiveTaskLiveData().getValue().getParticipantCount() + "/" + taskViewModel.getActiveTaskLiveData().getValue().getParticipantLimit());
            }
        });

        return root;
    }

    /**
     * Makes a small bar with text on the screen.
     * @param msg text to be shown on the screen.
     * @param view view the container is holding on.
     * @param success true will show green text and false will show red text.
     * @return Snackbar with text and yellow color.
     */
    private Snackbar setSnackbarText(String msg, View view, boolean success) {
        if(view != null) {
            Snackbar listResultMsg = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
            if(success) {
                listResultMsg.setTextColor(Color.GREEN);
            } else {
                listResultMsg.setTextColor(Color.RED);
            }
            return listResultMsg;
        }
        return null;
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
        } else {
            //Closing the map if task does not have GPS location.
            root.findViewById(R.id.mapView).setVisibility(View.GONE);
        }
    }
}