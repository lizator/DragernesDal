package com.example.dragernesdal.data.user;

import com.example.dragernesdal.data.user.model.ProfileDTO;

import org.junit.Test;

import static org.junit.Assert.*;

public class userRepositoryTest {
    UserRepository rp = new UserRepository(new PasswordHandler());

    @Test
    public void getInstance() {
        assertTrue(false);
    }

    @Test
    public void isLoggedIn() {
        assertTrue(false);
    }

    @Test
    public void logout() {
        assertTrue(false);
    }

    @Test
    public void login() {
        Result<ProfileDTO> result = rp.login("test@gmail.com", "password");
        if (result instanceof Result.Success) {
            ProfileDTO dto = ((Result.Success<ProfileDTO>) result).getData();
            assertEquals(dto.getId(), 2);
        } else {
            assertTrue(false);
        }
    }
}