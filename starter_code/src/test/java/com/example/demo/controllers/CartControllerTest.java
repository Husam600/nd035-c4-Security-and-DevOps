package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void testAddToCart() {
        User testUser = getUser();
        Item item = getItem();
        Cart cart = getEmptyCart(testUser);
        testUser.setCart(cart);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUserCart");
        request.setItemId(1L);
        request.setQuantity(3);

        when(userRepository.findByUsername(any())).thenReturn(testUser);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Cart savedCart = (Cart) response.getBody();
        assertEquals(3, savedCart.getItems().size());
        assertEquals(Optional.of(1l), Optional.ofNullable(savedCart.getItems().get(0).getId()));
    }

    @Test
    public void testAddToCartWhenUserNotFound() {

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUserCart");
        request.setItemId(1L);
        request.setQuantity(3);

        when(userRepository.findByUsername(any())).thenReturn(null);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddToCartWhenItemNotFound() {
        User testUser = getUser();

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUserCart");
        request.setItemId(1L);
        request.setQuantity(3);

        when(userRepository.findByUsername(any())).thenReturn(testUser);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveFromCart() {
        User testUser = getUser();
        Item item = getItem();
        List<Item> items = new ArrayList<>();
        items.add(item);
        Cart cart = getNonEmptyCart(testUser, items);
        testUser.setCart(cart);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUserCart");
        request.setItemId(1L);

        when(userRepository.findByUsername(any())).thenReturn(testUser);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testRemoveFromCartWhenUserNotFound() {

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUserCart");
        request.setItemId(1L);
        request.setQuantity(3);

        when(userRepository.findByUsername(any())).thenReturn(null);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveFromCartWhenItemNotFound() {
        User testUser = getUser();

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUserCart");
        request.setItemId(1L);
        request.setQuantity(3);

        when(userRepository.findByUsername(any())).thenReturn(testUser);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private User getUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        return user;
    }

    private Item getItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("MacBook");
        item.setDescription("MacBook 2023");
        item.setPrice(BigDecimal.valueOf(1999.99));
        return item;
    }

    private Cart getEmptyCart(User user) {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        return cart;
    }

    private Cart getNonEmptyCart(User user, List<Item> items) {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(items);
        return cart;
    }

}
