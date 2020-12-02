package no.ntnu.mobapp20g6.app1.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import no.ntnu.mobapp20g6.app1.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final Button ownTaskButton = root.findViewById(R.id.home_btn_own);
        final Button assignedTaskButton = root.findViewById(R.id.home_btn_assigned);
        final Button publicTaskButton = root.findViewById(R.id.home_btn_public);
        final Button groupButton = root.findViewById(R.id.home_btn_group);

        ownTaskButton.setOnClickListener(onClick -> {
            Toast.makeText(getContext(), R.string.no_functionality_added, Toast.LENGTH_LONG).show();
        });
        assignedTaskButton.setOnClickListener(onClick -> {
            Toast.makeText(getContext(), R.string.no_functionality_added, Toast.LENGTH_LONG).show();
        });
        publicTaskButton.setOnClickListener(onClick -> {
            Toast.makeText(getContext(), R.string.no_functionality_added, Toast.LENGTH_LONG).show();
        });
        groupButton.setOnClickListener(onClick -> {
            Toast.makeText(getContext(), R.string.no_functionality_added, Toast.LENGTH_LONG).show();
        });
        return root;
    }
}