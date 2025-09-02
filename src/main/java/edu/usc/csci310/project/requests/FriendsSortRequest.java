package edu.usc.csci310.project.requests;

public class FriendsSortRequest {
    private boolean ascending;
    private String username;

    public FriendsSortRequest(boolean ascending, String username) {
        this.ascending = ascending;
        this.username = username;
    }

    public boolean getAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
