package edu.usc.csci310.project.requests;

public class ArtistsRequest {
    private String artistName;
    public ArtistsRequest() {}

    public ArtistsRequest(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistName() {
        return artistName;
    }
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
