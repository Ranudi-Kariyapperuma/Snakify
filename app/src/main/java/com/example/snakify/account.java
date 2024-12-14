package com.example.snakify;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class account extends AppCompatActivity {

    private EditText nameField, phoneField, emailField;
    private Button submitButton, updateButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_account);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Reference to UI components
        nameField = findViewById(R.id.nameField);
        phoneField = findViewById(R.id.phoneField);
        emailField = findViewById(R.id.emailField);
        submitButton = findViewById(R.id.submitButton);
        updateButton = findViewById(R.id.updateButton);

        // Save data to Firestore
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });

        // Update data in Firestore
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
            }
        });
    }

    private void saveUserData() {
        // Get user inputs
        String name = nameField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String email = emailField.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid(); // Get current user's UID

        // Create a map of user data
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("phone", phone);
        user.put("email", email);

        // Save user data to Firestore
        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(account.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(account.this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error: ", e);
                    }
                });
    }

    private void updateUserData() {
        // Get user inputs
        String name = nameField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String email = emailField.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid(); // Get current user's UID

        // Update user data in Firestore
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.update("name", name, "phone", phone, "email", email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(account.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(account.this, "Error updating data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error: ", e);
                    }
                });
    }
}
