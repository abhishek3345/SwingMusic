package com.example.swingmusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.swingmusic.databinding.ActivityEmailOtpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailOtp extends AppCompatActivity {
    int code;
    private static int SPLASH_TIME_OUT = 2000;
    private TextView userEmail;

   Button closeBtn ;
    FirebaseAuth auth;

    ActivityEmailOtpBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(EmailOtp.this, R.color.home_scr));


        String email = getIntent().getStringExtra("email");
        binding.userEmail.setText(email);

        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EmailOtp.this, "Action Prohibited!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EmailOtp.this, ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });

    }

}