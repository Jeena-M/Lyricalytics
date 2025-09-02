package edu.usc.csci310.project.requests;

public class WordCloudGenerateRequest {
    private String artistName;
    private int songCount;
    private String username;

    public WordCloudGenerateRequest(String artistName, int songCount, String username) {
        this.artistName = artistName;
        this.songCount = songCount;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
