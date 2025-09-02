package edu.usc.csci310.project.requests;

import java.util.List;

public class WordCloudFromListGenerateRequest {
    private String artistName;
    private List<String> songs;
    private String username;

    public WordCloudFromListGenerateRequest() {}

    public WordCloudFromListGenerateRequest(String artistName, List<String> songs, String username) {
        this.songs = songs;
        this.artistName = artistName;
        this.username = username;
    }

    public String getArtistName() {
        return artistName;
    }
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public List<String> getSongs() {
        return songs;
    }
    public void setSongs(List<String> songs) {
        this.songs = songs;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
