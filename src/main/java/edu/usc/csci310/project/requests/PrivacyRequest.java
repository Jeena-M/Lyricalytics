package edu.usc.csci310.project.requests;

public class PrivacyRequest {
    String username;
    boolean privacy;

    public PrivacyRequest(String username, boolean privacy) {
        this.username = username;
        this.privacy = privacy;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }
}
