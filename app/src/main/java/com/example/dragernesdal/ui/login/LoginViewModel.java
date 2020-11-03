package com.example.dragernesdal.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.dragernesdal.data.user.UserRepository;
import com.example.dragernesdal.data.user.Result;
import com.example.dragernesdal.R;
import com.example.dragernesdal.data.user.model.ProfileDTO;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private UserRepository userRepository;

    LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password, boolean autoLogin) {
        // can be launched in a separate asynchronous job cause postValue can be in background
        Result<ProfileDTO> result;
        if (autoLogin) {
            result = userRepository.autologin(username, password);
        } else {
            result = userRepository.login(username, password);
        }

        if (result instanceof Result.Success) {
            ProfileDTO data = ((Result.Success<ProfileDTO>) result).getData();
            loginResult.postValue(new LoginResult(new LoggedInUserView(data.getFirstName(), data.getEmail(), data.getPassHash())));
        } else {
            loginResult.postValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}