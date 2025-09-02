package edu.usc.csci310.project.responses;

public class LoginResponse {
    private int id;
    private String username;
    private boolean isLoggedIn = false;

    public LoginResponse(int id, String username, boolean isLoggedIn) {
        this.id = id;
        this.username = username;
        this.isLoggedIn = isLoggedIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean loggedIn) {
        this.isLoggedIn = loggedIn;
    }
}
