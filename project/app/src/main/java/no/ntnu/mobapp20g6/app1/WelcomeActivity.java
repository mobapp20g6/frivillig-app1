package no.ntnu.mobapp20g6.app1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import no.ntnu.mobapp20g6.app1.ui.createaccount.CreateAccountActivity;
import no.ntnu.mobapp20g6.app1.ui.login.LoginActivity;

/**
 * @author TrymV
 */
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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