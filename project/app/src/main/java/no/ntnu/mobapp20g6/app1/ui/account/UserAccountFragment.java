package no.ntnu.mobapp20g6.app1.ui.account;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

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

        userAccountViewModel.fetchUserFromServer();

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

        userAccountViewModel.getResetPasswordResult().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean passwordResetSuccess) {
                loading.setVisibility(View.GONE);
                btnResetPass.setEnabled(true);
                btnResetClear.setEnabled(true);
                fieldNewPass.setEnabled(true);
                fieldOldPass.setEnabled(true);
                fieldNewVerifyPass.setEnabled(true);
                //FIXME: remove this when work
                //fieldNewPass.setText("");
                //fieldOldPass.setText("");
                //fieldNewVerifyPass.setText("");
                if (passwordResetSuccess) {
                    snackbar.setText("Success, password has been changed! Logging out").setTextColor(Color.GREEN);
                } else {
                    snackbar.setText("Failed, ensure that the old password is correct!").setTextColor(Color.YELLOW);
                }
                snackbar.show();
            }
        });

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
                userAccountViewModel.doPasswordReset(fieldOldPass.getText().toString(),fieldNewPass.getText().toString());
            }
        });

        btnResetClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fieldNewPass.setText("");
                fieldOldPass.setText("");
                fieldNewVerifyPass.setText("");
            }
        });
    }

}