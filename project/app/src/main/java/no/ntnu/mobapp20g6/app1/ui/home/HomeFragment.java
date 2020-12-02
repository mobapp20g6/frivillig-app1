package no.ntnu.mobapp20g6.app1.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.WelcomeActivity;
import no.ntnu.mobapp20g6.app1.ui.account.UserAccountViewModel;
import no.ntnu.mobapp20g6.app1.ui.account.UserAccountViewModelFactory;
import no.ntnu.mobapp20g6.app1.ui.login.LoginViewModel;
import no.ntnu.mobapp20g6.app1.ui.login.LoginViewModelFactory;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private UserAccountViewModel userAccountViewModel;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        userAccountViewModel =
                new ViewModelProvider(requireActivity(), new UserAccountViewModelFactory()).get(UserAccountViewModel.class);
        navController = NavHostFragment.findNavController(getParentFragment());
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final Button accountBtn = root.findViewById(R.id.home_btn_account);
        final Button createTaskBtn = root.findViewById(R.id.home_btn_task);
        final Button logoutBtn = root.findViewById(R.id.home_btn_logout);
        final Button groupBtn = root.findViewById(R.id.home_btn_group);

        createTaskBtn.setOnClickListener(onClick -> {
            navController.navigate(R.id.action_nav_home_to_nav_createtask);
        });
        groupBtn.setOnClickListener(onClick -> {
            Toast.makeText(getContext(), R.string.no_functionality_added, Toast.LENGTH_LONG).show();
        });
        accountBtn.setOnClickListener(onClick -> {
            navController.navigate(R.id.action_nav_home_to_nav_account);
        });
        logoutBtn.setOnClickListener(onClick -> {
            if (userAccountViewModel.logoutCurrentUser()) {
                Toast.makeText(getContext(), "Logged out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), WelcomeActivity.class));
            } else {
                Toast.makeText(getContext(), R.string.no_functionality_added, Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }
}