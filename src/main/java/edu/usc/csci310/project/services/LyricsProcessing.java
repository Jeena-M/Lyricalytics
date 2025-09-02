package edu.usc.csci310.project.services;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

import java.util.*;

public class LyricsProcessing {

    //note: includes full nltk stopword list
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "the", "a", "an", "and", "or", "but", "if", "while", "of", "at",
            "by", "for", "with", "about", "against", "between", "into", "through",
            "during", "before", "after", "above", "below", "to", "from", "up",
            "down", "in", "out", "on", "off", "over", "under", "again", "further",
            "then", "once", "here", "there", "when", "where", "why", "how", "all",
            "any", "both", "each", "few", "more", "most", "other", "some", "such",
            "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "mmm",
            "yeah", "um", "like", "oh", "be", "it", "ah", "uh", "huh", "really", "else", "okay",
            "i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself",
            "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which",
            "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be",
            "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing",
            "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through",
            "during", "before", "after", "above", "below", "to", "from", "up", "down", "in",
            "out", "on", "off", "over", "under", "again", "further", "then", "once",
            "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few",
            "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same",
            "so", "than", "too", "very", "can", "will", "just",
            "don't", "should", "now", "ooh"
    ));


    public static List<String> processLyrics(String lyrics) {
        //splits string into sentences and tokens
        Document doc = new Document(lyrics);
        List<String> lemmas = new ArrayList<>();

        for (Sentence sentence : doc.sentences()) {
            //lemmatize
            for (String lemma : sentence.lemmas()) {
                String cleanedLemma = lemma.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
                cleanedLemma = cleanedLemma.toLowerCase();
                //remove filler words and special char
                if (cleanedLemma.length() > 2 &&
                        !STOP_WORDS.contains(cleanedLemma) &&
                        !cleanedLemma.matches(".*\\d.*")) {
                    lemmas.add(cleanedLemma);
                }
            }
        }

        //unique words only
//        List<String> uniqueLemmas = lemmas.stream().distinct().collect(Collectors.toList());
//        return uniqueLemmas;
        return lemmas;
    }

    //test string for experimentation - not included in actual repo code
    //maven command to run main: clean, compile, exec:java
//    public static void main(String[] args) {
//        String lyrics = "[Intro: Sydney Sweeney]\n" +
//                "No, seriously, get your hands off my man\n" +
//                "\n" +
//                "[Verse 1]\n" +
//                "Baby blues, undressin' him\n" +
//                "Funny how you think that I don't notice it\n" +
//                "Actin' like we're friends, we're the opposite\n" +
//                "I know what you are, tryin' so hard\n" +
//                "Runnin' 'round tryna fuck a star, go\n" +
//                "\n" +
//                "[Chorus]\n" +
//                "Look at the floor or ceilin'\n" +
//                "Or anyone else you're feelin'\n" +
//                "Take home whoever walks in\n" +
//                "Just keep your eyes off him\n" +
//                "Yes, I'm Miss Possessive\n" +
//                "Pretty girl, gon' learn your lesson\n" +
//                "Some fights you never gonna win\n" +
//                "Just keep your eyes off him\n" +
//                "\n" +
//                "[Post-Chorus]\n" +
//                "Better, better keep your, keep your, keep your, keep your\n" +
//                "Bettеr, better keep your, keep your, keep your eyеs off\n" +
//                "Better, better keep your, keep your, keep your, keep your\n" +
//                "Better, better keep your, keep your, keep your eyes off\n";
//
//        List<String> processedWords = processLyrics(lyrics);
//        System.out.println("Processed Words: " + processedWords);
//    }
}
