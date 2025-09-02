package edu.usc.csci310.project.requests;

public class SongDetailsFriendsRequest {
    private String songName;

    public SongDetailsFriendsRequest() {}

    public SongDetailsFriendsRequest(String songName) {
        this.songName = songName;
    }

    public String getSongName() {
        return songName;
    }
}
