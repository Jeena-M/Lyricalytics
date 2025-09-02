package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordCloudFromListGenerateRequestTest {

    @Test
    void testNoArgConstructorInitialValues() {
        WordCloudFromListGenerateRequest req = new WordCloudFromListGenerateRequest();
        assertNull(req.getArtistName(), "artistName should be null after no-arg constructor");
        assertNull(req.getSongs(),      "songs should be null after no-arg constructor");
        assertNull(req.getUsername(),   "username should be null after no-arg constructor");
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        List<String> songs = Arrays.asList("Song1", "Song2", "Song3");
        WordCloudFromListGenerateRequest req = new WordCloudFromListGenerateRequest(
                "SomeArtist", songs, "TestUser"
        );

        assertEquals("SomeArtist", req.getArtistName(), "artistName should match constructor arg");
        assertSame(songs,          req.getSongs(),      "songs should be the same List instance");
        assertEquals("TestUser",   req.getUsername(),   "username should match constructor arg");
    }

    @Test
    void testSettersAndGetters() {
        WordCloudFromListGenerateRequest req = new WordCloudFromListGenerateRequest();

        // set artistName
        req.setArtistName("NewArtist");
        assertEquals("NewArtist", req.getArtistName());

        // set songs
        List<String> newSongs = Collections.singletonList("OnlySong");
        req.setSongs(newSongs);
        assertSame(newSongs, req.getSongs());

        // set username
        req.setUsername("NewUser");
        assertEquals("NewUser", req.getUsername());
    }

    @Test
    void testNullAssignmentsViaSetters() {
        List<String> songs = Arrays.asList("A", "B");
        WordCloudFromListGenerateRequest req = new WordCloudFromListGenerateRequest(
                "Artist", songs, "User"
        );

        req.setArtistName(null);
        req.setSongs(null);
        req.setUsername(null);

        assertNull(req.getArtistName(), "artistName should be null after setter");
        assertNull(req.getSongs(),      "songs should be null after setter");
        assertNull(req.getUsername(),   "username should be null after setter");
    }
}