package com.example.swingmusic;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


public class MusicPlayer extends AppCompatActivity implements View.OnClickListener, FavoriteManager.FavoriteStateListener {

    // Declare views
    private ImageView songThumbnail;
    private TextView songTitle, songArtist, positiveTimer, negativeTimer;
    private SeekBar seekBar;
    private Button repeatButton, previousButton, pauseResumeButton, nextButton, shuffleButton, favouriteButton;

    // Declare MediaPlayer
    private MediaPlayer mediaPlayer;

    private ArrayList<MusicFiles> musicFilesArrayList;

    private boolean isPlaying = false;
    private boolean isShuffleOn = false;
    private boolean isLoopOn = false;
    private int currentPosition = 0;

    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    private SongDatabase songDatabase;
    private SongDao songDao;
    private Executor databaseExecutor;

    private MusicPlayerService musicPlayerService;
    private boolean isBound = false;

    private FavoriteManager favoriteManager;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.MusicPlayerBinder binder = (MusicPlayerService.MusicPlayerBinder) service;
            musicPlayerService = binder.getService();
            isBound = true;

            // Pass the musicFilesArrayList to the service

            ArrayList<MusicFiles> musicFilesArrayList = getIntent().getParcelableArrayListExtra("musicFilesList");
            int currentPosition = getIntent().getIntExtra("songIndex", 0);
            musicPlayerService.setMusicFiles(musicFilesArrayList, currentPosition);


            if (!isPlaying) {
                musicPlayerService.playAudio();
            }
            updateUI(musicFilesArrayList.get(currentPosition));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        getSupportActionBar().hide();

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


        songTitle.setSelected(true);

        // Initialize the SongDatabase and SongDao
        songDatabase = SongDatabaseHelper.getInstance(this);
        songDao = songDatabase.songDao();
        databaseExecutor = DatabaseExecutor.getExecutor();

        // Initialize FavoriteManager
        favoriteManager = new FavoriteManager(songDao, databaseExecutor);

        // Set click listeners
        repeatButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        pauseResumeButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        shuffleButton.setOnClickListener(this);
        favouriteButton.setOnClickListener(this);

        // Initialize media player
        mediaPlayer = new MediaPlayer();

        Intent serviceIntent = new Intent(this, MusicPlayerService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        startService(serviceIntent);

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


        musicFilesArrayList = getIntent().getParcelableArrayListExtra("musicFilesList");
        // Get song path from intent
        String songPath = getIntent().getStringExtra("songPath");
        int position = getIntent().getIntExtra("songIndex", 0);
        currentPosition = position;

        MusicFiles currentSong = musicFilesArrayList.get(currentPosition);

        // Play audio from Firebase Storage URL


        // Play the selected song
        playAudio(currentSong);

        isPlaying = mediaPlayer.isPlaying();

        favoriteManager.isFavorite(currentSong, this);
        updateUI(currentSong);

        registerReceiver(progressReceiver, new IntentFilter("PROGRESS_UPDATE"));
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
    private void playAudio(MusicFiles currentSong) {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    // Release the current MediaPlayer
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();

            // Update UI
            updateUI(currentSong);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
        }
    }

    // Update UI method
    private void updateUI(MusicFiles currentSong) {
        //Update songTitle , artist and thumbnail
        String title = currentSong.getTitle();
        String artist = currentSong.getArtist();

        songTitle.setText(title);
        songArtist.setText(artist);
        songThumbnail.setImageResource(R.drawable.default_thumbnail);

        favoriteManager.isFavorite(currentSong, this);

        if (isBound && musicPlayerService != null) {
            musicPlayerService.updateNotification();
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

        MusicFiles currentSong = musicFilesArrayList.get(currentPosition);
        playAudio(currentSong);
        updateUI(currentSong);
    }

    // Play previous song method
    private void playPreviousSong() {
        if (currentPosition > 0) {
            currentPosition--;
        } else {
            // Notify user that it's the first song
            Toast.makeText(this, "You're already at the beginning of the playlist", Toast.LENGTH_SHORT).show();
        }

        MusicFiles currentSong = musicFilesArrayList.get(currentPosition);
        playAudio(currentSong);
        updateUI(currentSong);
    }

    @Override
    public void onClick(View v) {
        MusicFiles currentSong = musicFilesArrayList.get(currentPosition);
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
                updateUI(currentSong);
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
                break;
            case R.id.button_favourite:
                favoriteManager.toggleFavorite(currentSong, this, this);
                break;
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
        playAudio(musicFilesArrayList.get(currentPosition));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateSeekBarAndTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSeekBarAndTime();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }

        mHandler.removeCallbacks(mRunnable);
        //Release media player resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onFavoriteStateChanged(MusicFiles currentSong, boolean isFavorite) {

        favoriteManager.isFavorite(currentSong, this);

        if (isFavorite) {
            favouriteButton.setBackgroundResource(R.drawable.favorite);
            favouriteButton.setBackgroundTintList(getColorStateList(R.color.red));
        } else {
            favouriteButton.setBackgroundResource(R.drawable.favorite_off);
            favouriteButton.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
        }
    }

    private final BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int currentPosition = intent.getIntExtra("currentPosition", 0);
            int duration = intent.getIntExtra("duration", 0);

            // Update playback timer
            positiveTimer.setText(formatTime(currentPosition));
            negativeTimer.setText(formatTime(duration - currentPosition));

            // Update seek bar progress
            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);

            // Update pause/resume button icon
            isPlaying = intent.getBooleanExtra("isPlaying", false);
            if (isPlaying) {
                pauseResumeButton.setBackgroundResource(R.drawable.pause);
            } else {
                pauseResumeButton.setBackgroundResource(R.drawable.play);
            }
        }
    };
}
