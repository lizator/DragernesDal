package com.example.dragernesdal.data;

import com.example.dragernesdal.data.model.ProfileDTO;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoginDataSourceTest {

    @Test
    public void login() {
        LoginDataSource ds = new LoginDataSource();
        Result<ProfileDTO> result = ds.login("test@gmail.com", "This is password");
        ProfileDTO dto = ((Result.Success<ProfileDTO>) result).getData();
        assertEquals(dto.getId(), 2);
    }
}