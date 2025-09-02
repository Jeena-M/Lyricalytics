package edu.usc.csci310.project.responses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LyricsResponseTest {

    @Test
    void testSetLyrics() {
        LyricsResponse response = new LyricsResponse();
        response.setLyrics("We're no strangers to love...");
        assertEquals("We're no strangers to love...", response.getLyrics());
    }
}