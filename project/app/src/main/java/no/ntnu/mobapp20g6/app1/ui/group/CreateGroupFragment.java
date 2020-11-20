package no.ntnu.mobapp20g6.app1.ui.group;

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

import no.ntnu.mobapp20g6.app1.R;

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
        final EditText nameEdit = view.findViewById(R.id.create_group_name_input);
        final EditText descriptionEdit = view.findViewById(R.id.create_group_description_input);

        createButton.setOnClickListener(new View.OnClickListener() {
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
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel

    }

}