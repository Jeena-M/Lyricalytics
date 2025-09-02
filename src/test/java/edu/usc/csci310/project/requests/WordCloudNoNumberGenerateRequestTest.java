package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WordCloudNoNumberGenerateRequestTest {

    @Test
    void testNoArgConstructorInitialValues() {
        WordCloudNoNumberGenerateRequest req = new WordCloudNoNumberGenerateRequest();
        assertNull(req.getArtistName(), "artistName should be null after no-arg constructor");
        assertNull(req.getUsername(),   "username should be null after no-arg constructor");
    }

    @Test
    void testSingleArgConstructor() {
        WordCloudNoNumberGenerateRequest req = new WordCloudNoNumberGenerateRequest("ArtistSolo");
        assertEquals("ArtistSolo", req.getArtistName(), "artistName should match single-arg constructor");
        assertNull(req.getUsername(), "username should remain null when not set");
    }

    @Test
    void testSettersAndGetters() {
        WordCloudNoNumberGenerateRequest req = new WordCloudNoNumberGenerateRequest();
        req.setArtistName("NewArtist");
        req.setUsername("NewUser");

        assertEquals("NewArtist", req.getArtistName(), "setter/getter for artistName");
        assertEquals("NewUser",   req.getUsername(),   "setter/getter for username");
    }

    @Test
    void testNullAndEmptyAssignments() {
        WordCloudNoNumberGenerateRequest req = new WordCloudNoNumberGenerateRequest("InitArtist");
        // null
        req.setArtistName(null);
        req.setUsername(null);
        assertNull(req.getArtistName(), "artistName should be null after setter");
        assertNull(req.getUsername(),   "username should be null after setter");
        // empty string
        req.setArtistName("");
        req.setUsername("");
        assertEquals("", req.getArtistName(), "artistName should accept empty string");
        assertEquals("", req.getUsername(),   "username should accept empty string");
    }
}