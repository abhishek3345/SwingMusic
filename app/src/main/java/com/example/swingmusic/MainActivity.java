package com.example.swingmusic;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    private Button sign_in, sign_up;
    private ImageView img;
    private TextView tvSwing, tvTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sign_in = findViewById(R.id.button1);
        sign_up = findViewById(R.id.button2);
        img = findViewById(R.id.imageView);
        tvSwing = findViewById(R.id.textView);
        tvTag = findViewById(R.id.tagline);

        sign_in.setOnClickListener(v -> openActivitySignIn());
        sign_up.setOnClickListener(v -> openActivitySignUp());



    }
    public void closeActivity(View view){
        Intent intent = new Intent(MainActivity.this, SwingHomePage.class);
        startActivity(intent);
    }

    public void openActivitySignIn(){
        Intent intent = new Intent(MainActivity.this, signIn.class);
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View,String>(img,"logo_imag");
        pairs[1] = new Pair<View,String>(tvSwing,"logo_name");

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
            startActivity(intent,options.toBundle());
        }
    }
    public void openActivitySignUp(){
        Intent intent = new Intent(MainActivity.this,signUp.class);
        Pair[] pairs = new Pair[3];
        pairs[0] = new Pair<View,String>(img,"logo_imag");
        pairs[1] = new Pair<View,String>(tvSwing,"logo_name");
        pairs[2] = new Pair<View,String>(tvTag,"tag_line");

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
            startActivity(intent,options.toBundle());
        }
    }
}