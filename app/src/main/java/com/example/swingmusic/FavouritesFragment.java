package com.example.swingmusic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FavouritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoritesAdapter favoritesAdapter ;
    private MusicAdapter adapter;
    private ArrayList<MusicFiles> favouritesList;

    private SharedPreferences sharedPreferences;
    private Set<String> favoriteSongIds;


    private ArrayList<MusicFiles> allMusicFilesList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewFavourites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("FavoriteSongs", Context.MODE_PRIVATE);
        favoriteSongIds = sharedPreferences.getStringSet("FavoriteSongIds", new HashSet<>());

        // Get the musicFilesArrayList from the SongsFragment

        allMusicFilesList = SwingLibraryPage.getAllAudio(requireActivity());




        // Create an instance of the FavoritesAdapter
        favoritesAdapter = new FavoritesAdapter(getActivity(), favouritesList);
        recyclerView.setAdapter(favoritesAdapter);

        // Retrieve the list of favorite music files
        favouritesList = retrieveFavourites();


        // Set item click listener
        favoritesAdapter.setOnItemClickListener(position -> {
            String path = favouritesList.get(position).getPath();
            openMusicPlayer(path);
        });
    }

    // Method to retrieve favorite music files

    private ArrayList<MusicFiles> retrieveFavourites() {
        ArrayList<MusicFiles> favourites = new ArrayList<>();

        for (MusicFiles musicFile : allMusicFilesList) {
            if (favoriteSongIds.contains(musicFile.getId())) {
                favourites.add(musicFile);
            }
        }
        return favourites;
    }




    public void updateFavorites() {
        // Retrieve the updated list of favorites
        favouritesList = retrieveFavourites();

        // Notify the adapter about the data set change
        favoritesAdapter.updateFavoritesList(favouritesList);
    }


    // Method to open music player
    private void openMusicPlayer(String path) {
        Intent intent = new Intent(getActivity(), MusicPlayer.class);
        intent.putExtra("songPath", path);
        startActivity(intent);
    }
}
