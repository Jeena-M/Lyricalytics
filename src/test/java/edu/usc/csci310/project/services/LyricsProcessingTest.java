package edu.usc.csci310.project.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LyricsProcessingTest {
    private LyricsProcessing lyricsProcessing;
    private String lyrics = "[Intro: Sydney Sweeney]\n" +
            "No, seriously, get your hands off my man\n" +
            "\n" +
            "[Verse 1]\n" +
            "Baby blues, undressin' him\n" +
            "Funny how you think that I don't notice it\n" +
            "Actin' like we're friends, we're the opposite\n" +
            "I know what you are, tryin' so hard\n" +
            "Runnin' 'round tryna fuck a star, go\n" +
            "\n" +
            "[Chorus]\n" +
            "Look at the floor or ceilin'\n" +
            "Or anyone else you're feelin'\n" +
            "Take home whoever walks in\n" +
            "Just keep your eyes off him\n" +
            "Yes, I'm Miss Possessive\n" +
            "Pretty girl, gon' learn your lesson\n" +
            "Some fights you never gonna win\n" +
            "Just keep your eyes off him\n" +
            "\n" +
            "[Post-Chorus]\n" +
            "Better, better keep your, keep your, keep your, keep your\n" +
            "Bettеr, better keep your, keep your, keep your eyеs off\n" +
            "Better, better keep your, keep your, keep your, keep your\n" +
            "Better, better keep your, keep your, keep your eyes off\n";

    private String[] words = {"intro", "sydney", "sweeney", "seriously", "get", "hand", "man", "verse", "baby", "blues", "undressin", "funny", "think", "notice", "actin", "friend", "opposite", "know", "tryin", "hard", "runnin", "round", "tryna", "fuck", "star", "chorus", "look", "floor", "ceilin", "anyone", "feelin", "take", "home", "whoever", "walk", "keep", "eye", "yes", "miss", "possessive", "pretty", "girl", "learn", "lesson", "fight", "never", "win", "keep", "eye", "post-chorus", "better", "good", "keep", "keep", "keep", "keep", "bettеr", "good", "keep", "keep", "keep", "eyеs", "well", "good", "keep", "keep", "keep", "keep", "better", "good", "keep", "keep", "keep", "eye"};

    private List<String> expectedProcessedWords = Arrays.asList(words);


    @BeforeEach
    public void setUp() {
        lyricsProcessing = new LyricsProcessing();
    }

    @Test
    public void testProcessLyrics() {
        assertEquals(expectedProcessedWords, lyricsProcessing.processLyrics(lyrics));
    }

    @Test
    public void testProcessLyricsBranch() {
        assertEquals(expectedProcessedWords, lyricsProcessing.processLyrics(lyrics));
    }
}