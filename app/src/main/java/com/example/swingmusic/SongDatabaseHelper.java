package com.example.swingmusic;

import android.content.Context;

import androidx.room.Room;

public class SongDatabaseHelper {

    private static SongDatabase instance;

    public static SongDatabase getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    SongDatabase.class,"song_database")
                    .build();
        }
        return instance;
    }
}
