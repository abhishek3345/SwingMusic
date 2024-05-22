package com.example.swingmusic;


import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MusicPlayer extends AppCompatActivity implements View.OnClickListener {

    // Declare views
    private ImageView songThumbnail;
    private TextView songTitle, songArtist, positiveTimer, negativeTimer;
    private SeekBar seekBar;
    private Button repeatButton, previousButton, pauseResumeButton, nextButton, shuffleButton, favouriteButton;

    private SharedPreferences sharedPreferences;
    private Set<String> favoriteSongIds;

    // Declare MediaPlayer
    private MediaPlayer mediaPlayer;

    private ArrayList<MusicFiles> musicFilesArrayList;

    // Declare variables
    private boolean isPlaying = false;
    private boolean isShuffleOn = false;
    private boolean isLoopOn = false;
    private int currentPosition = 0 ;



    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);


        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("FavoriteSongs", Context.MODE_PRIVATE);
        favoriteSongIds = sharedPreferences.getStringSet("FavoriteSongIds", new HashSet<>());

        // Initialize views
        songThumbnail = findViewById(R.id.image_thumbnail);
        songTitle = findViewById(R.id.song_title);
        songArtist = findViewById(R.id.song_artist);
        positiveTimer = findViewById(R.id.positive_playback_timer);
        negativeTimer = findViewById(R.id.negative_playback_timer);
        seekBar = findViewById(R.id.seekBar);
        repeatButton = findViewById(R.id.reapet_button);
        previousButton = findViewById(R.id.previousButton);
        pauseResumeButton = findViewById(R.id.pauseResumeButton);
        nextButton = findViewById(R.id.nextButton);
        shuffleButton = findViewById(R.id.shuffleButton);
        favouriteButton = findViewById(R.id.button_favourite);


        musicFilesArrayList = getIntent().getParcelableArrayListExtra("musicFilesList");

        songTitle.setSelected(true);


        // Set click listeners
        repeatButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        pauseResumeButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        shuffleButton.setOnClickListener(this);
        favouriteButton.setOnClickListener(this);



        // Initialize media player
        mediaPlayer = new MediaPlayer();
        updateSeekBarAndTime();



        // Set up media player listeners
        mediaPlayer.setOnCompletionListener(mp -> {
            if (isLoopOn) {
                // Loop playback
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            } else {
                // Play next song
                playNextSong();
            }
        });

        // Set up seek bar change listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Seek to the selected position
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Pause playback while seeking
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Resume playback after seeking
                mediaPlayer.start();
            }
        });


        // Get song path from intent
        String songPath = getIntent().getStringExtra("songPath");

        // Play the selected song
        playAudio(songPath);

        isPlaying = mediaPlayer.isPlaying();

        updateUI();


    }

    private void updateSeekBarAndTime() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int duration = mediaPlayer.getDuration();
                    int currentPosition = mediaPlayer.getCurrentPosition();

                    // Update playback timer
                    positiveTimer.setText(formatTime(currentPosition));
                    negativeTimer.setText(formatTime(duration - currentPosition));

                    // Update seek bar progress
                    seekBar.setMax(duration);
                    seekBar.setProgress(currentPosition);
                }

                // Schedule the next update in 500 milliseconds
                mHandler.postDelayed(this, 500);
            }
        };

        // Start the initial update
        mHandler.post(mRunnable);

    }


    // Play audio method
    private void playAudio(String path) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();


            // Update UI
            updateUI();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
        }
    }

    // Update UI method
    private void updateUI() {
        //Update songTitle , artist and thumbnail
        if (currentPosition >= 0 && currentPosition < musicFilesArrayList.size()) {
            String title = musicFilesArrayList.get(currentPosition).getTitle();
            String artist = musicFilesArrayList.get(currentPosition).getArtist();

            songTitle.setText(title);
            songArtist.setText(artist);
            songThumbnail.setImageResource(R.drawable.default_thumbnail);

            updateFavoriteButtonUI(currentPosition);
        }

        // Update playback timer
        int duration = mediaPlayer.getDuration();
        int currentPosition = mediaPlayer.getCurrentPosition();
        positiveTimer.setText(formatTime(currentPosition));
        negativeTimer.setText(formatTime(duration - currentPosition));

        // Update seek bar progress
        seekBar.setMax(duration);
        seekBar.setProgress(currentPosition);

        // Update pause/resume button icon
        if (isPlaying) {
            pauseResumeButton.setBackgroundResource(R.drawable.pause);
        } else {
            pauseResumeButton.setBackgroundResource(R.drawable.play);
        }
    }

    // Format time method (convert milliseconds to mm:ss format)
    private String formatTime(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(minutes);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    // Play next song method
    private void playNextSong() {
        if (currentPosition < musicFilesArrayList.size() - 1) {
            currentPosition++;

        } else {
            // Notify user that it's the last song
            Toast.makeText(this, "You've reached the end of the playlist", Toast.LENGTH_SHORT).show();
        }
        String nextSongPath = musicFilesArrayList.get(currentPosition).getPath();
        playAudio(nextSongPath);
    }

    // Play previous song method
    private void playPreviousSong() {
        // Implement logic to play the previous song
        if (currentPosition > 0) {
            currentPosition--;

        } else {
            // Notify user that it's the first song
            Toast.makeText(this, "You're already at the beginning of the playlist", Toast.LENGTH_SHORT).show();
        }
        String previousSongPath = musicFilesArrayList.get(currentPosition).getPath();
        playAudio(previousSongPath);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reapet_button:
                // Handle repeat button click
                isLoopOn = !isLoopOn;
                updateLoopButton();
                Toast.makeText(this, "Loop mode changed", Toast.LENGTH_SHORT).show();
                break;

            case R.id.previousButton:
                playPreviousSong();
                break;
            case R.id.pauseResumeButton:
                if (isPlaying) {
                    // Pause playback
                    mediaPlayer.pause();
                    isPlaying = false;
                } else {
                    // Resume playback
                    mediaPlayer.start();
                    isPlaying = true;
                }
                // Update UI
                updateUI();
                break;
            case R.id.nextButton:
                playNextSong();
                break;
            case R.id.shuffleButton:
                isShuffleOn = !isShuffleOn;
                Toast.makeText(this, "Playback is shuffled", Toast.LENGTH_SHORT).show();

                shuffleButton.setBackgroundResource(isShuffleOn ? R.drawable.shuffle_on : R.drawable.shuffle_off);
                shuffleSongs();
                // Implement shuffle logic here
                // Handle shuffle button click
                break;
            case R.id.button_favourite:
                // Handle favourite button click
                toggleFavorite(currentPosition);
                break;
        }
    }


    private void toggleFavorite(int position) {
        MusicFiles currentSong = musicFilesArrayList.get(position);
        String currentSongId = currentSong.getId();

        if (favoriteSongIds.contains(currentSongId)) {
            // Remove from favorites
            favoriteSongIds.remove(currentSongId);
            currentSong.setFavorite(false);
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            // Add to favorites
            favoriteSongIds.add(currentSongId);
            currentSong.setFavorite(true);
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        }

        // Save the favorite song IDs to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("FavoriteSongIds", favoriteSongIds);
        editor.apply();

        // Notify the FavoritesFragment to update the UI
        updateFavoritesFragment();

        // Update the favorite button UI
        updateFavoriteButtonUI(position);
    }

    private void updateFavoritesFragment() {
        FavouritesFragment favouritesFragment = (FavouritesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_favourites);
        if (favouritesFragment != null) {
            favouritesFragment.updateFavorites();
        }
    }





    // Update favorite status in Firebase Database for the current user
    private void updateFavoriteButtonUI(int position) {
        MusicFiles currentSong = musicFilesArrayList.get(position);
        boolean isFavorite = favoriteSongIds.contains(currentSong.getId());

        // Update the favorite button UI based on the favorite status
        if (isFavorite) {
            favouriteButton.setBackgroundResource(R.drawable.favorite);
            favouriteButton.setBackgroundTintList(getColorStateList(R.color.red));
        } else {
            favouriteButton.setBackgroundResource(R.drawable.favorite_off);
            favouriteButton.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
        }
    }

    private void updateLoopButton() {
        int loopButtonDrawable;
        if (isLoopOn) {
            // Loop on mode
            loopButtonDrawable = R.drawable.repeat_on;
        } else {
            // Loop off mode
            loopButtonDrawable = R.drawable.repeat_off;
        }
        repeatButton.setBackgroundResource(loopButtonDrawable);
    }


    private void shuffleSongs() {
        // Shuffle the musicFilesArrayList
        Collections.shuffle(musicFilesArrayList);

        // Reset currentPosition to start from the beginning after shuffling
        currentPosition = 0;

        // Play the first song after shuffling
        playAudio(musicFilesArrayList.get(currentPosition).getPath());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
        // Release media player resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

