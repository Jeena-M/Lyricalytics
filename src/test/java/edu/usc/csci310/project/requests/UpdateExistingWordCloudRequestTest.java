package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateExistingWordCloudRequestTest {
    @Test
    public void getUsernameTest() {
        UpdateExistingWordCloudRequest up = new UpdateExistingWordCloudRequest("username", "cloud");
        assertEquals("username", up.getUsername());
    }

    @Test
    public void setUsernameTest() {
        UpdateExistingWordCloudRequest up = new UpdateExistingWordCloudRequest("username", "cloud");
        up.setUsername("username2");
        assertEquals("username2", up.getUsername());
    }

    @Test
    public void getWordCloudTest() {
        UpdateExistingWordCloudRequest up = new UpdateExistingWordCloudRequest("username", "cloud");
        assertEquals("cloud", up.getWordCloud());
    }

    @Test
    public void setWordCloudTest() {
        UpdateExistingWordCloudRequest up = new UpdateExistingWordCloudRequest("username", "cloud");
        up.setWordCloud("cloud2");
        assertEquals("cloud2", up.getWordCloud());
    }
}
