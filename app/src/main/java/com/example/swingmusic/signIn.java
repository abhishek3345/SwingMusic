package com.example.swingmusic;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swingmusic.databinding.ActivitySignInBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;


public class signIn extends AppCompatActivity {
    private Button button;
    private ImageView image;
    private TextView tv_text;

    private Button forgetPass ;

    ActivitySignInBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();


        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(signIn.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Finding your Account!");

        binding.signinbutt.setOnClickListener(v -> {
            progressDialog.show();
            auth.signInWithEmailAndPassword(binding.email1.getText().toString(), binding.password1.getText().toString()).
                    addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(signIn.this, "You have Successfully Signed Into your Account!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(signIn.this, accSupplierScr.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(signIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
//        if(auth.getCurrentUser()!=null){     // In case of detecteing the present user!
//            Intent intent = new Intent(signIn.this,Home.class);
//            startActivity(intent);
//        }

        button = findViewById(R.id.button);
        image = findViewById(R.id.logo_signIn);
        tv_text = findViewById(R.id.logo_name);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivitySignUp();
            }
        });

        forgetPass = binding.forgetpass ;

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signIn.this,ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

//    public void setPassword(View view){
//        FirebaseAuth mauth;
//        mauth= FirebaseAuth.getInstance();
//
//    }

    public void openActivitySignUp(){
        Intent intent = new Intent(signIn.this,signUp.class);
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View,String>(image,"logo_imag");
        pairs[1] = new Pair<View,String>(tv_text,"logo_name");

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){       // Not a necessary condition as its always true
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signIn.this,pairs);
            startActivity(intent,options.toBundle());
        }
    }


}


