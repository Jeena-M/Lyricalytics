package edu.usc.csci310.project.models;

public class SongFrequency {
    private String song;
    private int count;

    public SongFrequency(String song, int count) {
        this.song = song;
        this.count = count;
    }
    public String getSong() {
        return song;
    }
    public void setSong(String word) {
        this.song = word;
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
