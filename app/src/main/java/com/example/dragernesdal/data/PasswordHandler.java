package com.example.dragernesdal.data;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHandler {

    public PasswordHandler() {
    }

    public static boolean checkPass(String pass, String passHash, String Ssalt) {
        byte[] salt = new byte[16]; //converting salt to byte arr
        for (int i = 0; i < 16; i++){
            int first = 2 * i;
            int second = first + 1;
            String hex = Ssalt.charAt(first)  + "" + Ssalt.charAt(second); //getting the next 2 hexadecimals to convert to byte
            long x = Long.parseLong(hex, 16) - 128; //Converting back to having numbers between -128 - 127
            salt[i] = (byte) x;
        }
        String genPassHash = generatePassHash(pass, salt); //Generates passhash from password getting testet and given salt
        return genPassHash.equals(passHash); // returns true if password is correct
    }

    public static String generatePassHash(String pass, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] passHash = factory.generateSecret(spec).getEncoded();
            String genPass = "";
            for (int i = 0; i < 16; i++) {
                String gen = Integer.toHexString(passHash[i] + 128); //adding 128 to have numbers between 0 - 255
                if (gen.length() < 2) {
                    gen = "0" + gen;
                }
                genPass += gen;
            }
            return genPass;
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    // Generates passhash and new string
    public static ArrayList<String> encryptPassword (String pass) {
        ArrayList<String> ret = new ArrayList<String>();
        try {
            byte[] salt = generateSalt();
            KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1"); //https://www.baeldung.com/java-password-hashing#2-implementing-pbkdf2-in-java
            byte[] passHash = factory.generateSecret(spec).getEncoded();
            String genPass = "";
            String genSalt = "";
            for (int i = 0; i < 16; i++) {
                genPass += Integer.toHexString(passHash[i] + 128);
                genSalt += Integer.toHexString(salt[i] + 128);
            }
            ret.add(genPass);
            ret.add(genSalt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static byte[] generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

}
