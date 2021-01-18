package com.rbyte.dragernesdal.data.user;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.user.model.ProfileDTO;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class PasswordHandlerTest {
    PasswordHandler ph = new PasswordHandler();

    @Test
    public void checkPass() {
        String testPass = "testPassW0rd";
        String passHash = "3dc75421b8ffe5c569c9c97b21828961";
        String Ssalt    = "d2f027f3052434ba2c00d5ff53c32a84";
        assertTrue(ph.checkPass(testPass, passHash, Ssalt));
    }

    @Test
    public void checkWrongPass() {
        String testPass = "testPassWord"; //corrct: testPassW0rd
        String passHash = "3dc75421b8ffe5c569c9c97b21828961";
        String Ssalt    = "d2f027f3052434ba2c00d5ff53c32a84";
        assertFalse(ph.checkPass(testPass, passHash, Ssalt));
    }

    @Test
    public void generatePassHash() {
        String testPass = "testPassW0rd";
        String passHash = "3dc75421b8ffe5c569c9c97b21828961";
        String Ssalt    = "d2f027f3052434ba2c00d5ff53c32a84";

        byte[] salt = new byte[16]; //converting salt to byte arr
        for (int i = 0; i < 16; i++){
            int first = 2 * i;
            int second = first + 1;
            String hex = Ssalt.charAt(first)  + "" + Ssalt.charAt(second); //getting the next 2 hexadecimals to convert to byte
            long x = Long.parseLong(hex, 16) - 128;
            salt[i] = (byte) x;
        }

        assertEquals(passHash, ph.generatePassHash(testPass, salt));
    }

    @Test
    public void encryptPassword() {
        String testPass = "testPassW0rd";
        ArrayList<String> arr = ph.encryptPassword(testPass);
        assertNotEquals(testPass, arr.get(0));
    }

    @Test
    public void login() {
        PasswordHandler ds = new PasswordHandler();
        Result<ProfileDTO> result = ds.login("test@gmail.com", "pass");
        if (result instanceof Result.Success) {
            ProfileDTO dto = ((Result.Success<ProfileDTO>) result).getData();
            assertEquals(dto.getId(), 2);
        } else {
            assertTrue(false);
        }
    }
}