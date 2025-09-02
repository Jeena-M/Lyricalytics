package edu.usc.csci310.project.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SongTest {

    @Test
    void getTitle() {
        Song st = new Song("Cruel Summer", "Taylor Swift", "2019", "Fever dream high in the quiet of the night\nYou know that I caught it (oh yeah, you're right, I want it)\nBad, bad boy, shiny toy with a price\nYou know that I bought it (oh yeah, you're right, I want it)");
        assertEquals("Cruel Summer", st.getTitle());
    }

    @Test
    void getArtist() {
        Song st = new Song("Cruel Summer", "Taylor Swift", "2019", "Fever dream high in the quiet of the night\nYou know that I caught it (oh yeah, you're right, I want it)\nBad, bad boy, shiny toy with a price\nYou know that I bought it (oh yeah, you're right, I want it)");
        assertEquals("Taylor Swift", st.getArtist());
    }

    @Test
    void getYear() {
        Song st = new Song("Cruel Summer", "Taylor Swift", "2019", "Fever dream high in the quiet of the night\nYou know that I caught it (oh yeah, you're right, I want it)\nBad, bad boy, shiny toy with a price\nYou know that I bought it (oh yeah, you're right, I want it)");
        assertEquals("2019", st.getYear());
    }

    @Test
    void getLyrics() {
        Song st = new Song("Cruel Summer", "Taylor Swift", "2019", "Fever dream high in the quiet of the night\nYou know that I caught it (oh yeah, you're right, I want it)\nBad, bad boy, shiny toy with a price\nYou know that I bought it (oh yeah, you're right, I want it)");
        assertEquals("Fever dream high in the quiet of the night\nYou know that I caught it (oh yeah, you're right, I want it)\nBad, bad boy, shiny toy with a price\nYou know that I bought it (oh yeah, you're right, I want it)", st.getLyrics());
    }
}