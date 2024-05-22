package com.example.swingmusic;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swingmusic.databinding.ActivitySignUpBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class signUp extends AppCompatActivity {

    private Button button1, button2;
    private ImageView img;
    private TextView tv_text1, tvDesc;
    TextInputLayout mat1, mat2, mat3, mat4, mat5;


    FirebaseDatabase database;
    ProgressDialog progressDialog;

    ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(signUp.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account. Please Wait!");

        button2 = findViewById(R.id.butt);
        img = findViewById(R.id.logo_signUp);
        tv_text1 = findViewById(R.id.tv_text1);
        tvDesc = findViewById(R.id.desc);

        mat1 = findViewById(R.id.mat1);
        mat2 = findViewById(R.id.mat2);
        mat3 = findViewById(R.id.mat3);
        mat4 = findViewById(R.id.mat4);
        mat5 = findViewById(R.id.mat5);
        button1 = findViewById(R.id.signupbutt);

        binding.signupbutt.setOnClickListener(v -> {

            if(!validateName() | !validatePassword() | !validateEmail() | !validatePhoneNo() | !validateUsername()){
                return;
            }
            else{
                progressDialog.show();
                mAuth.createUserWithEmailAndPassword(binding.regEmail.getText().toString(), binding.regPassword.getText().toString()).
                        addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                userHelperClass helperClass = new userHelperClass(binding.regFullName.getText().toString(),binding.regUsername.getText().toString(), binding.regEmail.getText().toString(),
                                        binding.regPhoneNo.getText().toString(), binding.regPassword.getText().toString());
                                String id = task.getResult().getUser().getUid();
                                database.getReference().child("Users").child(id).setValue(helperClass);

                                callVerifyOTPScreen();
                            }
                            else{
                                Toast.makeText(signUp.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        button2.setOnClickListener(v -> openActivitySignIn());
    }



    public void openActivitySignIn(){
        Intent intent = new Intent(signUp.this,signIn.class);
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View,String>(img,"logo_imag");
        pairs[1] = new Pair<View,String>(tv_text1,"logo_name");


        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signUp.this,pairs);
        startActivity(intent,options.toBundle());
    }


    private Boolean validateName(){
        String val = mat1.getEditText().getText().toString();
        if(val.isEmpty()){
            mat1.setError("Field cannot be empty");
            return false;
        }
        else{
            mat1.setError(null);
            mat1.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateUsername(){
        String val = mat2.getEditText().getText().toString();
        if(val.isEmpty()){
            mat2.setError("Field cannot be empty");
            return false;
        }
        else if(val.length()>=15){
            mat2.setError("Username too long");
            return false;
        }
        else{
            mat2.setError(null);
            mat2.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail(){
        String val = mat3.getEditText().getText().toString();
        if(val.isEmpty()){
            mat3.setError("Field cannot be empty");
            return false;
        }
        else{
            mat3.setError(null);
            mat3.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePhoneNo(){
        String val = mat4.getEditText().getText().toString();
        if(val.isEmpty()){
            mat4.setError("Field cannot be empty");
            return false;
        }
        else if(val.length() != 10){
            mat4.setError("Enter valid Phone no");
            return false;
        }
        else{
            mat4.setError(null);
            mat4.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword(){
        String val = mat5.getEditText().getText().toString();
        if(val.isEmpty()){
            mat5.setError("Field cannot be empty");
            return false;
        }
        else if(val.length()<6){
            mat2.setError("Password too short");
            return false;
        }
        else{
            mat5.setError(null);
            mat5.setErrorEnabled(false);
            return true;
        }
    }

    public void callVerifyOTPScreen() {
        if(!validatePhoneNo())
            return;

        String phoneNumber = mat4.getEditText().getText().toString().trim();
        Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);
        intent.putExtra("phoneNo", phoneNumber);
        startActivity(intent);
    }


}
