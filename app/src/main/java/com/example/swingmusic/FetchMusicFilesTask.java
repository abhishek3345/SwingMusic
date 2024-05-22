package com.example.swingmusic;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class FetchMusicFilesTask extends AsyncTask<Void, Void, ArrayList<MusicFiles>> {
    private Context context;
    private MusicFilesCallback callback;

    public FetchMusicFilesTask(Context context, MusicFilesCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected ArrayList<MusicFiles> doInBackground(Void... voids) {
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
        };
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);


        if (cursor != null) {

                while (cursor.moveToNext()) {
                    String album = cursor.getString(0);
                    String title = cursor.getString(1);
                    String duration = cursor.getString(2);
                    String path = cursor.getString(3);
                    String artist = cursor.getString(4);

                    MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration);

                    tempAudioList.add(musicFiles);
                }
        }
        return tempAudioList;
    }


    @Override
    protected void onPostExecute(ArrayList<MusicFiles> musicFiles) {
        super.onPostExecute(musicFiles);
        if (callback != null) {
            callback.onMusicFilesFetched(musicFiles);
        }
    }

    public interface MusicFilesCallback {
        void onMusicFilesFetched(ArrayList<MusicFiles> musicFiles);
    }


}

