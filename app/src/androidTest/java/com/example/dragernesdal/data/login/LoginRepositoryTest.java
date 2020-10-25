package com.example.dragernesdal.data.login;

import com.example.dragernesdal.data.login.LoginRepository;
import com.example.dragernesdal.data.login.PasswordHandler;
import com.example.dragernesdal.data.login.Result;
import com.example.dragernesdal.data.login.model.ProfileDTO;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoginRepositoryTest {
    LoginRepository rp = new LoginRepository(new PasswordHandler());

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