package com.example.biologinapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class UserProfileActivity extends AppCompatActivity {

    TextView nameView, emailView, dobView, addressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        nameView = findViewById(R.id.nameView);
        emailView = findViewById(R.id.emailView);
        dobView = findViewById(R.id.dobView);
        addressView = findViewById(R.id.addressView);

        // Get data from intent
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String dob = getIntent().getStringExtra("dob");
        String address = getIntent().getStringExtra("address");

        nameView.setText("Name: " + name);
        emailView.setText("Email: " + email);
        dobView.setText("DOB: " + dob);
        addressView.setText("Address: " + address);
    }
}

