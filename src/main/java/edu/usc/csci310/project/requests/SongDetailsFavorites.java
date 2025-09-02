package edu.usc.csci310.project.requests;

public class SongDetailsFavorites {
    private String artistName;
    private String songName;

    public SongDetailsFavorites(String artistName, String songName) {
        this.artistName = artistName;
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
    public String getSongName() {
        return songName;
    }
    public void setSongName(String songName) {
        this.songName = songName;
    }
}
