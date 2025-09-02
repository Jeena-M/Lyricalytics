package edu.usc.csci310.project.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usc.csci310.project.models.WordFrequency;

import java.util.ArrayList;
import java.util.List;

public class WordCloudParser {
    public static List<WordFrequency> parseWordFrequencies(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(json);
        JsonNode root = mapper.readTree(json);
        List<WordFrequency> result = new ArrayList<>();

        for (JsonNode node : root) {
            String word = node.get("text").asText();
            int count = node.get("value").asInt();
            result.add(new WordFrequency(word, count));
        }

        return result;
    }
}


