package com.example.nutritrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LogOutActivity extends AppCompatActivity {

    private Button logoutButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        firebaseAuth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOutUser();
            }
        });
    }

    private void logOutUser() {
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.signOut();
            Toast.makeText(LogOutActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LogOutActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(LogOutActivity.this, "No user is currently logged in", Toast.LENGTH_SHORT).show();
        }
    }
}