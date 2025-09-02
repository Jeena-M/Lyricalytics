package edu.usc.csci310.project.requests;

import java.util.List;

public class UpdateWordCloudFromSongList {
    private String artist;
    private List<String> songs;
    private String username;
    private String wordCloudJson;

    public UpdateWordCloudFromSongList(String artist, List<String> songs, String username, String wordCloudJson) {
        this.artist = artist;
        this.songs = songs;
        this.username = username;
        this.wordCloudJson = wordCloudJson;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
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

    public String getWordCloudJson() {
        return wordCloudJson;
    }

    public void setWordCloudJson(String wordCloudJson) {
        this.wordCloudJson = wordCloudJson;
    }
}
