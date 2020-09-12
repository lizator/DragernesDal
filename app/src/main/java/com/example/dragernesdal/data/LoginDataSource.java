package com.example.dragernesdal.data;

import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.dragernesdal.data.model.LoggedInUser;
import com.example.dragernesdal.data.model.ProfileDTO;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<ProfileDTO> login(String email, String password) {

        try {
            ProfileDAO dao = new ProfileDAO();
            ProfileDTO user = dao.getProfileByEmail(email);
            String passS = user.getPassHash(); //passHash to check

            String[] saltS = user.getSalt().split(","); //salt for generating passHash with pass postet

            byte[] salt = new byte[16]; //converting saltS - String to salt - byte[16]
            for (int i = 0; i < 16; i++){
                salt[i] = new Integer(Integer.parseInt(saltS[i])).byteValue();
            }

            byte[] passHash2 = new byte[16];
            passHash2 = generatePassHash(password, salt); //generating new passHash with pass postet
            String passS2 = convertByteList(passHash2);

            if (passS.equals(passS2)) { //checking
                return new Result.Success<>(user);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            ProfileDAO dao = new ProfileDAO();
            try {
                dao.getConnected();
            } catch (SQLException e2){
                return new Result.Error(new IOException("Error in connecting to database", e));
            }
            return new Result.Error(new IOException("Error in Email or Password", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    private ArrayList<String> generatePassHash(String pass) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] salt = generateSalt();
        byte[] passHash = generatePassHash(pass, salt);
        String saltS = convertByteList(salt);
        String passS = convertByteList(passHash);
        ArrayList<String> arr = new ArrayList<>();
        arr.add(passHash.toString());
        arr.add(salt.toString());
        return arr;
    }

    private static byte[] generatePassHash(String pass, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] passHash = factory.generateSecret(spec).getEncoded();
        return passHash;
    }

    private static byte[] generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private String convertByteList(byte[] ls){
        String res = "";
        for (int i = 0; i < (ls.length - 1); i++){
            res += ls[i] + ",";
        }
        res += ls[ls.length];
        return res;
    }


}