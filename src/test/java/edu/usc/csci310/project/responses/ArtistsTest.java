package edu.usc.csci310.project.responses;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArtistsTest {

    @Test
    void testSetItems() {
        Item item1 = new Item();
        Item item2 = new Item();
        List<Item> itemList = Arrays.asList(item1, item2);

        Artists artists = new Artists();
        artists.setItems(itemList);

        assertEquals(2, artists.getItems().size());
        assertEquals(item1, artists.getItems().get(0));
        assertEquals(item2, artists.getItems().get(1));
    }
}
