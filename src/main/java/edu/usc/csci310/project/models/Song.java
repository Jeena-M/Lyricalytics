package edu.usc.csci310.project.models;

public class Song {
    private String title;
    private String artist;
    private String year;
    private String lyrics;

    public Song (String title, String artist, String year, String lyrics) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.lyrics = lyrics;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getYear() {
        return year;
    }

    public String getLyrics() {
        return lyrics;
    }
}
