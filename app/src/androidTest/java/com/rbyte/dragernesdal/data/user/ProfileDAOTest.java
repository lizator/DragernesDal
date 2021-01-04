package com.rbyte.dragernesdal.data.user;

import com.rbyte.dragernesdal.data.user.model.ProfileDTO;

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