package com.example.dragernesdal.data;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProfileDAOTest {
    ProfileDAO dao = new ProfileDAO();

    @Test
    public void getProfileByEmail() {
        try {
            dao.getProfileByEmail("test@gmail.com");
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
        assertTrue(true);
    }
}