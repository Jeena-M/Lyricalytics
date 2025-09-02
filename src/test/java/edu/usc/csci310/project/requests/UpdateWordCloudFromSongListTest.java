package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UpdateWordCloudFromSongListTest {

    @Test
    void testConstructorAndGetters() {
        List<String> songs = Arrays.asList("Song A", "Song B");
        UpdateWordCloudFromSongList req = new UpdateWordCloudFromSongList(
                "ArtistName", songs, "User123", "{\"foo\":\"bar\"}"
        );

        assertEquals("ArtistName", req.getArtist(), "artist should match constructor value");
        assertSame(songs, req.getSongs(), "songs list should be the same instance");
        assertEquals("User123", req.getUsername(), "username should match constructor value");
        assertEquals("{\"foo\":\"bar\"}", req.getWordCloudJson(), "wordCloudJson should match constructor value");
    }

    @Test
    void testSettersAndGetters() {
        UpdateWordCloudFromSongList req = new UpdateWordCloudFromSongList(
                "Initial", Collections.singletonList("InitialSong"), "InitialUser", "{}"
        );

        // artist
        req.setArtist("NewArtist");
        assertEquals("NewArtist", req.getArtist());

        // songs
        List<String> newSongs = Arrays.asList("X", "Y", "Z");
        req.setSongs(newSongs);
        assertSame(newSongs, req.getSongs());

        // username
        req.setUsername("NewUser");
        assertEquals("NewUser", req.getUsername());

        // wordCloudJson
        req.setWordCloudJson("[1,2,3]");
        assertEquals("[1,2,3]", req.getWordCloudJson());
    }

    @Test
    void testNullValues() {
        UpdateWordCloudFromSongList req = new UpdateWordCloudFromSongList(
                "A", Collections.singletonList("S"), "U", "J"
        );

        // set everything to null
        req.setArtist(null);
        req.setSongs(null);
        req.setUsername(null);
        req.setWordCloudJson(null);

        assertNull(req.getArtist(), "artist should be null after setter");
        assertNull(req.getSongs(), "songs should be null after setter");
        assertNull(req.getUsername(), "username should be null after setter");
        assertNull(req.getWordCloudJson(), "wordCloudJson should be null after setter");
    }
}
