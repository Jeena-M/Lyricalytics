package edu.usc.csci310.project.requests;

import edu.usc.csci310.project.models.WordFrequency;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.usc.csci310.project.requests.WordFrequencyRequest.getTop100WordFrequencies;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WordFrequencyRequestTest {

    @Test
    void getTop100WordFrequenciesTest1() {
        List<String> processedLyrics = new ArrayList<>();
        processedLyrics.add("Twinkle");
        processedLyrics.add("Twinkle");
        processedLyrics.add("Little");
        processedLyrics.add("Star");

        WordFrequency wf1 = new WordFrequency("Twinkle", 2);
        WordFrequency wf2 = new WordFrequency("Little", 1);
        WordFrequency wf3 = new WordFrequency("Star", 1);

        List<WordFrequency> expected = Arrays.asList(wf1, wf3, wf2);

        WordFrequencyRequest request = new WordFrequencyRequest();
        List<WordFrequency> output = getTop100WordFrequencies(processedLyrics);

        int i = 0;
        for (WordFrequency wf : output) {
            assertEquals(expected.get(i).getWord(), wf.getWord());
            assertEquals(expected.get(i).getCount(), wf.getCount());
            i += 1;
        }
    }

    @Test
    void getTop100WordFrequenciesTest2() {
        List<String> processedLyrics = Arrays.asList(
                "intro", "jay", "z", "yes", "crazy", "right", "incredibly", "ya", "girl", "bee", "ya", "boy", "young",
                "intro", "beyonce", "ready", "intro", "jay", "z", "yea", "history", "making", "part", "crazy", "right",
                "verse", "beyonce", "look", "stare", "deep", "eye", "touch", "every", "time", "leave", "beg", "go", "call",
                "name", "two", "three", "time", "row", "funny", "thing", "try", "explain", "feel", "pride", "one", "blame",
                "cuz", "know", "understand", "love", "one",
                "chorus", "beyonce", "get", "look", "crazy", "right", "love", "get", "look", "crazy", "right", "love",
                "get", "look", "crazy", "right", "touch", "get", "look", "crazy", "right", "touch", "get", "hope", "page",
                "right", "kiss", "get", "hope", "save", "right", "look", "crazy", "love", "get", "look", "get", "look",
                "crazy", "love",
                "verse", "beyonce", "talk", "friend", "quietly", "think", "look", "tennis", "shoe", "even", "need", "buy",
                "new", "dress", "nobody", "impress", "way", "know", "think", "know", "beat", "heart", "skip", "still",
                "understand", "love", "one",
                "chorus", "beyonce", "get", "look", "crazy", "right", "love", "get", "look", "crazy", "right", "crazy",
                "get", "look", "crazy", "right", "touch", "love", "get", "look", "crazy", "right", "love", "get", "hope",
                "page", "right", "kiss", "hey", "get", "hope", "save", "right", "look", "crazy", "love", "hey", "get",
                "look", "get", "look", "crazy", "love", "look", "crazy", "love", "get", "look", "get", "look", "crazy",
                "love",
                "verse", "jay", "z", "beyonce", "check", "let", "go", "young", "hov", "y'", "know", "flow", "loco",
                "young", "b", "r", "o", "c", "ol'", "g", "big", "homie", "one", "stick", "bony", "pocket", "fat",
                "tony", "soprano", "roc", "handle", "van", "axel", "shake", "phoney", "man", "get", "next", "genuine",
                "article", "sing", "though", "sling", "though", "anything", "bling", "yo", "star", "ringo", "roll",
                "crazy", "bring", "ya", "whole", "set", "jay", "z", "range", "crazy", "derange", "figure", "hey",
                "insane", "yes", "sir", "cut", "different", "cloth", "texture", "good", "fur", "chinchilla", "deal",
                "chain", "smoker", "think", "get", "name", "hova", "real", "game", "fall", "back", "young", "ever",
                "since", "label", "change", "platinum", "game", "wrap", "one",
                "bridge", "beyonce", "get", "look", "crazy", "baby", "lately", "foolish", "play", "baby", "care", "cuz",
                "love", "get", "good", "baby", "make", "fool", "get", "spring", "care", "see", "cuz", "baby", "get",
                "get", "crazy", "baby", "hey",
                "chorus", "beyonce", "get", "look", "crazy", "right", "love", "love", "get", "look", "crazy", "right",
                "lookin", "crazy", "get", "look", "crazy", "right", "touch", "get", "look", "crazy", "right", "get",
                "hope", "page", "right", "kiss", "baby", "get", "hope", "save", "right", "baby", "look", "crazy", "love",
                "whoa", "get", "look", "get", "look", "crazy", "love", "whoa", "get", "look", "crazy", "right", "love",
                "get", "look", "crazy", "right", "love", "get", "look", "crazy", "right", "touch", "get", "look",
                "crazy", "right", "touch", "get", "hope", "page", "right", "kiss", "get", "hope", "save", "right",
                "look", "crazy", "love", "get", "look", "get", "look", "crazy", "love"
        );

        String topWord = "get";
        int topCount = 41;

        List<WordFrequency> output = getTop100WordFrequencies(processedLyrics);

        assertEquals(topWord, output.get(0).getWord());
        assertEquals(topCount, output.get(0).getCount());
    }
}