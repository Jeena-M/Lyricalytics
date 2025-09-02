package edu.usc.csci310.project.responses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManyTracksResponseTest {

    @Test
    void testSetTracks() {
        Tracks tracks = new Tracks();
        ManyTracksResponse response = new ManyTracksResponse();

        response.setTracks(tracks);

        assertEquals(tracks, response.getTracks());
    }
}
