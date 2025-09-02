package edu.usc.csci310.project.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordFrequencyTest {

    @Test
    void getWord() {
        WordFrequency wf = new WordFrequency("risk", 6);
        assertEquals("risk", wf.getWord());
    }

    @Test
    void setWord() {
        WordFrequency wf = new WordFrequency("risk", 6);
        wf.setWord("know");
        assertEquals("know", wf.getWord());
    }

    @Test
    void getCount() {
        WordFrequency wf = new WordFrequency("risk", 6);
        assertEquals(6, wf.getCount());
    }

    @Test
    void setCount() {
        WordFrequency wf = new WordFrequency("risk", 6);
        wf.setCount(1);
        assertEquals(1, wf.getCount());
    }
}