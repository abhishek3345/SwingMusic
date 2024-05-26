package com.example.swingmusic;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swingmusic.FavoritesAdapter;
import com.example.swingmusic.MusicFiles;
import com.example.swingmusic.R;
import com.example.swingmusic.SongDatabase;
import com.example.swingmusic.SongDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    private RecyclerView recyclerViewFavourites;
    private FavoritesAdapter favouritesAdapter;
    private ArrayList<MusicFiles> favouriteMusicFiles;
    private SongDatabase songDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        recyclerViewFavourites = rootView.findViewById(R.id.recyclerViewFavourites);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favouriteMusicFiles = new ArrayList<>();

        // Get an instance of the SongDao
        songDatabase = SongDatabaseHelper.getInstance(requireContext());

        LiveData<List<Song>> favouriteSongs = songDatabase.songDao().getFavoriteSongs();

        favouriteSongs.observe(getViewLifecycleOwner(), songs->{

            // Convert the list of favourite songs to a list of MusicFiles
            favouriteMusicFiles = convertSongsToMusicFiles(songs);
            favouritesAdapter.setData(favouriteMusicFiles);

            //On tiem click
            favouritesAdapter.setOnItemClickListener(new FavoritesAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    String path = favouriteMusicFiles.get(position).getPath();
                    openMusicPlayer(path, position, favouriteMusicFiles);

                }
                @Override
                public void onItemLongClick(int position) {
                    showRemoveFromFavouritesDialog(position);
                }
            });


        });

        // Set up the adapter and recyclerView
        favouritesAdapter = new FavoritesAdapter(getActivity(),favouriteMusicFiles);
        recyclerViewFavourites.setAdapter(favouritesAdapter);
        recyclerViewFavourites.setLayoutManager(new LinearLayoutManager(requireContext()));

    }

    private void showRemoveFromFavouritesDialog(int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Remove from Favourites")
                .setMessage("Are you sure you want to remove this song from favourites?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    removeFromFavourites(position);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void removeFromFavourites(int position) {
        String songId = favouriteMusicFiles.get(position).getId();

        DatabaseExecutor.getExecutor().execute(() -> {
            try {
                Song song = songDatabase.songDao().getSongByIdSync(songId);
                if (song != null) {
                    boolean isFav ;
                    isFav = !song.isFavorite();
                    song.setFavorite(isFav);
                    songDatabase.songDao().update(song);

                    requireActivity().runOnUiThread(() -> {
                        favouriteMusicFiles.remove(position);
                        favouritesAdapter.notifyItemRemoved(position);
                    });
                }
            } catch (Exception e) {
                Log.e("FavouritesFragment", "Error removing song from favourites", e);
            }
        });
    }

    private void openMusicPlayer(String path, int position, ArrayList<MusicFiles>favouriteMusicFiles) {

        Intent intent = new Intent(getActivity(), MusicPlayer.class);
        intent.putParcelableArrayListExtra("musicFilesList", favouriteMusicFiles);
        intent.putExtra("songPath",path);
        intent.putExtra("songIndex",position);

        startActivity(intent);
    }

    private ArrayList<MusicFiles> convertSongsToMusicFiles(List<Song> songs) {
        ArrayList<MusicFiles> musicFiles = new ArrayList<>();
        for (Song song : songs) {
            MusicFiles musicFile = new MusicFiles(
                    song.getPath(),
                    song.getTitle(),
                    song.getArtist(),
                    song.getAlbum(),
                    song.getDuration()
            );
            musicFile.setFavorite(song.isFavorite());
            musicFiles.add(musicFile);
        }
        return musicFiles;
    }
}