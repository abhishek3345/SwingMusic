package com.example.swingmusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swingmusic.MusicFiles;
import com.example.swingmusic.R;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavouriteViewHolder> {

    private Context mContext;
    private ArrayList<MusicFiles> mFavouriteMusicFiles;

    private static OnItemClickListener mListener ;

    public FavoritesAdapter(Context context, ArrayList<MusicFiles> favouriteMusicFiles) {
        mContext = context;
        mFavouriteMusicFiles = favouriteMusicFiles;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener ;
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new FavouriteViewHolder(view);
    }

    public void setData(ArrayList<MusicFiles> newData) {
        mFavouriteMusicFiles = newData;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        MusicFiles musicFile = mFavouriteMusicFiles.get(position);
        holder.fileName.setText(musicFile.getTitle());
        holder.artistName.setText(musicFile.getArtist());
        // You can set the album art here if needed
    }

    @Override
    public int getItemCount() {
          if(mFavouriteMusicFiles != null){
            return mFavouriteMusicFiles.size();
        }else {
              return 0;
          }
    }


    static class FavouriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView fileName;
        TextView artistName;
        ImageView albumArt;

        FavouriteViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.music_file_name);
            artistName = itemView.findViewById(R.id.song_artist);
            albumArt = itemView.findViewById(R.id.music_img);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                mListener.onItemClick(position);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                mListener.onItemLongClick(position);
            }
            return true;
        }

    }
}