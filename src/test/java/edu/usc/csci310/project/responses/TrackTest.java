package edu.usc.csci310.project.responses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrackTest {

    @Test
    void testSetName() {
        Track track = new Track();
        track.setName("Blinding Lights");
        assertEquals("Blinding Lights", track.getName());
    }

    @Test
    void testSetAlbum() {
        Album album = new Album();
        album.setName("After Hours");
        album.setRelease_date("2020-03-20");

        Track track = new Track();
        track.setAlbum(album);

        assertEquals(album, track.getAlbum());
        assertEquals("After Hours", track.getAlbum().getName());
        assertEquals("2020-03-20", track.getAlbum().getRelease_date());
    }
}
