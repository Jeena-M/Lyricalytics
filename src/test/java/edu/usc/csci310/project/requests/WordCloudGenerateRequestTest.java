package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WordCloudGenerateRequestTest {

    @Test
    void testConstructorAndGetters() {
        WordCloudGenerateRequest req = new WordCloudGenerateRequest(
                "ArtistZ", 7, "UserZ"
        );

        assertEquals("ArtistZ", req.getArtistName(), "artistName should match constructor");
        assertEquals(7,         req.getSongCount(),  "songCount should match constructor");
        assertEquals("UserZ",   req.getUsername(),   "username should match constructor");
    }

    @Test
    void testSettersAndGetters() {
        WordCloudGenerateRequest req = new WordCloudGenerateRequest(
                "A", 1, "U"
        );

        // artistName
        req.setArtistName("NewArtist");
        assertEquals("NewArtist", req.getArtistName());

        // songCount
        req.setSongCount(15);
        assertEquals(15, req.getSongCount());

        // username
        req.setUsername("NewUser");
        assertEquals("NewUser", req.getUsername());
    }

    @Test
    void testNullStringsAndEdgeSongCount() {
        WordCloudGenerateRequest req = new WordCloudGenerateRequest(
                "InitArtist", 5, "InitUser"
        );

        // assign null to String fields
        req.setArtistName(null);
        req.setUsername(null);
        assertNull(req.getArtistName(), "artistName should be null after setter");
        assertNull(req.getUsername(),   "username should be null after setter");

        // edge values for songCount
        req.setSongCount(0);
        assertEquals(0, req.getSongCount(), "songCount should accept zero");

        req.setSongCount(-3);
        assertEquals(-3, req.getSongCount(), "songCount should accept negative values");
    }
}