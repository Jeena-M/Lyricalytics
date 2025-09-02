package edu.usc.csci310.project.responses;

import java.util.List;

public class TracksResponse {
    private List<Track> tracks;

    public TracksResponse() {}

    public List<Track> getTracks() { return tracks; }
    public void setTracks(List<Track> ts) { tracks = ts; }
}
