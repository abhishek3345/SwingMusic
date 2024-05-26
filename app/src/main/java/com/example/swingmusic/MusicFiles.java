package com.example.swingmusic;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.UUID;

public class MusicFiles implements Parcelable {

    private String id;
    private String path;
    private String title;
    private String artist;
    private String album;
    private String duration;

    private String url;

    private SongDao songDao;

    private boolean isFavorite ;

    public MusicFiles() {

    }


    public MusicFiles(String path, String title, String artist, String album, String duration) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.url = url;
        this.id = UUID.randomUUID().toString();
        this.isFavorite = false;

    }

    public MusicFiles(Parcel in) {
        id = in .readString();
        path = in.readString();
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        duration = in.readString();
        url = in.readString();
        isFavorite = in.readByte() !=0;
    }


    public static final Creator<MusicFiles> CREATOR = new Creator<MusicFiles>() {
        @Override
        public MusicFiles createFromParcel(Parcel in) {
            return new MusicFiles(in);
        }

        @Override
        public MusicFiles[] newArray(int size) {
            return new MusicFiles[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(path);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeString(duration);
        dest.writeString(url);
        dest.writeByte((byte) (isFavorite ? 1:0));
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isFavorite(){
        return isFavorite;
    }

    public void setFavorite(boolean favorite){
        isFavorite = favorite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {this.id = id;}

    public void insertSongs(List<MusicFiles> musicFiles, Context context){
        songDao = SongDatabaseHelper.getInstance(context).songDao();

        for (MusicFiles musicFile : musicFiles) {
            Song song = new Song(
                    musicFile.getId(),
                    musicFile.getPath(),
                    musicFile.getTitle(),
                    musicFile.getArtist(),
                    musicFile.getAlbum(),
                    musicFile.getDuration(),
                    musicFile.isFavorite()
            );
            songDao.insert(song);
        }
    }

}
