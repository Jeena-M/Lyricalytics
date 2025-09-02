package edu.usc.csci310.project.requests;

public class UpdateExistingWordCloudRequest {
    private String username;
    private String wordCloud;

    public UpdateExistingWordCloudRequest(String username, String wordCloud) {
        this.username = username;
        this.wordCloud = wordCloud;
    }

    public String getUsername() {
        return username;
    }

    public String getWordCloud() {
        return wordCloud;
    }

    public void setWordCloud(String wordCloud) {
        this.wordCloud = wordCloud;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
