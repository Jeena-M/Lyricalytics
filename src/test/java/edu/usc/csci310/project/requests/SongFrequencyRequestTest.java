package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SongFrequencyRequestTest {

    @Test
    void testConstructorAndGetters() {
        SongFrequencyRequest req = new SongFrequencyRequest(
                "ArtistX", 5, "hello", "UserA"
        );

        assertEquals("ArtistX", req.getArtistName(), "artistName should match constructor");
        assertEquals(5, req.getSongCount(), "songCount should match constructor");
        assertEquals("hello", req.getWord(), "word should match constructor");
        assertEquals("UserA", req.getUsername(), "username should match constructor");
    }

    @Test
    void testSettersAndGetters() {
        SongFrequencyRequest req = new SongFrequencyRequest(
                "A", 1, "w", "U"
        );

        // artistName
        req.setArtistName("NewArtist");
        assertEquals("NewArtist", req.getArtistName());

        // songCount
        req.setSongCount(10);
        assertEquals(10, req.getSongCount());

        // word
        req.setWord("world");
        assertEquals("world", req.getWord());

        // username
        req.setUsername("NewUser");
        assertEquals("NewUser", req.getUsername());
    }

    @Test
    void testNullStrings() {
        SongFrequencyRequest req = new SongFrequencyRequest(
                "X", 2, "Y", "Z"
        );

        req.setArtistName(null);
        req.setWord(null);
        req.setUsername(null);

        assertNull(req.getArtistName(), "artistName should be null after setter");
        assertNull(req.getWord(), "word should be null after setter");
        assertNull(req.getUsername(), "username should be null after setter");
    }

    @Test
    void testSongCountEdgeValues() {
        SongFrequencyRequest req = new SongFrequencyRequest(
                "Any", 3, "w", "u"
        );

        // zero
        req.setSongCount(0);
        assertEquals(0, req.getSongCount(), "songCount should be zero");

        // negative
        req.setSongCount(-5);
        assertEquals(-5, req.getSongCount(), "songCount should accept negative values");
    }
}