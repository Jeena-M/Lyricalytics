package edu.usc.csci310.project.responses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlbumTest {

    @Test
    void testSetName() {
        Album album = new Album();
        album.setName("Test Album");
        assertEquals("Test Album", album.getName());
    }

    @Test
    void testSetReleaseDate() {
        Album album = new Album();
        album.setRelease_date("2024-01-01");
        assertEquals("2024-01-01", album.getRelease_date());
    }
}