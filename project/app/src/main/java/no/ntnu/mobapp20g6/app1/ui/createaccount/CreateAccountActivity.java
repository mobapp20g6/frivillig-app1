package no.ntnu.mobapp20g6.app1.ui.createaccount;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.WelcomeActivity;

public class CreateAccountActivity extends AppCompatActivity {

    private CreateAccountViewModel createAccountViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        //Disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().setTitle(R.string.create_account);
        createAccountViewModel = new ViewModelProvider(this, new CreateAccountViewModelFactory())
                .get(CreateAccountViewModel.class);

        final EditText firstNameText = findViewById(R.id.create_acc_first_name);
        final EditText lastNameText = findViewById(R.id.create_acc_last_name);
        final EditText emailText = findViewById(R.id.create_acc_email);
        final EditText passwordText = findViewById(R.id.create_acc_pwd);
        final EditText passwordVerifyText = findViewById(R.id.create_acc_verify_pwd);

        final Button createButton = findViewById(R.id.create_acc_create_button);
        final Button clearButton = findViewById(R.id.create_acc_clear_button);
        final ProgressBar loading = findViewById(R.id.create_acc_loading);

        createButton.setEnabled(false);

        createAccountViewModel.getCreateFormStateLiveData().observe(this, createFormState -> {
            //Set create button to enabled if all data is valid.
            createButton.setEnabled(createFormState.isDataValid());
            if(createFormState.getFirstNameError() != null) {
                firstNameText.setError(getString(createFormState.getFirstNameError()));
            }
            if(createFormState.getLastNameError() != null) {
                lastNameText.setError(getString(createFormState.getLastNameError()));
            }
            if(createFormState.getEmailError() != null) {
                emailText.setError(getString(createFormState.getEmailError()));
            }
            if(createFormState.getPasswordError() != null) {
                passwordText.setError(getString(createFormState.getPasswordError()));
            }
            if(createFormState.getPasswordVerifyError() != null) {
                passwordVerifyText.setError(getString(createFormState.getPasswordVerifyError()));
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                createAccountViewModel.isFieldsValid(firstNameText.getText().toString(), lastNameText.getText().toString(),
                        emailText.getText().toString(), passwordText.getText().toString(), passwordVerifyText.getText().toString());
            }
        };
        firstNameText.addTextChangedListener(afterTextChangedListener);
        lastNameText.addTextChangedListener(afterTextChangedListener);
        emailText.addTextChangedListener(afterTextChangedListener);
        passwordText.addTextChangedListener(afterTextChangedListener);
        passwordVerifyText.addTextChangedListener(afterTextChangedListener);

        createButton.setOnClickListener(onClick -> {
            loading.setVisibility(View.VISIBLE);
            createButton.setEnabled(false);
            clearButton.setEnabled(false);
            firstNameText.setEnabled(false);
            lastNameText.setEnabled(false);
            emailText.setEnabled(false);
            passwordText.setEnabled(false);
            passwordVerifyText.setEnabled(false);
            createAccountViewModel.createAccount(emailText.getText().toString(), passwordText.getText().toString(),
                    firstNameText.getText().toString(), lastNameText.getText().toString(), result -> {
                loading.setVisibility(View.GONE);
                if(result) {
                    Toast.makeText(this.getBaseContext(), R.string.successfully_made_account, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, WelcomeActivity.class));
                } else {
                    Toast.makeText(this.getBaseContext(), R.string.failed_to_make_account, Toast.LENGTH_LONG).show();
                    createButton.setEnabled(true);
                    clearButton.setEnabled(true);
                    firstNameText.setEnabled(true);
                    lastNameText.setEnabled(true);
                    emailText.setEnabled(true);
                    passwordText.setEnabled(true);
                    passwordVerifyText.setEnabled(true);
                }
                    });
        });

        clearButton.setOnClickListener(onClick -> {
            firstNameText.setText("");
            lastNameText.setText("");
            emailText.setText("");
            passwordText.setText("");
            passwordVerifyText.setText("");
        });
    }
}