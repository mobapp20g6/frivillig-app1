package no.ntnu.mobapp20g6.app1.ui.group;

import androidx.annotation.StringRes;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.concurrent.atomic.AtomicBoolean;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.utilities.GPS;
import no.ntnu.mobapp20g6.app1.data.model.Group;

import static android.app.Activity.RESULT_OK;

public class CreateGroupFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private CreateGroupViewModel cgViewModel;
    private NavController navController;
    private Context context;
    private GPS gps;

    public static CreateGroupFragment newInstance() {
        return new CreateGroupFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.navController = NavHostFragment.findNavController(getParentFragment());
        this.cgViewModel = new ViewModelProvider(this, new CreateGroupViewModelFactory())
                .get(CreateGroupViewModel.class);
        this.context = getContext();
        if (context != null) {
            this.gps = new GPS(context, getActivity());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            cgViewModel.setImageUriPathAfterCaptureIntent();
        } else {
            cgViewModel.deleteImageFileAfterCapture();
        }
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
        final Button addPicBtn = view.findViewById(R.id.create_group_extras_btn_picture);
        final Button removePicBtn = view.findViewById(R.id.create_group_extras_btn_remove_picture);

        final Button createBtn = view.findViewById(R.id.create_group_confirmation_input_btn_create);
        final Button cancelBtn = view.findViewById(R.id.create_group_confirmation_input_btn_cancel);

        initBtns();
        observeLiveData();

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

        addLocBtn.setOnClickListener(v -> {
            cgViewModel.onButtonPressRunGpsBasedOnSetState();
        });

        removeLocBtn.setOnClickListener(v -> {
            cgViewModel.removeGpsAndLiveData();
        });

        addPicBtn.setOnClickListener(v -> {
            dispatchIntent();
        });

        removePicBtn.setOnClickListener(v -> {
            cgViewModel.getPictureMutableLiveData().setValue(null);
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
                createGroup(groupName, groupDesc, groupOrgID);
                //TODO: Navigate to GroupFragment.
            }
        });

        cancelBtn.setOnClickListener(v -> {
            groupOrgIdText.setText("");
            groupNameText.setText("");
            groupDescText.setText("");
            cgViewModel.getPictureMutableLiveData().setValue(null);
            cgViewModel.getLocationStateMutableLiveData().setValue(null);
            cgViewModel.deleteImageFileAfterCapture();
            showUserFeedback(R.string.create_group_cancel_action);
            navController.navigate(R.id.action_nav_group_to_nav_home);
        });
    }

    private void observeLiveData() {
        cgViewModel.getPictureMutableLiveData().observe(getViewLifecycleOwner(), this::displayPicture);
        cgViewModel.getLocationLiveData().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                cgViewModel.onGpsResultUpdateSetState(location, null);

            }
        });

        cgViewModel.getLocationStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayLoc(s);
            }
        });

    }

    private void initBtns() {
        cgViewModel.initGps(getContext(), getActivity());
        displayLoc(cgViewModel.getLocationStateMutableLiveData().getValue());
        displayPicture(cgViewModel.getPictureMutableLiveData().getValue());
    }

    private void displayLoc(String state) {
        Button addLocBtn = getView().findViewById(R.id.create_group_extras_input_btn_location);
        Button removeLocBtn = getView().findViewById(R.id.create_group_extras_btn_remove_location);
        if (state == null) {
            removeLocBtn.setVisibility(View.GONE);
            addLocBtn.setVisibility(View.GONE);
        } else {
            switch (state) {
                case "ready":
                    addLocBtn.setText("Add Location");
                    addLocBtn.setEnabled(true);
                    addLocBtn.setVisibility(View.VISIBLE);
                    removeLocBtn.setVisibility(View.GONE);
                    break;
                case "aquire":
                    addLocBtn.setText("Getting GPS position fix");
                    addLocBtn.setEnabled(false);
                    cgViewModel.startGpsFailureTimer();
                    break;
                case "set":
                    addLocBtn.setEnabled(true);
                    addLocBtn.setVisibility(View.GONE);
                    removeLocBtn.setVisibility(View.VISIBLE);
                    break;
                case "denied":
                    addLocBtn.setVisibility(View.VISIBLE);
                    removeLocBtn.setVisibility(View.GONE);
                    addLocBtn.setText("Permission denied, try again ?");
                    break;
                case "failed":
                    addLocBtn.setEnabled(false);
                    addLocBtn.setVisibility(View.VISIBLE);
                    removeLocBtn.setVisibility(View.GONE);
                    addLocBtn.setText("Unable to get GPS, restart app");
                    break;
                case "timeout":
                    addLocBtn.setText("Failed, try again ?");
                    addLocBtn.setEnabled(true);
                    addLocBtn.setVisibility(View.VISIBLE);
                    removeLocBtn.setVisibility(View.GONE);
                    break;

                default:
                    break;
            }
        }
    }

    private void dispatchIntent(){
        cgViewModel.startImageCaptureIntent(REQUEST_IMAGE_CAPTURE, this, getContext());
    }

    private void displayPicture(String filePath) {
        Button addPicBtn = getView().findViewById(R.id.create_group_extras_btn_picture);
        Button removePicBtn = getView().findViewById(R.id.create_group_extras_btn_remove_picture);
        ImageView picture = getView().findViewById(R.id.create_group_extras_preview_picture);
        if (filePath == null) {
            addPicBtn.setVisibility(View.VISIBLE);
            removePicBtn.setVisibility(View.GONE);
            picture.setVisibility(View.GONE);
            picture.setImageBitmap(null);
        } else {
            addPicBtn.setVisibility(View.GONE);
            removePicBtn.setVisibility(View.VISIBLE);
            Bitmap image = BitmapFactory.decodeFile(filePath);
            picture.setImageBitmap(image);
            picture.setVisibility(View.VISIBLE);
        }
    }

    private void createGroup(String groupName, String groupDesc, String groupOrgID) {
        cgViewModel.createGroup(groupName, groupDesc, groupOrgID, createGroupCallBack -> {
                    if (createGroupCallBack == null) {
                        showUserFeedback(R.string.create_group_failed_creation);
                    } else {
                        showUserFeedback(R.string.create_group_success_creation);
                        if (cgViewModel.isLocationSet()) {
                            addLocToGroup(createGroupCallBack);
                        }
                        if (cgViewModel.isPictureSet()) {
                            addPicToGroup(createGroupCallBack.getGroupId(),
                                    cgViewModel.getPictureMutableLiveData().getValue());
                        }
                    }
                });
    }

    private void addPicToGroup(
            Long groupID, String filePath) {
        cgViewModel.addPicToGroup(groupID, filePath, addPicToGroupCallBack -> {
            if (addPicToGroupCallBack == null) {
                showUserFeedback(R.string.create_group_failed_pic_add);
            } else {
                showUserFeedback(R.string.create_group_success_pic_add);
            }
        });
    }

    private void addLocToGroup(
            Group group) {
        cgViewModel.addLocToGroup(group,
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
}