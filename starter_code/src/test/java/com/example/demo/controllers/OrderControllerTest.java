package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    private OrderController orderController;

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void testSubmit(){
        User user = getUserWithCart();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<UserOrder> orderResponseEntity = orderController.submit(user.getUsername());
        assertNotNull(orderResponseEntity);
        assertEquals(HttpStatus.OK, orderResponseEntity.getStatusCode());
        assertNotNull(orderResponseEntity.getBody());
    }

    @Test
    public void testSubmitWhenUserIsNotFound(){
        when(userRepository.findByUsername(any())).thenReturn(null);
        ResponseEntity<UserOrder> orderResponseEntity = orderController.submit("Null user");
        assertNotNull(orderResponseEntity);
        assertEquals(HttpStatus.NOT_FOUND, orderResponseEntity.getStatusCode());
    }

    @Test
    public void testGetOrdersForUser(){
        User user = getUserWithCart();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<List<UserOrder>> orderResponseEntity = orderController.getOrdersForUser(user.getUsername());
        assertNotNull(orderResponseEntity);
        assertEquals(HttpStatus.OK, orderResponseEntity.getStatusCode());
        assertNotNull(orderResponseEntity.getBody());
    }

    @Test
    public void testGetOrdersForUserWhenUserIsNotFound(){
        when(userRepository.findByUsername(any())).thenReturn(null);
        ResponseEntity<List<UserOrder>> orderResponseEntity = orderController.getOrdersForUser("Null user");
        assertNotNull(orderResponseEntity);
        assertEquals(HttpStatus.NOT_FOUND, orderResponseEntity.getStatusCode());
    }


    private User getUserWithCart(){
        User user = new User();
        user.setUsername("TestUser");
        Cart cart = new Cart();
        cart.setItems(Collections.emptyList());
        user.setCart(cart);
        return user;
    }
}
