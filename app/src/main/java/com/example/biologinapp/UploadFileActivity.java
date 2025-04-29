package com.example.biologinapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadFileActivity extends AppCompatActivity {

    Button chooseFileBtn, uploadFileBtn;
    Uri fileUri;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);

        chooseFileBtn = findViewById(R.id.chooseFileBtn);
        uploadFileBtn = findViewById(R.id.uploadFileBtn);

        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        chooseFileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            String[] mimeTypes = {"image/*", "video/*", "application/pdf", "application/msword"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(intent, 101);
        });

        uploadFileBtn.setOnClickListener(v -> {
            if (fileUri != null) {
                uploadFile(fileUri);
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            Toast.makeText(this, "File selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile(Uri uri) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading...");
        dialog.show();

        String fileExt = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(getContentResolver().getType(uri));

        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + fileExt);
        fileRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    FirebaseDatabase.getInstance().getReference("files")
                            .push()
                            .setValue(downloadUri.toString());

                    dialog.dismiss();
                    Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
