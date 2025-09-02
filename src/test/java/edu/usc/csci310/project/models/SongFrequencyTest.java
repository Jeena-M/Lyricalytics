package edu.usc.csci310.project.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SongFrequencyTest {

    @Test
    void getSong() {
        SongFrequency sf = new SongFrequency("Cruel Summer", 5);
        assertEquals("Cruel Summer", sf.getSong());
    }

    @Test
    void setSong() {
        SongFrequency sf = new SongFrequency("Cruel Summer", 5);
        sf.setSong("You Belong With Me");
        assertEquals("You Belong With Me", sf.getSong());
    }

    @Test
    void getCount() {
        SongFrequency sf = new SongFrequency("Cruel Summer", 5);
        assertEquals(5, sf.getCount());
    }

    @Test
    void setCount() {
        SongFrequency sf = new SongFrequency("Cruel Summer", 5);
        sf.setCount(1);
        assertEquals(1, sf.getCount());
    }
}