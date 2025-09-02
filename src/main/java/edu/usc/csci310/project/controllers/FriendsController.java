package edu.usc.csci310.project.controllers;

import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.requests.FindFriendsRequest;
import edu.usc.csci310.project.requests.SongDetailsFriendsRequest;
import edu.usc.csci310.project.services.FriendsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
public class FriendsController {
    private final FriendsService friendsService;

    public FriendsController(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @PostMapping("/getFriends")
    public ResponseEntity<Map<String, List<String>>> getFriends(@RequestBody FindFriendsRequest request) {
        Map<String, List<String>> result = new HashMap<>();
        try {
            return ResponseEntity.ok(friendsService.getComparisonList(request.getFriendname(), request.getUsername()));
        } catch (Exception e) {
            Map<String, List<String>> error = new HashMap<>();
            error.put("_error", List.of(e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/clearComparisonMap")
    public ResponseEntity<Void> clearComparisonMap(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        if (username != null) {
            friendsService.clearComparisonMapForUser(username);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/songDetailsFriendsPage")
    public ResponseEntity<Song> getSongDetailsForFriendsPage(@RequestBody SongDetailsFriendsRequest request) throws Exception {
        Song song = friendsService.getSongDetailsFromFriendsPage (request.getSongName());
        return ResponseEntity.ok(song);
    }

}