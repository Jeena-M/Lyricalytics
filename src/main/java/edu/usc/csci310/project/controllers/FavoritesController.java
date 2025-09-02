package edu.usc.csci310.project.controllers;

import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.models.WordFrequency;
import edu.usc.csci310.project.requests.*;
import edu.usc.csci310.project.services.FavoritesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoritesController {
    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @PostMapping("/generate")
    public ResponseEntity<List<WordFrequency>> generateWordCloudFromFavorites(@RequestBody FavoritesListSongs request) {
        try {
            if (request.getUsername() == null) {

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .header("X-Error-Message", "Null username.")
                        .body(null);
            }

            List<WordFrequency> wordFrequencies = favoritesService.generateWordCloudFromFavoritesList(request.getUsername());
            return ResponseEntity.ok(wordFrequencies);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Error creating word count data based on favorites: " + e.getMessage())
                    .body(null);
        }
    }

    @PostMapping("/getFavoritesSongs")
    public ResponseEntity<List<Song>> getFavoritesSongs(@RequestBody FavoritesListSongs request) throws Exception {
        try {
            List<Song> songs = favoritesService.getFavoritesForUser(request.getUsername());
            return ResponseEntity.ok(songs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Error getting favorites songs data: " + e.getMessage())
                    .body(null);
        }
    }


    @PostMapping("/addSongToFavorites")
    public ResponseEntity<String> addSongToFavorites(@RequestBody AddSongToFavorites request) {
        try {
            int id = favoritesService.addSongToFavoritesList(request);
            if (id > 0){
                return ResponseEntity.ok("Song added with ID: " + id);
            }
            return ResponseEntity.ok("Song not added - duplicate song");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add song to favorites.");
        }
    }

    @PostMapping("/deleteAllSongs")
    public ResponseEntity<String> deleteAllSongsForUser(@RequestBody FavoritesListSongs request) {
        try {
            int deletedCount = favoritesService.deleteFavoritesByUsername(request.getUsername());
            return ResponseEntity.ok("Deleted " + deletedCount + " favorite(s) for user: " + request.getUsername());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete favorites for user: " + request.getUsername());
        }
    }

    @PostMapping("/deleteOneSong")
    public ResponseEntity<String> deleteSongFromFavorites(@RequestBody DeleteSongFromFavorites request) {
        try {
            int deletedCount = favoritesService.deleteSongByUsernameAndSong(request.getUsername(), request.getSongName(), request.getArtistName());
            return ResponseEntity.ok("Deleted " + deletedCount + " favorite for user: " + request.getUsername());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete this song for user: " + request.getUsername());
        }
    }

    @PostMapping("/moveSongUp")
    public ResponseEntity<String> moveSongUp(@RequestBody ReOrderSongRequest request){
        try {
            favoritesService.moveSongUpInFavoritesList(request.getUsername(), request.getSongName(), request.getArtistName());
            return ResponseEntity.ok("Reordered song");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to reorder song");
        }
    }

    @PostMapping("/moveSongDown")
    public ResponseEntity<String> moveSongDown(@RequestBody ReOrderSongRequest request){
        try {
            favoritesService.moveSongDownInFavoritesList(request.getUsername(), request.getSongName(), request.getArtistName());
            return ResponseEntity.ok("Reordered song");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to reorder song");
        }
    }

    @PostMapping("/updateExistingWordCloudWithFavorites")
    public ResponseEntity<List<WordFrequency>> updateExistingWordCloudWithFavorites (@RequestBody UpdateExistingWordCloudRequest request){
        try {
            List<WordFrequency> newWordCloud = favoritesService.updateExistingWordCloud(request.getUsername(), request.getWordCloud());
            return ResponseEntity.ok(newWordCloud);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Error updating word count data based on favorites: " + e.getMessage())
                    .body(null);
        }
    }

    @PostMapping("/togglePrivacyMode")
    public ResponseEntity<String> togglePrivacy (@RequestBody PrivacyRequest request){
        try {
            String result = favoritesService.togglePrivacyMode(request.getPrivacy(), request.getUsername());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to toggle privacy mode: " + e.getMessage());
        }
    }

    @GetMapping("/privacy")
    public ResponseEntity<Map<String,Boolean>> getPrivacy(@RequestParam String username) {
        try {
            boolean isPrivate = favoritesService.getPrivacyMode(username);
            return ResponseEntity.ok(Map.of("isPrivate", isPrivate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("isPrivate", false));
        }
    }
}