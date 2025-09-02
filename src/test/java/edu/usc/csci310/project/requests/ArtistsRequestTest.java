package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArtistsRequestTest {
    @Test
    public void getArtistNameTest() {
        ArtistsRequest artistsRequest = new ArtistsRequest("Noah Kahan");
        assertEquals("Noah Kahan", artistsRequest.getArtistName());
    }

    @Test
    public void setArtistNameTest() {
        ArtistsRequest artistsRequest = new ArtistsRequest();
        artistsRequest.setArtistName("Noah Kahan");
        assertEquals("Noah Kahan", artistsRequest.getArtistName());
    }
}
