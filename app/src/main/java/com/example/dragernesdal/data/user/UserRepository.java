package com.example.dragernesdal.data.user;

import com.example.dragernesdal.data.Result;
import com.example.dragernesdal.data.user.model.ProfileDTO;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class UserRepository {

    private static volatile UserRepository instance;

    private PasswordHandler passwordHandler;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private ProfileDTO user = null;

    // private constructor : singleton access
    public UserRepository(PasswordHandler passwordHandler) {
        this.passwordHandler = passwordHandler;
    }

    public static UserRepository getInstance(PasswordHandler dataSource) {
        if (instance == null) {
            instance = new UserRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        passwordHandler.logout();
    }

    private void setLoggedInUser(ProfileDTO user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<ProfileDTO> login(String username, String password) {
        // handle login
        Result<ProfileDTO> result = passwordHandler.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<ProfileDTO>) result).getData());
        }
        return result;
    }

    public Result<ProfileDTO> autologin(String username, String passhash) {
        // handle login
        Result<ProfileDTO> result = passwordHandler.autologin(username, passhash);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<ProfileDTO>) result).getData());
        }
        return result;
    }

    public Result<ProfileDTO> createUser(ProfileDTO user) {
        Result<ProfileDTO> res = passwordHandler.createUser(user);
        return res;
    }
}