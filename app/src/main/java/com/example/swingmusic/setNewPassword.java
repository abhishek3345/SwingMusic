package com.example.swingmusic;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swingmusic.databinding.ActivitySetNewPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class setNewPassword extends AppCompatActivity {

    private ActivitySetNewPasswordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetNewPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set click listener for back icon
        binding.backIconSetPassword.setOnClickListener(v -> onBackPressed());

        // Set click listener for reset button
        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = binding.newPasswordEditText.getText().toString().trim();
                String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();

                if (newPassword.equals(confirmPassword)) {
                    // Passwords match, proceed with reset
                    resetPassword(newPassword);
                } else {
                    // Passwords don't match, show error
                    Toast.makeText(setNewPassword.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetPassword(String newPassword) {
        mAuth.getCurrentUser().updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(setNewPassword.this, "Password reset successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(setNewPassword.this, "Failed to reset password. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
