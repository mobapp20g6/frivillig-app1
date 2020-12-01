package no.ntnu.mobapp20g6.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import no.ntnu.mobapp20g6.app1.ui.createaccount.CreateAccountActivity;
import no.ntnu.mobapp20g6.app1.ui.login.LoginActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        Button loginButton = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(onClick->{
            startActivity(new Intent(this, CreateAccountActivity.class));
        });
        loginButton.setOnClickListener(onClick->{
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}