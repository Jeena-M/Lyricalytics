package edu.usc.csci310.project.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateAccountResponse {
    private int id;
    private String username;
    private String password;

    public CreateAccountResponse(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    @JsonProperty("id")
    public int getIdAccount() {
        return id;
    }

    public void setIdAccount(int id) {
        this.id = id;
    }

    @JsonProperty("username")
    public String getUsernameAccount() {
        return username;
    }

    public void setUsernameAccount(String username) {
        this.username = username;
    }

    @JsonProperty("password")
    public String getPasswordAccount() {
        return password;
    }

    public void setPasswordAccount(String password) {
        this.password = password;
    }
}
