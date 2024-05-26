package com.example.swingmusic;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class FavoriteManager {

    private SongDao songDao;
    private Executor databaseExecutor;

    public FavoriteManager(SongDao songDao, Executor databaseExecutor) {
        this.songDao = songDao;
        this.databaseExecutor = databaseExecutor;
    }

    public void toggleFavorite(MusicFiles currentSong, Context context, FavoriteStateListener listener) {
        String songId = currentSong.getId();

        // Execute database operation in the background
        databaseExecutor.execute(() -> {
            // Get the song from the database
            Song song = songDao.getSongByIdSync(songId); // This method will be synchronous

            boolean isFavorite;

            if (song == null) {
                // Song is not in the database, add it as a favorite
                Song newSong = new Song(songId, currentSong.getPath(), currentSong.getTitle(), currentSong.getArtist(), currentSong.getAlbum(), currentSong.getDuration(), true);
                songDao.insert(newSong);
                isFavorite = true;
            } else {
                // Song is already in the database, toggle the favorite state
                isFavorite = !song.isFavorite();
                song.setFavorite(isFavorite);
                songDao.update(song);
            }

            // Show toast and update UI on the main thread
            boolean finalIsFavorite = isFavorite;
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(context, finalIsFavorite ? "Added to favorites" : "Removed from favorites", Toast.LENGTH_SHORT).show();
                listener.onFavoriteStateChanged(currentSong, finalIsFavorite);
            });
        });
    }

    public void isFavorite(MusicFiles currentSong, FavoriteStateListener listener) {
        databaseExecutor.execute(() -> {
            String songId = currentSong.getId();
            Song song = songDao.getSongByIdSync(songId);
            boolean isFavorite = song != null && song.isFavorite();
            new Handler(Looper.getMainLooper()).post(() -> listener.onFavoriteStateChanged(currentSong, isFavorite));

        });
    }

    public interface FavoriteStateListener {
        void onFavoriteStateChanged(MusicFiles currentSong, boolean isFavorite);
    }
}

