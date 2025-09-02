package edu.usc.csci310.project.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {
    private String username;
    private String password;

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setLoginRequestUsername(String username) {
        this.username = username;
    }

    @JsonProperty("password")
    public String getLoginRequestPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setLoginRequestPassword(String password) {
        this.password = password;
    }
}
