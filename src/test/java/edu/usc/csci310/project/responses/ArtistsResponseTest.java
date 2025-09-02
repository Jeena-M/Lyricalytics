package edu.usc.csci310.project.responses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArtistsResponseTest {

    @Test
    void testSetArtists() {
        Artists artists = new Artists();
        ArtistsResponse response = new ArtistsResponse();

        response.setArtists(artists);

        assertEquals(artists, response.getArtists());
    }
}
