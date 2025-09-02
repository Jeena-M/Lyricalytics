package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateExistingWordCloudWithSearchWithSongCountTest {

    @Test
    void updateExistingWordCloudWithSearchWithSongCount_settersAndGetters() {
        UpdateExistingWordCloudWithSearchWithSongCount request = new UpdateExistingWordCloudWithSearchWithSongCount();

        request.setArtist("Taylor Swift");
        request.setSongCount(3);
        request.setUsername("user1");

        assertEquals("Taylor Swift", request.getArtist());
        assertEquals(3, request.getSongCount());
        assertEquals("user1", request.getUsername());
    }

}