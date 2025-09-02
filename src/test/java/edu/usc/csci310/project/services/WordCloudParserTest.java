package edu.usc.csci310.project.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WordCloudParserTest {
    @Test
    public void parseWordFrequenciesTest() throws Exception {
        WordCloudParser wc = new WordCloudParser();
        String json = "[{\"text\": \"lyrics\", \"value\": 1}]";
        assertEquals("lyrics", wc.parseWordFrequencies(json).get(0).getWord());
    }
}
