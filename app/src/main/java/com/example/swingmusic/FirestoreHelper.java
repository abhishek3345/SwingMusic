package com.example.swingmusic;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class FirestoreHelper {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference musicFilesRef = db.collection("musicFiles");

    public interface FirestoreCallback {
        void onCallback(ArrayList<MusicFiles> musicFilesList);
    }

    public void fetchMusicFiles(final FirestoreCallback firestoreCallback) {
        musicFilesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<MusicFiles> musicFilesList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("FirestoreHelper", "Document ID: " + document.getId() + " => " + document.getData());
                        MusicFiles musicFile = document.toObject(MusicFiles.class);
                        Log.d("FirestoreHelper", "MusicFile: " + musicFile.getTitle() + ", URL: " + musicFile.getUrl());
                        musicFilesList.add(musicFile);
                    }
                    firestoreCallback.onCallback(musicFilesList);
                } else {
                    Log.d("FirestoreHelper", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
