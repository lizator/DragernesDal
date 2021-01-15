package com.rbyte.dragernesdal.data.user;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.user.model.ProfileDTO;

import java.io.IOException;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class UserRepository {

    private static volatile UserRepository instance;

    private ProfileDAO dao;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private ProfileDTO user = null;

    // private constructor : singleton access
    private UserRepository(ProfileDAO dao) {
        this.dao = dao;
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository(new ProfileDAO());
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        try {
            dao.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setLoggedInUser(ProfileDTO user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<ProfileDTO> getUserByEmail(String email) {
        Result<ProfileDTO> result = dao.getByEmail(email);
        if (result instanceof Result.Success) {

        }
        return result;
    }

    public Result<ProfileDTO> login(String username, String password) {
        // handle login
        try {
            Result<ProfileDTO> result = dao.login(username, password);
            if (result instanceof Result.Success) {
                setLoggedInUser(((Result.Success<ProfileDTO>) result).getData());
            }
            return result;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public Result<ProfileDTO> autoLogin(String username, String passhash) {
        // handle autologin
        try {
            Result<ProfileDTO> result = dao.autoLogin(username, passhash);
            if (result instanceof Result.Success) {
                setLoggedInUser(((Result.Success<ProfileDTO>) result).getData());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(e);
        }
    }

    public Result<ProfileDTO> createUser(ProfileDTO user) {
        Result<ProfileDTO> res = dao.createUser(user);
        return res;
    }
}