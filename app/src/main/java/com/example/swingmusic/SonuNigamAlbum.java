package com.example.swingmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SonuNigamAlbum extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sonu_nigam_album);
    }
}