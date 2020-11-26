package no.ntnu.mobapp20g6.app1.ui.group;

import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.bind.util.ISO8601Utils;

import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;

import no.ntnu.mobapp20g6.app1.R;

public class CreateGroupFragment extends Fragment {

    private CreateGroupViewModel cgViewModel;

    public static CreateGroupFragment newInstance() {
        return new CreateGroupFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cgViewModel = new ViewModelProvider(this, new CreateGroupViewModelFactory())
                .get(CreateGroupViewModel.class);

        final EditText groupNameText = view.findViewById(R.id.create_group_details_name_input);
        final EditText groupDescText = view.findViewById(R.id.create_group_details_desc_input);
        final EditText groupOrgIdText = view.findViewById(R.id.create_group_details_input_orgid);
        final Button brregBtn = view.findViewById(R.id.create_group_details_input_btn_brreg);
        final Button createBtn = view.findViewById(R.id.create_group_confirmation_input_btn_create);

        brregBtn.setOnClickListener(v -> {
            String groupOrgId = groupOrgIdText.getText().toString();
            if (validateOrgId(groupOrgId)){
                cgViewModel.getBrregOrg(groupOrgId, brregCallBack -> {
                    if (brregCallBack != null) {
                        JsonObject embedded = brregCallBack.getAsJsonObject("_embedded");
                        JsonArray entities = (JsonArray) embedded.get("enheter");
                        JsonObject entitylist = (JsonObject) entities.get(0);
                        JsonPrimitive name = (JsonPrimitive) entitylist.get("navn");
                        JsonObject desclist = (JsonObject) entitylist.get("organisasjonsform");
                        JsonPrimitive desc = (JsonPrimitive) desclist.get("beskrivelse");
                        groupNameText.setText(name.toString().replace("\"", ""));
                        groupDescText.setText(desc.toString().replace("\"", ""));
                    }
                });
            }
        });

        //TODO: Make it so that only name and description is sent if no succesfull callback from brreg.
        createBtn.setOnClickListener(v -> {
            String groupName = groupNameText.getText().toString();
            String groupDesc = groupDescText.getText().toString();
            String groupOrgID = groupOrgIdText.getText().toString();

            boolean validName = validateTextInput(groupName);
            boolean validDesc = validateTextInput(groupDesc);
            boolean validOrgID = validateOrgId(groupOrgID);

            if (!validName) {
                groupNameText.setError(getString(R.string.create_group_group_name_invalid));
            }

            if (!validDesc) {
                groupDescText.setError(getString(R.string.create_group_group_desc_invalid));
            }

            if (!validOrgID) {
                groupOrgIdText.setError(getString(R.string.create_group_group_orgID_invalid));
            }

            if (validName && validDesc && validOrgID) {
                createGroup(groupName, groupDesc, groupOrgID);
            }
        });
    }

    private void createGroup(String groupName, String groupDesc, String groupOrgID) {
        cgViewModel.createGroup(
                groupName,
                groupDesc,
               groupOrgID,
                createGroupCallBack -> {
                    if (createGroupCallBack == null) {
                        showUserFeedback(R.string.create_group_failed_creation);
                    } else {
                        showUserFeedback(R.string.create_group_success_creation);
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