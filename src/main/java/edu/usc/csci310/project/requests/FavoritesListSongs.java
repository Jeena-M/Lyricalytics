package edu.usc.csci310.project.requests;

public class FavoritesListSongs {
    private String username;
    public FavoritesListSongs() {}
    public FavoritesListSongs(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
