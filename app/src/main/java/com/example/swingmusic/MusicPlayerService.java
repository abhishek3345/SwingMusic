package com.example.swingmusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

import java.util.ArrayList;

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private MediaPlayer mediaPlayer;
    private ArrayList<MusicFiles> musicFilesArrayList;
    private int currentPosition;
    private boolean isPlaying;

    private final IBinder binder = new MusicPlayerBinder();

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "MusicPlayerChannel";

    public static final String ACTION_PLAY = "com.example.swingmusic.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.swingmusic.ACTION_PAUSE";
    public static final String ACTION_NEXT = "com.example.swingmusic.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "com.example.swingmusic.ACTION_PREVIOUS";

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateNotificationRunnable;

    @Override
    public void onCreate() {
        super.onCreate();


        //Release media player resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setWakeMode(getApplicationContext(), android.os.PowerManager.PARTIAL_WAKE_LOCK);
        createNotificationChannel();
    }

    private void createUpdateNotificationRunnable() {
        updateNotificationRunnable = new Runnable() {
            @Override
            public void run() {
                updateNotification(); // Update notification content
                handler.postDelayed(this, 500); // Repeat every 500ms
            }
        };
    }

    public void setMusicFiles(ArrayList<MusicFiles> musicFiles, int position) {
        musicFilesArrayList = musicFiles;
        currentPosition = position;
        playCurrentSong();
    }

    private void playCurrentSong() {
        String songPath = musicFilesArrayList.get(currentPosition).getPath();
        try {
            mediaPlayer.release();
            mediaPlayer.reset();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(songPath);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playAudio() {
        if (!isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
            updateNotification();
            startForeground(NOTIFICATION_ID, getNotification());
        }
    }

    public void pauseAudio() {
        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            updateNotification();
            stopForeground(false); // Keep the notification after stopping foreground
            handler.removeCallbacks(updateNotificationRunnable);
        }
    }

    public void playNextSong() {
        if (currentPosition < musicFilesArrayList.size() - 1) {
            currentPosition++;
        } else {
            currentPosition = 0;
        }
        playCurrentSong();
        updateNotification();
    }

    public void playPreviousSong() {
        if (currentPosition > 0) {
            currentPosition--;
        } else {
            currentPosition = musicFilesArrayList.size() - 1;
        }
        playCurrentSong();
        updateNotification();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        isPlaying = true;
        updateNotification();
        startForeground(NOTIFICATION_ID, getNotification());
        handler.postDelayed(updateNotificationRunnable, 500); // Start updating
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // Handle media player errors
        return false;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Music Player", NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void updateNotification() {
        String songTitle = getCurrentSongTitle();
        String artistName = getCurrentArtistName();
        Bitmap albumArt = getCurrentAlbumArt();
        // Get the current playback position from mediaPlayer.getCurrentPosition()
        int currentPosition = mediaPlayer.getCurrentPosition();
        String elapsedTime = formatTime(currentPosition);

        PendingIntent pauseIntent = getPendingIntent(ACTION_PAUSE);
        PendingIntent playIntent = getPendingIntent(ACTION_PLAY);
        PendingIntent nextIntent = getPendingIntent(ACTION_NEXT);
        PendingIntent previousIntent = getPendingIntent(ACTION_PREVIOUS);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(songTitle)
                .setContentText(artistName)
                .setSmallIcon(R.drawable.app_logo_black)
                .setLargeIcon(albumArt)
                .addAction(R.drawable.previous, "Previous", previousIntent)
                .addAction(isPlaying ? R.drawable.pause : R.drawable.play, isPlaying ? "Pause" : "Play", isPlaying ? pauseIntent : playIntent)
                .addAction(R.drawable.next, "Next", nextIntent)
                .setStyle(new MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2) // Show all actions in compact view
                        .setMediaSession(null) // Set the media session if you have one
                )
                .setContentText(elapsedTime) // Add the current playback time
                .setOngoing(isPlaying)
                .setOnlyAlertOnce(true) // Prevents the notification from making sound again
                .build();

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private Notification getNotification() {
        String songTitle = getCurrentSongTitle();
        String artistName = getCurrentArtistName();
        Bitmap albumArt = getCurrentAlbumArt();

        PendingIntent pauseIntent = getPendingIntent(ACTION_PAUSE);
        PendingIntent playIntent = getPendingIntent(ACTION_PLAY);
        PendingIntent nextIntent = getPendingIntent(ACTION_NEXT);
        PendingIntent previousIntent = getPendingIntent(ACTION_PREVIOUS);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(songTitle)
                .setContentText(artistName)
                .setSmallIcon(R.drawable.app_logo_black)
                .setLargeIcon(albumArt)
                .addAction(R.drawable.previous, "Previous", previousIntent)
                .addAction(isPlaying ? R.drawable.pause : R.drawable.play, isPlaying ? "Pause" : "Play", isPlaying ? pauseIntent : playIntent)
                .addAction(R.drawable.next, "Next", nextIntent)
                .setStyle(new MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2) // Show all actions in compact view
                        .setMediaSession(null) // Set the media session if you have one
                )
                .setOngoing(isPlaying)
                .setOnlyAlertOnce(true) // Prevents the notification from making sound again
                .build();
    }

    private PendingIntent getPendingIntent(String action) {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction(action);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private String getCurrentSongTitle() {
        return musicFilesArrayList.get(currentPosition).getTitle();
    }

    private String getCurrentArtistName() {
        return musicFilesArrayList.get(currentPosition).getArtist();
    }

    private Bitmap getCurrentAlbumArt() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.default_thumbnail);
    }

    private String formatTime(int milliseconds) {
        long minutes = milliseconds / 1000 / 60;
        long seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public class MusicPlayerBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_PLAY.equals(action)) {
                playAudio();
            } else if (ACTION_PAUSE.equals(action)) {
                pauseAudio();
            } else if (ACTION_NEXT.equals(action)) {
                // Stop the current playback and release the MediaPlayer
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null; // Reset to null
                }
                playNextSong(); // Play the next song
            } else if (ACTION_PREVIOUS.equals(action)) {
                // Stop the current playback and release the MediaPlayer
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null; // Reset to null
                }
                playPreviousSong(); // Play the previous song
            }
        }
        return START_STICKY; // Keep service running even after being stopped
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateNotificationRunnable);
    }
}