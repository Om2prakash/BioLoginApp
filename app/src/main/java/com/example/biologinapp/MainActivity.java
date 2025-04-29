package com.example.biologinapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.*;

import java.util.UUID;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    TextView statusText;
    Button authButton;
    Button openFingerprintActivityButton;
    DatabaseReference databaseRef, fingerprintMapRef;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    Handler logoutHandler = new Handler();
    Runnable logoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        authButton = findViewById(R.id.authButton);
        openFingerprintActivityButton = findViewById(R.id.openFingerprintActivityButton);

        Button registerUserButton = findViewById(R.id.registerUserButton);
        registerUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FingerprintRegisterActivity.class);
            startActivity(intent);
        });

        databaseRef = FirebaseDatabase.getInstance().getReference("users");
        fingerprintMapRef = FirebaseDatabase.getInstance().getReference("fingerprints");

        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);

                        // 🔐 Simulate scanned fingerprint ID (in real app use fingerprint device)
                        String currentFingerprintID = "fp_dummy_id"; // Replace with actual scanned ID if using hardware

                        fingerprintMapRef.child(currentFingerprintID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String uid = snapshot.getValue(String.class);

                                    databaseRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            String name = snapshot.child("name").getValue(String.class);
                                            String email = snapshot.child("email").getValue(String.class);
                                            String dob = snapshot.child("dob").getValue(String.class);
                                            String address = snapshot.child("address").getValue(String.class);

                                            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                                            intent.putExtra("name", name);
                                            intent.putExtra("email", email);
                                            intent.putExtra("dob", dob);
                                            intent.putExtra("address", address);
                                            startActivity(intent);

                                            startLogoutTimer();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            statusText.setText("Error fetching user: " + error.getMessage());
                                        }
                                    });
                                } else {
                                    statusText.setText("Fingerprint not registered");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                statusText.setText("Error reading fingerprint map: " + error.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        statusText.setText("Fingerprint authentication failed");
                    }
                });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login with Fingerprint")
                .setSubtitle("Scan your finger to continue")
                .setNegativeButtonText("Cancel")
                .build();

        authButton.setOnClickListener(v -> biometricPrompt.authenticate(promptInfo));

        openFingerprintActivityButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FingerprintLoginActivity.class);
            startActivity(intent);
        });
    }

    private void startLogoutTimer() {
        logoutRunnable = () -> {
            statusText.setText("Auto logout: Session expired");
        };
        logoutHandler.postDelayed(logoutRunnable, 120000); // 2 minutes
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logoutRunnable != null) {
            logoutHandler.removeCallbacks(logoutRunnable);
        }
    }
}
