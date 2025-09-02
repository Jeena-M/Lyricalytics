package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SongDetailRequestTest {

    @Test
    void testConstructorAndGetters() {
        SongDetailRequest req = new SongDetailRequest(
                "ArtistAlpha", "SongBeta", "UserGamma"
        );

        assertEquals("ArtistAlpha", req.getArtistName(), "artistName should match constructor");
        assertEquals("SongBeta",    req.getSongName(),    "songName should match constructor");
        assertEquals("UserGamma",   req.getUsername(),    "username should match constructor");
    }

    @Test
    void testSettersAndGetters() {
        SongDetailRequest req = new SongDetailRequest(
                "X", "Y", "Z"
        );

        // artistName
        req.setArtistName("NewArtist");
        assertEquals("NewArtist", req.getArtistName());

        // songName
        req.setSongName("NewSong");
        assertEquals("NewSong", req.getSongName());

        // username
        req.setUsername("NewUser");
        assertEquals("NewUser", req.getUsername());
    }

    @Test
    void testNullAssignments() {
        SongDetailRequest req = new SongDetailRequest(
                "InitArtist", "InitSong", "InitUser"
        );

        req.setArtistName(null);
        req.setSongName(null);
        req.setUsername(null);

        assertNull(req.getArtistName(), "artistName should be null after setter");
        assertNull(req.getSongName(),   "songName should be null after setter");
        assertNull(req.getUsername(),   "username should be null after setter");
    }
}