package no.ntnu.mobapp20g6.app1.ui.account;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.data.model.LoggedInUser;

public class UserAccountFragment extends Fragment {

    public static UserAccountFragment newInstance() {
        return new UserAccountFragment();
    }

    private UserAccountViewModel userAccountViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userAccountViewModel =
                new ViewModelProvider(requireActivity(), new UserAccountViewModelFactory()).get(UserAccountViewModel.class);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_useraccount, container, false);
        final TextView userName = root.findViewById(R.id.fragment_account_username);
        final TextView userEmail = root.findViewById(R.id.account_email_value);
        final TextView userGroup = root.findViewById(R.id.account_group_value);
        final TextView userCreated = root.findViewById(R.id.account_created_value);

        final EditText fieldOldPass = root.findViewById(R.id.resetpass_old);
        final EditText fieldNewPass = root.findViewById(R.id.resetpass_new);
        final EditText fieldNewVerifyPass = root.findViewById(R.id.resetpass_verify);

        final Button btnResetPass = root.findViewById(R.id.account_resetpw_ok);
        final Button btnResetClear = root.findViewById(R.id.account_resetpw_cancel);

        btnResetPass.setEnabled(false);

        final TextView sessionFooter = root.findViewById(R.id.account_session_remain_text);

        userAccountViewModel.getCurrentUserLiveData().observe(getViewLifecycleOwner(), new Observer<LoggedInUser>() {
            @Override
            public void onChanged(LoggedInUser user) {
                String firstname = user.getUserFirstName();
                String lastname = user.getUserLastName();
                if (firstname.length()+lastname.length() > 32) {
                    userName.setText(firstname);
                } else {
                    userName.setText(firstname + " " + lastname);
                }

                userEmail.setText(user.getUserEmail());
                Date created = user.getUserCreated();
                DateFormat shortDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                userCreated.setText(shortDate.format(created));
                sessionFooter.setText("Session expires: " + shortDate.format(user.getExpireTime()));

                if (user.getUserGroup() != null) {
                    userGroup.setText(user.getUserGroup().getGroupName());
                } else {
                    userGroup.setText("No organization associated");
                    userGroup.setTextColor(Color.LTGRAY);
                }

            }
        });

        userAccountViewModel.fetchUserFromServer();

        return root;
    }

}