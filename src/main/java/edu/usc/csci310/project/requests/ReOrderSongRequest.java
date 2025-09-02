package edu.usc.csci310.project.requests;

public class ReOrderSongRequest {
    private String songName;
    private String artistName;
    private String username;
    public ReOrderSongRequest(String songName, String artistName, String username) {
        this.songName = songName;
        this.artistName = artistName;
        this.username = username;
    }
    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

