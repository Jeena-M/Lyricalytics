package edu.usc.csci310.project.requests;

public class UpdateExistingWordCloudWithSearchWithSongCount {
    private String artist;
    private int songCount;
    private String username;
    private String wordCloudJson;

    public UpdateExistingWordCloudWithSearchWithSongCount() {}
    public UpdateExistingWordCloudWithSearchWithSongCount(String artist, int songCount, String username, String wordCloudJson) {
        this.artist = artist;
        this.songCount = songCount;
        this.username = username;
        this.wordCloudJson = wordCloudJson;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
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


}
