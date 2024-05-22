package com.example.swingmusic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swingmusic.MusicAdapter;
import com.example.swingmusic.MusicFiles;
import com.example.swingmusic.R;
import com.example.swingmusic.SwingLibraryPage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class SongsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<MusicFiles> musicFilesArrayList;
    private MediaPlayer mediaPlayer ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        mediaPlayer = new MediaPlayer();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Get music files list
        SwingLibraryPage activity = (SwingLibraryPage) getActivity();
        musicFilesArrayList = SwingLibraryPage.getAllAudio(requireActivity());
        // Set adapter to RecyclerView
        MusicAdapter adapter = new MusicAdapter(getActivity(), musicFilesArrayList);
        recyclerView.setAdapter(adapter);



        adapter.setOnItemClickListener(position -> {
            String path = musicFilesArrayList.get(position).getPath() ;

           openMusicPlayer(path);
        });
    }

    private void openMusicPlayer(String path) {
        Intent intent = new Intent(getActivity(), MusicPlayer.class);
        intent.putParcelableArrayListExtra("musicFilesList", musicFilesArrayList);
        intent.putExtra("songPath",path);


        startActivity(intent);
    }

    public ArrayList<MusicFiles> getMusicFilesList() {
        return musicFilesArrayList;
    }
}
