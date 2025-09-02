package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SongDetailsFavoritesTest {

    @Test
    void testConstructorAndGetters() {
        SongDetailsFavorites sdf = new SongDetailsFavorites("ArtistOne", "SongOne");

        assertEquals("ArtistOne", sdf.getArtistName(), "artistName should match constructor");
        assertEquals("SongOne",   sdf.getSongName(),   "songName should match constructor");
    }

    @Test
    void testSettersAndGetters() {
        SongDetailsFavorites sdf = new SongDetailsFavorites("A", "B");

        // artistName setter/getter
        sdf.setArtistName("NewArtist");
        assertEquals("NewArtist", sdf.getArtistName());

        // songName setter/getter
        sdf.setSongName("NewSong");
        assertEquals("NewSong", sdf.getSongName());
    }

    @Test
    void testNullAndEmptyAssignments() {
        SongDetailsFavorites sdf = new SongDetailsFavorites("InitArtist", "InitSong");

        // set to null
        sdf.setArtistName(null);
        sdf.setSongName(null);
        assertNull(sdf.getArtistName(), "artistName should be null after setter");
        assertNull(sdf.getSongName(),   "songName should be null after setter");

        // set to empty strings
        sdf.setArtistName("");
        sdf.setSongName("");
        assertEquals("", sdf.getArtistName(), "artistName should be empty string");
        assertEquals("", sdf.getSongName(),   "songName should be empty string");
    }
}