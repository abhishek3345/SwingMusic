package com.example.swingmusic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.swingmusic.databinding.ActivityVerifyOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {
    private ActivityVerifyOtpBinding binding;

    Button verifyBtn ;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String codeBySystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_verify_otp);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(VerifyOTP.this, R.color.home_scr));

        // Get phone number from previous activity
        String phoneNumber = getIntent().getStringExtra("phoneNo");

        String phno = "+91" + phoneNumber ;

        // Set phone number text
        binding.pno.setText(phno);
        verifyBtn = binding.verifyButt ;

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                String code = binding.pinView.getText().toString().trim();
                if (!code.isEmpty()) {
                    verifyCode(code);
                } else {
                    Toast.makeText(VerifyOTP.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Send verification code to user
        sendVerificationCodeToUser(phno);
    }

    public void closeButt(View view) {
        Toast.makeText(this, "Operation Prohibited!", Toast.LENGTH_SHORT).show();
    }

    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNo)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            String code = credential.getSmsCode();
            if (code != null) {
                // Automatically fill OTP code if received
                binding.pinView.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            codeBySystem = verificationId;
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInUsingCredential(credential);
    }

    private void signInUsingCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Verification successful, proceed to next activity
                            Toast.makeText(VerifyOTP.this, "Verification Completed!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(VerifyOTP.this, signIn.class); // Change to your next activity
                            startActivity(intent);
                            finish(); // Finish this activity to prevent going back
                        } else {
                            // Verification failed
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(VerifyOTP.this, "Verification Not Completed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
