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
import no.ntnu.mobapp20g6.app1.ui.login.LoginViewModel;
import no.ntnu.mobapp20g6.app1.ui.login.LoginViewModelFactory;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private LoginViewModel loginViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final Button accountBtn = root.findViewById(R.id.home_btn_account);
        final Button createTaskBtn = root.findViewById(R.id.home_btn_task);
        final Button logoutBtn = root.findViewById(R.id.home_btn_logout);
        final Button groupBtn = root.findViewById(R.id.home_btn_group);

        accountBtn.setOnClickListener(onClick -> {
            Toast.makeText(getContext(), R.string.no_functionality_added, Toast.LENGTH_LONG).show();
        });
        createTaskBtn.setOnClickListener(onClick -> {
            Toast.makeText(getContext(), R.string.no_functionality_added, Toast.LENGTH_LONG).show();
        });
        logoutBtn.setOnClickListener(onClick -> {
            Toast.makeText(getContext(), R.string.no_functionality_added, Toast.LENGTH_LONG).show();

        });
        groupBtn.setOnClickListener(onClick -> {
            Toast.makeText(getContext(), R.string.no_functionality_added, Toast.LENGTH_LONG).show();
        });
        return root;
    }
}