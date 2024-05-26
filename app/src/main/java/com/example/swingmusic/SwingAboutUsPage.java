package com.example.swingmusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;

public class SwingAboutUsPage extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swing_about_us_page);

        getSupportActionBar().hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(SwingAboutUsPage.this,R.color.home_scr));

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id._about_us);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id._home:
                        startActivity(new Intent(getApplicationContext(), SwingHomePage.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id._library:
                        startActivity(new Intent(getApplicationContext(),SwingLibraryPage.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id._account:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id._about_us:
                        return true;
                }
                return false;
            }
        });

        Button linkedinButton = findViewById(R.id.btn_linkedin);
        Button githubButton = findViewById(R.id.btn_github);
        Button gmailButton = findViewById(R.id.btn_gmail);

        // Set click listeners for the buttons
        linkedinButton.setOnClickListener(v -> openLinkedIn());
        githubButton.setOnClickListener(v -> openGitHub());
        gmailButton.setOnClickListener(v -> openGmail());

    }



    private void openLinkedIn() {
        String linkedInUrl = "https://www.linkedin.com/in/abhishek-pawar-052578218";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedInUrl));

            startActivity(intent);

    }

    private void openGitHub() {
        String githubUrl = "https://github.com/abhishek3345";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));

            startActivity(intent);
    }

    private void openGmail() {
        String gmailUrl = "mailto:2020bcs007@sggs.ac.in";
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(gmailUrl));
            startActivity(intent);

    }
}