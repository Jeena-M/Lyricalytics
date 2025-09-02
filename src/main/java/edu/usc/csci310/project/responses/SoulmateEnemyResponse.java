package edu.usc.csci310.project.responses;

import edu.usc.csci310.project.models.Song;

import java.util.List;

public class SoulmateEnemyResponse {
    private String username;
    private List<Song> favorites;

    public SoulmateEnemyResponse(String username, List<Song> favorites) {
        this.username = username;
        this.favorites = favorites;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Song> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Song> favorites) {
        this.favorites = favorites;
    }
}
