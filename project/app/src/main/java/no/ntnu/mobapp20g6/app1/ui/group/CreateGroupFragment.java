package no.ntnu.mobapp20g6.app1.ui.group;

import androidx.annotation.StringRes;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.data.model.Group;

public class CreateGroupFragment extends Fragment {

    private CreateGroupViewModel cgViewModel;

    public static CreateGroupFragment newInstance() {
        return new CreateGroupFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_group_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cgViewModel = new ViewModelProvider(this, new CreateGroupViewModelFactory())
                .get(CreateGroupViewModel.class);

        final Button createButton = view.findViewById(R.id.create_group_create_button);
        final EditText groupNameText = view.findViewById(R.id.create_group_name_input);
        final EditText groupDescText = view.findViewById(R.id.create_group_description_input);
        final EditText groupOrgId = view.findViewById(R.id.create_group_org_id_input);

        cgViewModel.getCreateGroupFormState().observe(getViewLifecycleOwner(), new Observer<CreateGroupFormState>() {
            @Override
            public void onChanged(CreateGroupFormState createGroupFormState) {
                if (createGroupFormState == null) {
                    return;
                }
                createButton.setEnabled(createGroupFormState.isDataValid());
                if (createGroupFormState.getGroupNameError() != null) {
                    groupNameText.setError(getString(createGroupFormState.getGroupNameError()));
                }
                if (createGroupFormState.getGroupDescError() != null) {
                    groupDescText.setError(getString(createGroupFormState.getGroupDescError()));
                }
            }
        });

        cgViewModel.getCreateGroupResult().observe(getViewLifecycleOwner(), new Observer<CreateGroupResult>() {
            @Override
            public void onChanged(CreateGroupResult createGroupResult) {
                if (createGroupResult != null) {
                    return;
                }
                if (createGroupResult.getError() != null) {
                    showGroupCreationFailed(createGroupResult.getError());
                } else {
                    System.out.println("FÅR RESULT TILBAKE FRÅ STUFF");
                    showGroupCreationSuccess(createGroupResult.getSuccess());
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                cgViewModel.createGroupInputChange(
                        groupNameText.getText().toString(),
                        groupDescText.getText().toString(),
                        groupOrgId.getText().toString());
            }
        };
        groupNameText.addTextChangedListener(afterTextChangedListener);
        groupDescText.addTextChangedListener(afterTextChangedListener);
        groupDescText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    cgViewModel.createGroup(
                            groupNameText.getText().toString(),
                            groupDescText.getText().toString(),
                            groupOrgId.getText().toString());
                }
                return false;
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cgViewModel.createGroup(
                        groupNameText.getText().toString(),
                        groupDescText.getText().toString(),
                        groupOrgId.getText().toString());
            }
        });
/*        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean nameGiven = true;
                boolean descGiven = true;
                if (nameEdit.getText().length() == 0) {
                    nameEdit.setError("Name of group is required.");
                    nameGiven = false;
                }
                if (descriptionEdit.getText().length() == 0) {
                    descriptionEdit.setError("Description of group is required.");
                    descGiven = false;
                }
                if (nameGiven && descGiven) {
                    cgViewModel.createGroup(nameEdit.getText().toString(), descriptionEdit.getText().toString(), null);
                    Toast.makeText(getContext().getApplicationContext(), "Group Created", Toast.LENGTH_LONG);
                }
            }
        });*/
    }

    private void showGroupCreationSuccess(Group success) {
        if(getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    success.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void showGroupCreationFailed(@StringRes Integer error) {
        if(getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    error,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel

    }

}