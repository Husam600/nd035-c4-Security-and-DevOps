package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testGetItems() {
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setId(1L);
        item.setName("iPhone");
        items.add(item);

        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> responseEntity = itemController.getItems();
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testGetItemById() {
        Item item = new Item();
        item.setId(1L);
        item.setName("iPhone");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        ResponseEntity<Item> responseEntity = itemController.getItemById(1L);
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testGetItemsByName() {
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setId(1L);
        item.setName("iPhone");
        items.add(item);

        when(itemRepository.findByName("iPhone")).thenReturn(items);
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("iPhone");
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testGetItemsByNameWhenItemNotFound() {

        when(itemRepository.findByName("iPhone")).thenReturn(null);
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("iPhone");
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
