package com.example.swingmusic;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {

    private Context context;
    private ArrayList<MusicFiles> favoritesList;
    private OnItemClickListener onItemClickListener;


    public FavoritesAdapter(Context context, ArrayList<MusicFiles> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList != null ? favoritesList : new ArrayList<>();
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_items, parent, false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        MusicFiles currentSong = favoritesList.get(position);
        holder.file_name.setText(currentSong.getTitle());
        holder.artist_name.setText(currentSong.getArtist());
        // Set the album art or thumbnail if available
        // holder.album_art.setImageBitmap(...);
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public void updateFavoritesList(ArrayList<MusicFiles> newFavoritesList) {
        this.favoritesList = newFavoritesList != null ? newFavoritesList : new ArrayList<>();
        notifyDataSetChanged();
    }
    public class FavoritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView file_name;
        TextView artist_name;
        ImageView album_art;

        public FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            artist_name = itemView.findViewById(R.id.song_artist);
            album_art = itemView.findViewById(R.id.music_img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(position);
                }
            }
        }
    }

}