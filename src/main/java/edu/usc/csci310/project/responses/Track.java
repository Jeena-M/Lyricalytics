package edu.usc.csci310.project.responses;

public class Track {
    private String name;
    private int popularity;
    private Album album;

    public String getName() { return name; }
    public void setName(String na) { name = na; }

    public int getPopularity() { return popularity; }
    public void setPopularity(int p) { popularity = p; }

    public Album getAlbum() { return album; }
    public void setAlbum(Album a) { album = a; }
}
