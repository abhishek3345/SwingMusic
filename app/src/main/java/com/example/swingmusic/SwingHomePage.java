package com.example.swingmusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SwingHomePage extends AppCompatActivity  {

    BottomNavigationView bottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swing_home_page);

        getSupportActionBar().hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(SwingHomePage.this,R.color.black));

        //Vertical Navigation Bar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_lay);
        NavigationView navigationView = findViewById(R.id.ver_nav_view); // This is one is the side bar!

        View headerView = navigationView.getHeaderView(0);

        TextView nameTextView = headerView.findViewById(R.id.Acname);
        TextView usernameTextView = headerView.findViewById(R.id.username);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        if(currentUser != null) {
            String currentUserId = currentUser.getUid();
            userRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve name and username from the dataSnapshot
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String username = dataSnapshot.child("username").getValue(String.class);

                        // Set the name and username to TextViews
                        nameTextView.setText(name);
                        usernameTextView.setText(username);
                    }
                }

                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        } else {
            nameTextView.setText(R.string.user);
            usernameTextView.setText(R.string.register);
        }

        MenuItem logoutMenuItem = navigationView.getMenu().findItem(R.id.logout);

        logoutMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                FirebaseAuth.getInstance().signOut();
                nameTextView.setText(R.string.user);
                usernameTextView.setText(R.string.register);
                Toast.makeText(SwingHomePage.this,"User signed out successfully!",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SwingHomePage.this,MainActivity.class);
                startActivity(intent);
                return true;
            }
        });


        MenuItem rateUsMenuItem = navigationView.getMenu().findItem(R.id.nav_rateus);

        rateUsMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {

                Toast.makeText(SwingHomePage.this, "Open with Gmail!", Toast.LENGTH_LONG).show();
                String[] recipient = {"2020bcs007@sggs.ac.in"};
                String subject = "User Rating of your SwingMusic app";
                String message = "Give us the rating between 1 to 5\n\n\n" +
                        "Thanks for your Rating!\n" +
                        "Team SwingMusic";

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, recipient);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, message);

                startActivity(emailIntent);

                return true;
            }
        });

        MenuItem feedbackMenuItem = navigationView.getMenu().findItem(R.id.nav_feedback);
        feedbackMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                // Define the recipient, subject, and message for the email
                Toast.makeText(SwingHomePage.this, "Open with Gmail!", Toast.LENGTH_LONG).show();
                String[] recipient = {"2020bcs007@sggs.ac.in"};
                String subject = "User Feedback of your SwingMusic app";
                String message =  "Thanks for your time, we are glad to hear from you!\n\n" +
                        "Please share your feedback below:\n\n" +
                        "<!--Your feedback here--!>\n\n" +
                        "Thanks for your feedback!\n" +
                        "We are working hard to serve you!";

                // Create an intent to send an email
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, recipient);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, message);

                // Ensure there is an email app to handle the intent

                startActivity(emailIntent);

                return true;
            }

        });



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id){
                    case R.id.nav_home:
                        Toast.makeText(SwingHomePage.this, "Home is aldready open!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_library:
                        Intent intent = new Intent(SwingHomePage.this, SwingLibraryPage.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_account:
                        Intent intent1 = new Intent(SwingHomePage.this, MainActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_settings:
                        Toast.makeText(SwingHomePage.this, "settings is under work!", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_aboutus:
                        Intent intent2 = new Intent(SwingHomePage.this,SwingAboutUsPage.class);
                        startActivity(intent2);
                        break;
                    case R.id.nav_share:
                        String githubProfileUrl = "https://github.com/abhishek3345/";
                        String shareMessage = "Hey! check out this SwingMusic app for listening songs. Download from this link: " + githubProfileUrl;
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        startActivity(Intent.createChooser(shareIntent, "Share via"));
                        break;

                }
                return true;
            }
        });


        //Bottom Navigation Bar
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id._home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id._home:
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
                        startActivity(new Intent(getApplicationContext(), SwingAboutUsPage.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        LinearLayout layout = findViewById(R.id.arijit_singh_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_arijit = new Intent(SwingHomePage.this,ArijitSinghAlbum.class);
                startActivity(intent_arijit);
            }
        });

        LinearLayout layout1 = findViewById(R.id.zyan_layout);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_zyan = new Intent(SwingHomePage.this, SonuNigamAlbum.class);
                startActivity(intent_zyan);
            }
        });
    }

}