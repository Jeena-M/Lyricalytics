package edu.usc.csci310.project.responses;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemTest {

    @Test
    void testSetId() {
        Item item = new Item();
        item.setId("abc123");
        assertEquals("abc123", item.getId());
    }

    @Test
    void testSetName() {
        Item item = new Item();
        item.setName("Sample Artist");
        assertEquals("Sample Artist", item.getName());
    }

    @Test
    void testSetImages() {
        Image image1 = new Image();
        image1.setURL("http://example.com/1.jpg");
        image1.setHeight(100);
        image1.setWidth(100);

        Image image2 = new Image();
        image2.setURL("http://example.com/2.jpg");
        image2.setHeight(200);
        image2.setWidth(200);

        List<Image> images = Arrays.asList(image1, image2);

        Item item = new Item();
        item.setImages(images);

        assertEquals(2, item.getImages().size());
        assertEquals("http://example.com/1.jpg", item.getImages().get(0).getURL());
        assertEquals(200, item.getImages().get(1).getHeight());
    }
}
