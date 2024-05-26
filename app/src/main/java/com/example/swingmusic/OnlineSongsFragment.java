package com.example.swingmusic;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public class OnlineSongsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<MusicFiles> musicFilesArrayList;
    private OnlineSongsAdapter adapter;
    private FirestoreHelper firestoreHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_online_songs, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        firestoreHelper = new FirestoreHelper();
        fetchMusicFiles();

        return rootView;
    }

    private void fetchMusicFiles() {
        firestoreHelper.fetchMusicFiles(new FirestoreHelper.FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<MusicFiles> musicFiles) {
                musicFilesArrayList = musicFiles;
                adapter = new OnlineSongsAdapter(getActivity(), musicFilesArrayList, new OnlineSongsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String url = musicFilesArrayList.get(position).getUrl();
                        openMusicPlayer(url, position);
                    }
                });
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private void openMusicPlayer(String url, int position) {
        Intent intent = new Intent(getActivity(), MusicPlayer.class);
        intent.putParcelableArrayListExtra("musicFilesList", musicFilesArrayList);
        intent.putExtra("songUrl", url);
        intent.putExtra("songIndex", position);
        startActivity(intent);
    }
}
