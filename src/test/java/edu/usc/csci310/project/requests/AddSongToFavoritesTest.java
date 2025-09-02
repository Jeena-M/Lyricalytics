package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AddSongToFavoritesTest {

    @Test
    void testNoArgConstructorInitialValues() {
        AddSongToFavorites req = new AddSongToFavorites();
        assertNull(req.getSongName(),   "songName should be null after no-arg constructor");
        assertNull(req.getArtistName(), "artistName should be null after no-arg constructor");
        assertNull(req.getUsername(),   "username should be null after no-arg constructor");
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        AddSongToFavorites req = new AddSongToFavorites("MySong", "MyArtist", "MyUser");
        assertEquals("MySong",   req.getSongName(),   "songName should match constructor");
        assertEquals("MyArtist", req.getArtistName(), "artistName should match constructor");
        assertEquals("MyUser",   req.getUsername(),   "username should match constructor");
    }

    @Test
    void testSettersAndGetters() {
        AddSongToFavorites req = new AddSongToFavorites();
        req.setSongName("NewSong");
        req.setArtistName("NewArtist");
        req.setUsername("NewUser");

        assertEquals("NewSong",   req.getSongName(),   "setter/getter for songName");
        assertEquals("NewArtist", req.getArtistName(), "setter/getter for artistName");
        assertEquals("NewUser",   req.getUsername(),   "setter/getter for username");
    }

    @Test
    void testNullAndEmptyAssignments() {
        AddSongToFavorites req = new AddSongToFavorites("S", "A", "U");
        req.setSongName(null);
        req.setArtistName(null);
        req.setUsername(null);
        assertNull(req.getSongName(),   "songName should be null after setter");
        assertNull(req.getArtistName(), "artistName should be null after setter");
        assertNull(req.getUsername(),   "username should be null after setter");
        req.setSongName("");
        req.setArtistName("");
        req.setUsername("");
        assertEquals("", req.getSongName(),   "songName should accept empty string");
        assertEquals("", req.getArtistName(), "artistName should accept empty string");
        assertEquals("", req.getUsername(),   "username should accept empty string");
    }
}