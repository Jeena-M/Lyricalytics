package edu.usc.csci310.project.controllers;


import edu.usc.csci310.project.models.Artist;
import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.models.SongFrequency;
import edu.usc.csci310.project.models.WordFrequency;
import edu.usc.csci310.project.requests.*;
import edu.usc.csci310.project.services.WordCloudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wordcloud")
public class WordCloudController {
    private final WordCloudService wordCloudService;

    public WordCloudController(WordCloudService wordCloudService) {
        this.wordCloudService = wordCloudService;
    }

    //word_table -- returns words in wordcloud + word frequency
    //song_table -- for a given word returns songs with that word + its frequency in the lyrics
    //details -- returns song's artist, year, lyrics
   @PostMapping("/generate")
   public ResponseEntity<List<WordFrequency>> generateWordCloud(@RequestBody WordCloudGenerateRequest request) {
       try {
           if (request.getArtistName() == null || request.getArtistName().isEmpty()) {

               return ResponseEntity
                       .status(HttpStatus.BAD_REQUEST)
                       .header("X-Error-Message", "Artist name cannot be empty.")
                       .body(null);
           }
            System.out.println(request.getUsername());
            List<WordFrequency> wordFrequencies = wordCloudService.generateWordCloud(request.getArtistName(), request.getSongCount(), request.getUsername());
           return ResponseEntity.ok(wordFrequencies);
       } catch (Exception e) {
           e.printStackTrace();
           return ResponseEntity
                   .status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .header("X-Error-Message", "Error creating word count data: " + e.getMessage())
                   .body(null);
       }
   }

    @PostMapping("/generateNoNumber")
    public ResponseEntity<List<WordFrequency>> generateWordCloudNoNumber(@RequestBody WordCloudNoNumberGenerateRequest request) {
        try {
            if (request.getArtistName() == null || request.getArtistName().isEmpty()) {

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .header("X-Error-Message", "Artist name cannot be empty.")
                        .body(null);
            }

            List<WordFrequency> wordFrequencies = wordCloudService.generateWordCloudNoNumber(request.getArtistName(), request.getUsername());
            return ResponseEntity.ok(wordFrequencies);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Error creating word count data: " + e.getMessage())
                    .body(null);
        }
    }

    @PostMapping("/songFrequencyForWordInLyrics")
    public ResponseEntity<List<SongFrequency>> getSongFrequencyForWordInLyrics(@RequestBody SongFrequencyRequest request) throws Exception {
        try {
            List<SongFrequency> songFrequencies = wordCloudService.getSongFrequencyInLyrics(request.getWord(), request.getArtistName(), request.getSongCount(), request.getUsername());
            return ResponseEntity.ok(songFrequencies);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Error creating song count data: " + e.getMessage())
                    .body(null);
        }
    }

    @PostMapping("/songFrequencyForWordInLyricsNoNumber")
    public ResponseEntity<List<SongFrequency>> getSongFrequencyForWordInLyricsNoNumber(@RequestBody SongFrequencyRequest request) throws Exception {
        try {
            List<SongFrequency> songFrequencies = wordCloudService.getSongFrequencyInLyricsNoCount(request.getWord(), request.getArtistName(), request.getSongCount(), request.getUsername());
            return ResponseEntity.ok(songFrequencies);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Error creating song count data: " + e.getMessage())
                    .body(null);
        }
    }

    @PostMapping("/songDetails")
    public ResponseEntity<Song> getSongDetails(@RequestBody SongDetailRequest request) throws Exception {
        Song song = wordCloudService.getSongDetails(request.getSongName(), request.getArtistName(), request.getUsername());
        return ResponseEntity.ok(song);
    }

    @PostMapping("/songDetailsFavorites")
    public ResponseEntity<Song> getSongDetailsFavorites(@RequestBody SongDetailsFavorites request) throws Exception {
        Song song = wordCloudService.getSongDetailsFavorites(request.getSongName(), request.getArtistName());
        return ResponseEntity.ok(song);
    }

    @PostMapping("/artists")
    public ResponseEntity<List<Artist>> getAmbiguousArtists(@RequestBody ArtistsRequest request) throws Exception {
        List<Artist> artists = wordCloudService.getAmbiguousArtists(request.getArtistName());
        return ResponseEntity.ok(artists);
    }

    @PostMapping("/addSong")
    public ResponseEntity<List<String>> getSongsForArtist(@RequestBody ArtistsRequest request) throws Exception {
        List<String> songs = wordCloudService.getSongsForArtist(request.getArtistName());
        return ResponseEntity.ok(songs);
    }

    @PostMapping("/generateFromList")
    public ResponseEntity<List<WordFrequency>> generateWordCloudFromList(@RequestBody WordCloudFromListGenerateRequest request) {
        try {
            if (request.getArtistName() == null || request.getArtistName().isEmpty()) {

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .header("X-Error-Message", "Artist name cannot be empty.")
                        .body(null);
            }

            if(request.getSongs() == null || request.getSongs().isEmpty()){
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .header("X-Error-Message", "No songs selected.")
                        .body(null);
            }

            List<WordFrequency> wordFrequencies = wordCloudService.generateWordCloudFromSongList(request.getArtistName(), request.getSongs(), request.getUsername());
            return ResponseEntity.ok(wordFrequencies);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Error creating word count data: " + e.getMessage())
                    .body(null);
        }
    }

    @PostMapping("/updateExistingWordCloudFromList")
    public ResponseEntity<List<WordFrequency>> updateExistingWordCloudWithList (@RequestBody UpdateWordCloudFromSongList request){
        try {
            List<WordFrequency> newWordCloud = wordCloudService.updateWordCloudFromSongList(request.getArtist(), request.getSongs(), request.getUsername(), request.getWordCloudJson());
            return ResponseEntity.ok(newWordCloud);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Error updating word count data based on favorites: " + e.getMessage())
                    .body(null);
        }
    }

    @PostMapping("/updateExistingWordCloudWithSearchSongCount")
    public ResponseEntity<List<WordFrequency>> updateExistingWordCloudSearchSongCount (@RequestBody UpdateExistingWordCloudWithSearchWithSongCount request){
        try {
            List<WordFrequency> newWordCloud = wordCloudService.updateWordCloudFromSongListWithSongCount(request.getArtist(), request.getSongCount(), request.getUsername(), request.getWordCloudJson());
            return ResponseEntity.ok(newWordCloud);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Error updating word count data based on favorites: " + e.getMessage())
                    .body(null);
        }
    }
}

