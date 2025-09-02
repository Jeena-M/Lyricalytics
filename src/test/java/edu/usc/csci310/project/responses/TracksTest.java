package edu.usc.csci310.project.responses;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TracksTest {

    @Test
    void testSetItems() {
        Track track1 = new Track();
        track1.setName("Track One");

        Track track2 = new Track();
        track2.setName("Track Two");

        List<Track> trackList = Arrays.asList(track1, track2);

        Tracks tracks = new Tracks();
        tracks.setItems(trackList);

        assertEquals(2, tracks.getItems().size());
        assertEquals("Track One", tracks.getItems().get(0).getName());
        assertEquals("Track Two", tracks.getItems().get(1).getName());
    }
}