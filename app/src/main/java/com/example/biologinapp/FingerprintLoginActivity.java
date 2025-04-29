package com.example.biologinapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FingerprintLoginActivity extends AppCompatActivity {

    TextView nameTextView, dobTextView, addressTextView;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_login); // XML file ka naam bhi ye rakh lena

        // TextView find karo
        nameTextView = findViewById(R.id.textName);
        dobTextView = findViewById(R.id.textDOB);
        addressTextView = findViewById(R.id.textAddress);

        // Firebase Realtime DB ka reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Dummy fingerprint ID (ye future me actual fingerprint se milega)
        String fingerprintID = "fingerprint123";

        // Data fetch karna
        databaseReference.child(fingerprintID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String dob = snapshot.child("dob").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);

                    nameTextView.setText("Name: " + name);
                    dobTextView.setText("DOB: " + dob);
                    addressTextView.setText("Address: " + address);
                } else {
                    nameTextView.setText("User not found!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                nameTextView.setText("Failed to read data: " + error.getMessage());
            }
        });
    }
}
