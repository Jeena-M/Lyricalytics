package edu.usc.csci310.project.models;

public class WordFrequency {
    private String word;
    private int count;

    public WordFrequency(String word, int count) {
        this.word = word;
        this.count = count;
    }
    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
