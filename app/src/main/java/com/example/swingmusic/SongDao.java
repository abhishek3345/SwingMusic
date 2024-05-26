package com.example.swingmusic;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SongDao {
    @Insert
    void insert(Song song);

    @Delete
    void delete(Song song);

    @Query("SELECT * FROM songs WHERE isFavorite = 1")
    LiveData<List<Song>>getFavoriteSongs();


    @Query("SELECT * FROM songs WHERE id = :songId LIMIT 1")
    Song getSongByIdSync(String songId); // Synchronous method

    @Query("SELECT * FROM songs WHERE id = :songId LIMIT 1")
    Song getSongById(String songId); // Synchronous method

    @Query("SELECT isFavorite FROM songs WHERE id = :songId LIMIT 1")
    boolean isSongFavorite(String songId);

    @Update
    void update(Song song);
    // Add more methods as needed

}

