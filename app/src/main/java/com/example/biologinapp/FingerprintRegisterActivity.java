package com.example.biologinapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;
import java.util.concurrent.Executor;

public class FingerprintRegisterActivity extends AppCompatActivity {

    EditText nameInput, emailInput, dobInput, addressInput;
    Button registerButton;
    DatabaseReference databaseRef;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.inputName);
        emailInput = findViewById(R.id.inputEmail);
        dobInput = findViewById(R.id.inputDOB);
        addressInput = findViewById(R.id.inputAddress);
        registerButton = findViewById(R.id.registerButton);

        databaseRef = FirebaseDatabase.getInstance().getReference("users");

        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(FingerprintRegisterActivity.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        registerNewUser();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(FingerprintRegisterActivity.this, "Fingerprint failed", Toast.LENGTH_SHORT).show();
                    }
                });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Register with Fingerprint")
                .setSubtitle("Touch sensor to register")
                .setNegativeButtonText("Cancel")
                .build();

        registerButton.setOnClickListener(v -> biometricPrompt.authenticate(promptInfo));
    }

    private void registerNewUser() {
        String uid = "finger_uid_" + UUID.randomUUID().toString().substring(0, 6);
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String dob = dobInput.getText().toString();
        String address = addressInput.getText().toString();

        if (name.isEmpty() || email.isEmpty() || dob.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        UserModel user = new UserModel(name, email, dob, address);
        databaseRef.child(uid).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registered! UID: " + uid, Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}