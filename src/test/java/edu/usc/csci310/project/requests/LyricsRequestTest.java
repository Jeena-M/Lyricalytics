package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LyricsRequestTest {

    private HttpClient client;
    private HttpResponse<String> response;
    private LyricsRequest lyricsRequest;

    @BeforeEach
    public void setUp() {
        client = mock(HttpClient.class);
        response = mock(HttpResponse.class);
        lyricsRequest = new LyricsRequest(client);
    }

    @Test
    public void testConstructor() {
        LyricsRequest lyricsReq = new LyricsRequest();
        assertTrue(lyricsReq != null);
    }

    @Test
    public void getLyricsTest() throws Exception{
        String artist = "Suki Waterhouse";
        String title = "To Love";
        String lyrics = "Is there a universe" +
                " Where our paths never crossed?" + " Where I caught your eye" +
                " But then someone arrived and we both forgot" +
                " I traveled all corners where destiny called me" +
                " And it almost killed me but I got good stories" +
                " While you were out somewhere in your own nightmare" +
                " But now we're both here" + " And we talk of how lucky we got" +
                " As we watched old lovers we dodged" +
                " While the world's falling apart" + " You make it so easy to love" +
                " To love, to love, to love, oh-oh-oh" +
                " To love, to love, to love, oh-oh-oh, oh-oh-oh" +
                " Oh, how lucky we are" + " Is there a space" +
                " Somewhere in your world that was always for me?" + " And was it your face" +
                " At the back of my mind haunting my dreams?" +
                " Kept waking up lonely with voices around me" +
                " They tell me you're out there waiting to hold me" +
                " What did they tell you? I read you were lonely, too" +
                " Now we talk of how lucky we got" + " And we could've flown on like stars" +
                " But you told me right from the start" + " I'm easy to love" +
                " To love, to love, to love, ah-ah-ah" + " To love, to love, to love, ah-ah-ah" +
                " Now I found myself this kinda love, I can't believe it" +
                " I'll never leave it behind" +
                " I thought I'd never get to feel another fucking feeling" +
                " But I feel" + " This love, this love, this love" +
                " Oh, I feel it" + " This love, this love, this love" +
                " Oh, how lucky we are" + " Oh, how lucky we are " + " Oh, how lucky we are";

        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"lyrics\": \"" + lyrics + "\"}");

        String actualLyrics = lyricsRequest.getLyricsFromTitleArtist(artist, title);
        String lyricsComp = lyrics.replaceAll("\n", " ");
        String actualLyricsComp = actualLyrics.replaceAll("\n", " ");
        assertEquals(lyricsComp, actualLyricsComp);
    }

    @Test
    public void testLyricsRequestWithInvalidTitle() throws Exception {
        String artist = "Taylor Swift";
        String title = "Blue";

        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(400);

        assertThrows(Exception.class, () -> lyricsRequest.getLyricsFromTitleArtist(title, artist));
    }

}