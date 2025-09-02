package edu.usc.csci310.project.requests;

public class FindFriendsRequest {
    private String username;
    private String friendname;

    public FindFriendsRequest() {}

    public FindFriendsRequest(String username, String friendname) {
        this.username = username;
        this.friendname = friendname;
    }

    public String getUsername() {
        return username;
    }

    public String getFriendname() {
        return friendname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFriendname(String friendname) {
        this.friendname = friendname;
    }
}
