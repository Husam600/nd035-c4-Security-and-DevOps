package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void testFoundedUserByFindById() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> userResponseEntity = userController.findById(1L);

        assertNotNull(userResponseEntity);
        assertEquals(HttpStatus.OK, userResponseEntity.getStatusCode());
        assertNotNull(userResponseEntity.getBody());
        assertEquals(1L, userResponseEntity.getBody().getId());
        assertEquals("testUser", userResponseEntity.getBody().getUsername());
    }

    @Test
    public void testFoundedUserFindByUserName() {
        User user = new User();
        user.setId(1L);
        String userName = "testFoundUser";
        user.setUsername(userName);

        when(userRepository.findByUsername(userName)).thenReturn(user);

        ResponseEntity<User> userResponseEntity = userController.findByUserName(userName);
        assertNotNull(userResponseEntity);
        assertEquals(HttpStatus.OK, userResponseEntity.getStatusCode());
        assertNotNull(userResponseEntity.getBody());
        assertEquals(1L, userResponseEntity.getBody().getId());
        assertEquals("testFoundUser", userResponseEntity.getBody().getUsername());
    }

    @Test
    public void testNotFoundedUserFindByUserName() {

        when(userRepository.findByUsername("NotFound")).thenReturn(null);

        ResponseEntity<User> userResponseEntity = userController.findByUserName("NotFound");
        assertNotNull(userResponseEntity);
        assertEquals(HttpStatus.NOT_FOUND, userResponseEntity.getStatusCode());
    }

    @Test
    public void testCreateUser() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("Husam");
        createUserRequest.setPassword("Husam123Password");
        createUserRequest.setConfirmPassword("Husam123Password");

        when(bCryptPasswordEncoder.encode("Husam123Password")).thenReturn("hashedPassword");

        ResponseEntity<?> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof User);
        User createdUser = (User) response.getBody();
        assertEquals("Husam", createdUser.getUsername());
        assertEquals("hashedPassword", createdUser.getPassword());
    }

    @Test
    public void testCreateNewUserWithInvalidUserName() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("");
        createUserRequest.setPassword("Husam123Password");
        createUserRequest.setConfirmPassword("Password");

        when(bCryptPasswordEncoder.encode("Husam123Password")).thenReturn("hashedPassword");
        ResponseEntity<?> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof String);
        String errorMessage = (String) response.getBody();
        assertEquals("The user name can't be null or empty", errorMessage);

    }

    @Test
    public void testCreateNewUserWithAlreadyExistsUsername() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("ExistedHusam");
        createUserRequest.setPassword("Husam123Password");
        createUserRequest.setConfirmPassword("Husam123Password");

        when(bCryptPasswordEncoder.encode("Husam123Password")).thenReturn("hashedPassword");
        when(userRepository.findByUsername(any())).thenReturn(new User());
        ResponseEntity<?> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof String);
        String errorMessage = (String) response.getBody();
        assertEquals("The user name is already used", errorMessage);

    }

    @Test
    public void testCreateNewUserWithInvalidPassword() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("InvalidPasswordHusam");
        createUserRequest.setPassword("Husam123Password");
        createUserRequest.setConfirmPassword("Password");

        when(bCryptPasswordEncoder.encode("Husam123Password")).thenReturn("hashedPassword");
        ResponseEntity<?> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof String);
        String errorMessage = (String) response.getBody();
        assertEquals("Password must be at least 7 characters long and match the confirmation password.", errorMessage);

    }

}
