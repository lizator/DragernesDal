package com.example.dragernesdal.data;

import com.example.dragernesdal.data.model.ProfileDTO;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProfileDAOTest {
    ProfileDAO dao = new ProfileDAO();

    @Test
    public void getProfileByEmail() {
        ProfileDTO dto;
        try {
            dto = dao.getProfileByEmail("test@gmail.com");
        } catch (Exception e) {
            dto = null;
            e.printStackTrace();
            assertTrue(false);
        }
        assertEquals(2, dto.getId());
    }
}