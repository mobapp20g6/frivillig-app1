package no.ntnu.mobapp20g6.app1.ui.group;

import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.concurrent.atomic.AtomicBoolean;

import no.ntnu.mobapp20g6.app1.PhotoProvider;
import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.data.GPS;
import no.ntnu.mobapp20g6.app1.data.model.Group;

import static android.app.Activity.RESULT_OK;

public class CreateGroupFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private CreateGroupViewModel cgViewModel;
    private NavController navController;
    private Context context;
    private GPS gps;
    private PhotoProvider photoProvider;

    public static CreateGroupFragment newInstance() {
        return new CreateGroupFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        this.navController = NavHostFragment.findNavController(getParentFragment());
        this.cgViewModel = new ViewModelProvider(this, new CreateGroupViewModelFactory())
                .get(CreateGroupViewModel.class);
        this.gps = new GPS(context);
        this.photoProvider = new PhotoProvider(context);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText groupNameText = view.findViewById(R.id.create_group_details_name_input);
        final EditText groupDescText = view.findViewById(R.id.create_group_details_desc_input);
        final EditText groupOrgIdText = view.findViewById(R.id.create_group_details_input_orgid);
        final Button brregBtn = view.findViewById(R.id.create_group_details_input_btn_brreg);

        final Button addLocBtn = view.findViewById(R.id.create_group_extras_input_btn_location);
        final Button removeLocBtn = view.findViewById(R.id.create_group_extras_btn_remove_location);
        final ImageView picture = view.findViewById(R.id.create_group_extras_preview_picture);
        final Button addPicBtn = view.findViewById(R.id.create_group_extras_btn_picture);
        final Button removePicBtn = view.findViewById(R.id.create_group_extras_btn_remove_picture);

        final Button createBtn = view.findViewById(R.id.create_group_confirmation_input_btn_create);
        final Button cancelBtn = view.findViewById(R.id.create_group_confirmation_input_btn_cancel);

        removeLocBtn.setVisibility(View.GONE);
        removePicBtn.setVisibility(View.GONE);

        displayPictureInUi(cgViewModel.getPictureMutableLiveData().getValue());

        AtomicBoolean voluntaryOrgFound = new AtomicBoolean(false);

        brregBtn.setOnClickListener(v -> {
            String groupOrgId = groupOrgIdText.getText().toString();
            if (!validateOrgId(groupOrgId)){
                groupOrgIdText.setError(getString(R.string.create_group_group_orgID_invalid));
            } else {
                cgViewModel.getBrregOrg(groupOrgId, brregCallBack -> {
                    if (brregCallBack != null) {
                        JsonObject embedded = brregCallBack.getAsJsonObject("_embedded");
                        if (embedded != null) {
                            voluntaryOrgFound.set(true);

                            JsonArray entities = (JsonArray) embedded.get("enheter");
                            JsonObject entitylist = (JsonObject) entities.get(0);
                            JsonPrimitive name = (JsonPrimitive) entitylist.get("navn");
                            JsonObject desclist = (JsonObject) entitylist.get("organisasjonsform");
                            JsonPrimitive desc = (JsonPrimitive) desclist.get("beskrivelse");

                            groupNameText.setText(name.toString().replace("\"", ""));
                            groupDescText.setText(desc.toString().replace("\"", "").replace("/", "-"));
                        } else {
                            showUserFeedback(R.string.create_group_brreg_search_failed);
                        }
                    } else {
                        showUserFeedback(R.string.create_group_brreg_search_failed);
                    }
                });
            }
        });

        final Location[] loc = {null};

        addLocBtn.setOnClickListener(v -> {
            gps.askForPermissionGPS(getActivity());
            loc[0] = gps.getCurrentLocation();
            if (loc[0] != null) {
                showUserFeedback(R.string.location_found);
                removeLocBtn.setVisibility(View.VISIBLE);
                addLocBtn.setVisibility(View.GONE);
            }
        });

        removeLocBtn.setOnClickListener(v -> {
            loc[0] = null;
            addLocBtn.setVisibility(View.VISIBLE);
            removeLocBtn.setVisibility(View.GONE);
            showUserFeedback(R.string.location_removed);
        });

        addPicBtn.setOnClickListener(v -> {
            removePicBtn.setVisibility(View.VISIBLE);
            picture.setVisibility(View.VISIBLE);
            dispatchIntent();
            addPicBtn.setVisibility(View.GONE);
        });

        removePicBtn.setOnClickListener(v -> {
            addPicBtn.setVisibility(View.VISIBLE);
            picture.setVisibility(View.GONE);
            removePicBtn.setVisibility(View.GONE);
        });

        createBtn.setOnClickListener(v -> {
            String groupName = groupNameText.getText().toString();
            String groupDesc = groupDescText.getText().toString();
            String groupOrgID = groupOrgIdText.getText().toString();

            boolean validName = validateTextInput(groupName);
            boolean validDesc = validateTextInput(groupDesc);
            boolean validOrgId = validateOrgId(groupOrgID);

            if (!validName) {
                groupNameText.setError(getString(R.string.create_group_group_name_invalid));
            }

            if (!validDesc) {
                groupDescText.setError(getString(R.string.create_group_group_desc_invalid));
            }

            if (voluntaryOrgFound.get() && validOrgId) {
                voluntaryOrgFound.set(false);
            } else{
                showUserFeedback(R.string.create_group_no_voluntary_org_feedback);
                groupOrgID = "";
            }

            if (validName && validDesc) {
                createGroup(groupName, groupDesc, groupOrgID, loc);
                //TODO: Navigate to GroupFragment.
            }
        });

        cancelBtn.setOnClickListener(v -> {
            showUserFeedback(R.string.create_group_cancel_action);
            navController.navigate(R.id.action_nav_group_to_nav_home);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gps.stopLocationUpdates();
    }

    private void dispatchIntent(){
        photoProvider.dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE, getParentFragment());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("imageBitmap");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            cgViewModel.getPictureMutableLiveData().setValue(imageBitmap);
            displayPictureInUi(imageBitmap);
        }
    }

    private void displayPictureInUi(Bitmap image) {
        ImageView picture = getView().findViewById(R.id.create_group_extras_preview_picture);
        if (image != null) {
            picture.setImageBitmap(image);
            picture.setVisibility(View.VISIBLE);
        } else {
            picture.setVisibility(View.GONE);
            picture.setImageBitmap(null);
        }
    }

    private void createGroup(String groupName, String groupDesc, String groupOrgID, Location[] loc) {
        String latitude = null;
        String longitude = null;
        if (loc[0] != null) {
            latitude = String.valueOf(loc[0].getLatitude());
            longitude= String.valueOf(loc[0].getLongitude());
        }
        String finalLatitude = latitude;
        String finalLongitude = longitude;
        cgViewModel.createGroup(
                groupName,
                groupDesc,
               groupOrgID,
                createGroupCallBack -> {
                    if (createGroupCallBack == null) {
                        showUserFeedback(R.string.create_group_failed_creation);
                    } else {
                        showUserFeedback(R.string.create_group_success_creation);
                        if (finalLatitude != null && finalLongitude !=null) {
                            addLocToGroup(createGroupCallBack.getGroupId(),
                                    finalLatitude, finalLongitude,
                                    null, null, null, null);

                        }
                    }
                });
    }

    private void addLocToGroup(
            Long groupID,
            String latitude, String longitude,
            String streetAddr, String city, Long postal, String country) {
        cgViewModel.addLocToGroup(groupID,
                latitude, longitude,
                streetAddr, city, postal, country,
                addLocToGroupCallBack -> {
                    if (addLocToGroupCallBack == null) {
                        showUserFeedback(R.string.create_group_failed_loc_add);
                    } else {
                        showUserFeedback(R.string.create_group_success_loc_add);
                    }
                });
    }

    private boolean validateTextInput(String string) {
        return !(string.equals("") || string.length() > 255);
    }

    private boolean validateOrgId(String groupOrgID) {
        return groupOrgID.length() == 9;
    }

    private void showUserFeedback(@StringRes Integer string) {
        if(getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    string,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel

    }

}