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
    private PinView pinView;

    FirebaseAuth auth;

    ActivityEmailOtpBinding binding;
    private Button verifybutt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(EmailOtp.this,R.color.home_scr));

        String email = getIntent().getStringExtra("email");

        auth = FirebaseAuth.getInstance();

        sendVerifyEmail(email);


        binding.userEmail.setText(email);
        pinView = binding.pinFromUser;


        binding.verifyButt.setOnClickListener(v -> {
            String Inputedcode = pinView.getText().toString();
            if(Inputedcode != null){
                Intent intent = new Intent(EmailOtp.this,setNewPassword.class);
                startActivity(intent);
                checkCode(Inputedcode);
            }
            else {
                Toast.makeText(EmailOtp.this, "Enter the OTP!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void closeButt(View view){
        Toast.makeText(this, "Action Prohibited!", Toast.LENGTH_SHORT).show();
    }



    public void sendVerifyEmail(String email){
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EmailOtp.this, "Verification email sent to " + email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EmailOtp.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(EmailOtp.this, "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkCode(String Inputedcode){
        if(Inputedcode.equals(String.valueOf(code))){
            Toast.makeText(this, "Verification Completed!", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    Toast.makeText(EmailOtp.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EmailOtp.this, AftersignUpSupplier.class);
                    startActivity(intent);
                    finish();
                }
            },SPLASH_TIME_OUT);

        }
        else{
            Toast.makeText(this, "OTP Mismatched!", Toast.LENGTH_SHORT).show();
        }
    }


}