package edu.usc.csci310.project.requests;

public class SongFrequencyRequest {
    private String artistName;
    private int songCount;
    private String word;
    private String username;

    public SongFrequencyRequest(String artistName, int songCount, String word, String username) {
        this.artistName = artistName;
        this.songCount = songCount;
        this.word = word;
        this.username = username;
    }

    public String getArtistName() {
        return artistName;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
