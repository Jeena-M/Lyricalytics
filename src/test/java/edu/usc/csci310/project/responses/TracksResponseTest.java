package edu.usc.csci310.project.responses;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TracksResponseTest {

    @Test
    void testSetTracks() {
        Track track1 = new Track();
        track1.setName("Song A");

        Track track2 = new Track();
        track2.setName("Song B");

        List<Track> trackList = Arrays.asList(track1, track2);

        TracksResponse response = new TracksResponse();
        response.setTracks(trackList);

        assertEquals(2, response.getTracks().size());
        assertEquals("Song A", response.getTracks().get(0).getName());
        assertEquals("Song B", response.getTracks().get(1).getName());
    }
}