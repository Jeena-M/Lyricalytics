package edu.usc.csci310.project.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usc.csci310.project.responses.LyricsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
public class LyricsRequest {
    private static HttpClient client;

    @Autowired
    public LyricsRequest(HttpClient client) {
        this.client = client;
    }

    public LyricsRequest() {
        this.client = HttpClient.newHttpClient();
    }

    public static String getLyricsFromTitleArtist(String title, String artist) throws Exception {
        title = URLEncoder.encode(title, StandardCharsets.UTF_8)
                .replace("+", "%20");
        artist = URLEncoder.encode(artist, StandardCharsets.UTF_8)
                .replace("+", "%20");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.lyrics.ovh/v1/" + artist + "/" + title))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status Code: " + response.statusCode());
        if(response.statusCode() != HttpStatus.OK.value()) {
            throw new Exception(response.body());
        }
        ObjectMapper mapper = new ObjectMapper();
        LyricsResponse lyricsResponse = mapper.readValue(response.body(), LyricsResponse.class);

        String lyrics = lyricsResponse.getLyrics();
        System.out.println(lyrics);
        return lyrics;
    }

    //main for testing
//    public static void main(String[] args) throws Exception {
//        LyricsRequest lyricsRequest = new LyricsRequest(HttpClient.newHttpClient());
//        lyricsRequest.getLyricsFromTitleArtist("To Love", "Suki Waterhouse");
//    }
}