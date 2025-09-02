package edu.usc.csci310.project.requests;

public class SoulmateEnemyRequest{
    private String username;

    public SoulmateEnemyRequest() {}
    public SoulmateEnemyRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
