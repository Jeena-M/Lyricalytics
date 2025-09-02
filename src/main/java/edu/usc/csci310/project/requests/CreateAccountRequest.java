package edu.usc.csci310.project.requests;

public class CreateAccountRequest {
    private String createReqUsername;
    private String createRequestPass;
    private String createRequestConfirmPass;

    public String getUsername() {
        return createReqUsername;
    }

    public void setUsername(String createReqUsername) {
        this.createReqUsername = createReqUsername;
    }

    public String getPassword() {
        return createRequestPass;
    }

    public void setPassword(String createRequestPass) {
        this.createRequestPass = createRequestPass;
    }

    public String getConfirmPassword() {
        return createRequestConfirmPass;
    }

    public void setConfirmPassword(String createRequestConfirmPass) {
        this.createRequestConfirmPass = createRequestConfirmPass;
    }
}