package edu.usc.csci310.project.responses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageTest {

    @Test
    void testSetURL() {
        Image image = new Image();
        image.setURL("https://example.com/image.jpg");
        assertEquals("https://example.com/image.jpg", image.getURL());
    }

    @Test
    void testSetHeight() {
        Image image = new Image();
        image.setHeight(300);
        assertEquals(300, image.getHeight());
    }

    @Test
    void testSetWidth() {
        Image image = new Image();
        image.setWidth(500);
        assertEquals(500, image.getWidth());
    }
}

