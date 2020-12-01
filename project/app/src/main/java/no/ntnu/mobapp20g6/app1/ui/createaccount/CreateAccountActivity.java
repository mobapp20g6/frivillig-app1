package no.ntnu.mobapp20g6.app1.ui.createaccount;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import no.ntnu.mobapp20g6.app1.R;

public class CreateAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getSupportActionBar().setTitle(R.string.create_account);
    }
}