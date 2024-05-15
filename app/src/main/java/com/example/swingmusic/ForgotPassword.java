package com.example.swingmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swingmusic.databinding.ActivityForgotPasswordBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPassword extends AppCompatActivity {

    private ImageView backIcon, forgotPassIcon;
    private TextView forgotPassTitle, forgotPassDesc;
    private TextInputLayout emailTextInputLayout;
    private TextInputEditText emailEditText;
    private Button nextButton;
    ActivityForgotPasswordBinding binding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

// Access the views using binding
        backIcon = binding.backIcon;
        forgotPassIcon = binding.forgotPassIcon;
        forgotPassTitle = binding.forgotPassTitle;
        forgotPassDesc = binding.forgotPassDesc;
        emailTextInputLayout = binding.emailTextInputLayout;
        emailEditText = binding.emailEditText;
        nextButton = binding.nextButton;


        // Set click listener for back button
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set click listener for next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform next action, such as sending reset password email
                String email = emailEditText.getText().toString().trim();
                if (!isValidEmail(email)) {
                    emailTextInputLayout.setError("Invalid email");
                } else {
                    // Proceed with sending reset password email
                    Intent intent = new Intent(ForgotPassword.this, EmailOtp.class);
                    intent.putExtra("email", email);
                    Toast.makeText(ForgotPassword.this, "Reset password email sent to " + email, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }
        });
    }

    // Function to validate email address
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
