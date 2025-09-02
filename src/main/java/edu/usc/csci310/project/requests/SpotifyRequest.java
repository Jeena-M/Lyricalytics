package edu.usc.csci310.project.requests;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usc.csci310.project.responses.ArtistsResponse;
import edu.usc.csci310.project.responses.Item;
import edu.usc.csci310.project.responses.ManyTracksResponse;
import edu.usc.csci310.project.responses.Track;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static edu.usc.csci310.project.requests.TokenRequest.getAccessToken;

@Component
public class SpotifyRequest {
    private static HttpClient client;
    private static TokenRequest tokenRequest;

    public SpotifyRequest(HttpClient client) {
        this.client = client;
        this.tokenRequest = new TokenRequest();
    }

    public SpotifyRequest() {
        this.client = HttpClient.newHttpClient();
        this.tokenRequest = new TokenRequest();
    }

    public List<Item> getPossibleArtists(String name) throws Exception {
        String urlName = URLEncoder.encode(name, StandardCharsets.UTF_8)
                .replace("+", "%20");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/search?q="+urlName+"&type=artist&limit=20"))
                .header("Authorization", "Bearer " + getAccessToken())
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status Code: " + response.statusCode());
//        System.out.println(response.body());
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ArtistsResponse artistsResponse = mapper.readValue(response.body(), ArtistsResponse.class);

        if(artistsResponse.getArtists().getItems().isEmpty()) {
            throw new Exception("No matching artist found");
        }

        boolean relevantResults = false;
        List<Item> artists = new ArrayList<>();
        for(Item item : artistsResponse.getArtists().getItems()) {
            if(item.getName().toLowerCase().equals(name.toLowerCase())){
                artists.add(item);
                System.out.println(artists.get(0).getName());
                return artists;
            }
            if(item.getName().toLowerCase().contains(name.toLowerCase())){
                relevantResults = true;
                artists.add(item);
            }
        }
        if(!relevantResults) {
            throw new Exception("No matching artist found");
        }
        for(Item item : artists) {
            System.out.println(item.getName());
        }
        return artists;
    }

    public List<String> getNumPopularSongs(String name, int num) throws Exception {
        List<Item> artists = getPossibleArtists(name);
        //check if found the one specific artist (don't need popup)
        if(artists.size() != 1){
            throw new Exception("Found many artists");
        }
        String urlName = URLEncoder.encode(artists.get(0).getName(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/search?q="+urlName+"&type=track&limit=50"))
                .header("Authorization", "Bearer " + getAccessToken())
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status Code: " + response.statusCode());
//        System.out.println(response.body());
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ManyTracksResponse manyTracksResponse = mapper.readValue(response.body(), ManyTracksResponse.class);
        List<Track> returnedTracks = manyTracksResponse.getTracks().getItems();

        if(returnedTracks.isEmpty()) {
            //no songs found error
            throw new Exception("No songs found");
        }

        //sort tracks by popularity (descending)
        returnedTracks.sort(Comparator.comparingInt(Track::getPopularity).reversed());

        if(num > 50){
            System.out.println("Error, can get at most 50 tracks");
            num = 50;
        }

        List<String> songs = new ArrayList<String>();
        for(int i = 0; i < num; i++){
            songs.add(returnedTracks.get(i).getName());
            System.out.println(returnedTracks.get(i).getName());
        }

        return songs;
    }

    //will return 50 songs by this artist
    public List<String> getAllSongsForArtist(String name) throws Exception {
        List<Item> artists = getPossibleArtists(name);
        //check if found the one specific artist (don't need popup)
        if(artists.size() != 1){
            throw new Exception("Found many artists");
        }
        String urlName = URLEncoder.encode(artists.get(0).getName(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/search?q="+urlName+"&type=track&limit=50"))
                .header("Authorization", "Bearer " + getAccessToken())
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status Code: " + response.statusCode());
//        System.out.println(response.body());
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ManyTracksResponse manyTracksResponse = mapper.readValue(response.body(), ManyTracksResponse.class);

        if(manyTracksResponse.getTracks().getItems().isEmpty()) {
            //no songs found error
            throw new Exception("No songs found");
        }

        Set<String> uniqueSongs = new HashSet<>();
        List<String> songs = new ArrayList<String>();
        for(Track track : manyTracksResponse.getTracks().getItems()){
            if(!uniqueSongs.contains(track.getName())){
                uniqueSongs.add(track.getName());
                songs.add(track.getName());
                System.out.println(track.getName());
            }
        }

        return songs;
    }

    //returns release date for a given song and artist
    public String getReleaseDateForSong(String song, String artist) throws Exception {
        String urlSong = URLEncoder.encode(song, StandardCharsets.UTF_8)
                .replace("+", "%20");
        String urlArtist = URLEncoder.encode(artist, StandardCharsets.UTF_8)
                .replace("+", "%20");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/search?q="+urlSong+"+"+urlArtist+"&type=track&limit=1"))
                .header("Authorization", "Bearer " + getAccessToken())
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status Code: " + response.statusCode());
        System.out.println(response.body());
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ManyTracksResponse manyTracksResponse = mapper.readValue(response.body(), ManyTracksResponse.class);

        if(manyTracksResponse.getTracks().getItems().isEmpty()) {
            //no songs found error
            throw new Exception("No songs found");
        }

        System.out.println(manyTracksResponse.getTracks().getItems().get(0).getAlbum().getName());
        String date = manyTracksResponse.getTracks().getItems().get(0).getAlbum().getRelease_date();
        date = date.substring(0, 4);
        System.out.println(date);

        return date;
    }

    //main for testing
//    public static void main(String[] args) throws Exception {
//    SpotifyRequest request = new SpotifyRequest();
//    request.getPossibleArtists("Taylor");
//        getPossibleArtists("Justin");
//    request.getNumPopularSongs("Taylor Swift", 51);
//    request.getAllSongsForArtist("Taylor Swift");
//        request.getReleaseDateForSong("July", "Hozier");
//    }
}