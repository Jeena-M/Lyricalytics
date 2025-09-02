package edu.usc.csci310.project.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArtistTest {
    @Test
    void getName() {
        Artist artist = new Artist("Noah Kahan", "fakeurl");
        assertEquals("Noah Kahan", artist.getName());
    }

    @Test
    void setName() {
        Artist artist = new Artist("Noah", "fakeurl");
        artist.setName("Noah Kahan");
        assertEquals("Noah Kahan", artist.getName());
    }

    @Test
    void getImageUrl() {
        Artist artist = new Artist("Noah Kahan", "fakeurl");
        assertEquals("fakeurl", artist.getImageUrl());
    }

    @Test
    void setImageUrl() {
        Artist artist = new Artist("Noah", "fakeurl");
        artist.setImageUrl("fakeurl2");
        assertEquals("fakeurl2", artist.getImageUrl());
    }

}
