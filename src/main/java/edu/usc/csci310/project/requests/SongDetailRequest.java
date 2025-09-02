package edu.usc.csci310.project.requests;

public class SongDetailRequest {
    private String artistName;
    private String songName;
    private String username;

    public SongDetailRequest(String artistName, String songName, String userName) {
        this.artistName = artistName;
        this.songName = songName;
        this.username = userName;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
