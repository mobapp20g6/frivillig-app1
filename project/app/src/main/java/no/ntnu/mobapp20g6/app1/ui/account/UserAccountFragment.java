package no.ntnu.mobapp20g6.app1.ui.account;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import no.ntnu.mobapp20g6.app1.MainActivity;
import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.WelcomeActivity;
import no.ntnu.mobapp20g6.app1.data.Result;
import no.ntnu.mobapp20g6.app1.data.model.LoggedInUser;

/**
 *  Displays a fragment regarding the user account of the logged in user. The user has
 *  the possibility to reset their password on this fragment. Author: NilsJ
 */
public class UserAccountFragment extends Fragment {

    public static UserAccountFragment newInstance() {
        return new UserAccountFragment();
    }

    private UserAccountViewModel userAccountViewModel;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.navController = NavHostFragment.findNavController(getParentFragment());

        this.userAccountViewModel =
                new ViewModelProvider(requireActivity(), new UserAccountViewModelFactory()).get(UserAccountViewModel.class);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_useraccount, container, false);

        if (userAccountViewModel.isUserLoggedIn()) {
            userAccountViewModel.fetchUserFromServer();
        } else {
            // User not logged in -> GOTO login activity
            startActivity(new Intent(getActivity(), WelcomeActivity.class));
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final TextView userName = view.findViewById(R.id.fragment_account_username);
        final TextView userEmail = view.findViewById(R.id.account_email_value);
        final TextView userGroup = view.findViewById(R.id.account_group_value);
        final TextView userCreated = view.findViewById(R.id.account_created_value);

        final EditText fieldOldPass = view.findViewById(R.id.resetpass_old);
        final EditText fieldNewPass = view.findViewById(R.id.resetpass_new);
        final EditText fieldNewVerifyPass = view.findViewById(R.id.resetpass_verify);

        final Button btnResetPass = view.findViewById(R.id.account_resetpw_ok);
        final Button btnResetClear = view.findViewById(R.id.account_resetpw_cancel);
        final ProgressBar loading = view.findViewById(R.id.account_loading);
        final Snackbar snackbar = Snackbar.make(view,null,Snackbar.LENGTH_LONG);

        btnResetPass.setEnabled(false);

        final TextView sessionFooter = view.findViewById(R.id.account_session_remain_text);

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


        userAccountViewModel.getResetPasswordFormState().observe(getViewLifecycleOwner(), new Observer<UserAccountResetFormState>() {
            @Override
            public void onChanged(UserAccountResetFormState userAccountResetFormState) {
                if (userAccountResetFormState == null) {
                    return;
                }

                if (userAccountResetFormState.isDataValid() && userAccountViewModel.isUserLoggedIn()) {
                    btnResetPass.setEnabled(true);
                } else {
                    btnResetPass.setEnabled(false);
                }

                if (userAccountResetFormState.getPasswordOldError() != null) {
                    fieldOldPass.setError(getString(userAccountResetFormState.getPasswordOldError()));
                }

                if (userAccountResetFormState.getPasswordNewError() != null) {
                    fieldNewPass.setError(getString(userAccountResetFormState.getPasswordNewError()));
                }

                if (userAccountResetFormState.getPasswordVerifyError()!= null) {
                    fieldNewVerifyPass.setError(getString(userAccountResetFormState.getPasswordVerifyError()));
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
                userAccountViewModel.resetPasswordDataChanged(fieldOldPass.getText().toString(),
                        fieldNewPass.getText().toString(), fieldNewVerifyPass.getText().toString());
            }
        };
        fieldOldPass.addTextChangedListener(afterTextChangedListener);
        fieldNewPass.addTextChangedListener(afterTextChangedListener);
        fieldNewVerifyPass.addTextChangedListener(afterTextChangedListener);


        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                btnResetPass.setEnabled(false);
                btnResetClear.setEnabled(false);
                fieldNewPass.setEnabled(false);
                fieldOldPass.setEnabled(false);
                fieldNewVerifyPass.setEnabled(false);
                userAccountViewModel.doPasswordReset(fieldOldPass.getText().toString(),fieldNewPass.getText().toString(), (result) -> {
                    loading.setVisibility(View.GONE);
                    btnResetPass.setEnabled(true);
                    btnResetClear.setEnabled(true);
                    //FIXME: remove this when work
                    if (result instanceof Result.Success) {
                        Boolean success = ((Result.Success<Boolean>) result).getData();
                        if (success) {
                            fieldNewPass.setText("");
                            fieldOldPass.setText("");
                            fieldNewVerifyPass.setText("");
                            snackbar.setText("Success, password changed! Logging out").setTextColor(Color.GREEN);
                            userAccountViewModel.logoutCurrentUser();
                            startActivity(new Intent(getActivity(), WelcomeActivity.class));
                        } else {
                            snackbar.setText("Error, check if current password is correct!").setTextColor(Color.YELLOW);
                        }
                    } else {
                        snackbar.setText("Error, unable to reset password. Try again later!").setTextColor(Color.RED);
                        navController.navigate(R.id.action_nav_account_self);

                    }
                    fieldNewPass.setEnabled(true);
                    fieldOldPass.setEnabled(true);
                    fieldNewVerifyPass.setEnabled(true);
                    snackbar.show();
                });
            }
        });

        btnResetClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fieldNewPass.setText("");
                fieldOldPass.setText("");
                fieldNewVerifyPass.setText("");
                fieldNewPass.setEnabled(true);
                fieldOldPass.setEnabled(true);
                fieldNewVerifyPass.setEnabled(true);
            }
        });
    }

}