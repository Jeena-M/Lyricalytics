package edu.usc.csci310.project.requests;

public class WordCloudNoNumberGenerateRequest {
    private String artistName;
    private String username;

    public WordCloudNoNumberGenerateRequest() {}

    public WordCloudNoNumberGenerateRequest(String artistName) {
        this.artistName = artistName;
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
