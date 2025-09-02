package edu.usc.csci310.project.requests;

import edu.usc.csci310.project.models.WordFrequency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordFrequencyRequest {
    public static List<WordFrequency> getTop100WordFrequencies(List<String> processedLyrics){
        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : processedLyrics) {
            if (wordCount.containsKey(word)){
                wordCount.put(word, wordCount.get(word)+1);
            } else{
                wordCount.put(word, 1);
            }
        }

        List<WordFrequency> wordFrequencies = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            wordFrequencies.add(new WordFrequency(entry.getKey(), entry.getValue()));
        }

        wordFrequencies.sort((wf1, wf2) -> Integer.compare(wf2.getCount(), wf1.getCount()));

        return wordFrequencies.size() > 100 ? wordFrequencies.subList(0, 100) : wordFrequencies;

    }
}
