package com.example.dragernesdal.ui.usercreation;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dragernesdal.ui.usercreation.CreateUserFormState;
import com.example.dragernesdal.R;
import com.example.dragernesdal.data.user.PasswordHandler;
import com.example.dragernesdal.data.user.Result;
import com.example.dragernesdal.data.user.UserRepository;
import com.example.dragernesdal.data.user.model.ProfileDTO;


public class CreateUserViewModel extends ViewModel {

    private final MutableLiveData<CreateUserFormState> createUserFormState = new MutableLiveData<>();
    private final MutableLiveData<CreateUserResult> status = new MutableLiveData<CreateUserResult>();
    private UserRepository userRepository;


    public CreateUserViewModel() {
        this.userRepository = new UserRepository(new PasswordHandler());
    }

    public CreateUserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<CreateUserFormState> getCreateUserFormState() {
        return createUserFormState;
    }

    public LiveData<CreateUserResult> getStatus() {
        return status;
    }

    public void createUser(ProfileDTO user) {
        Result<ProfileDTO> result = userRepository.createUser(user);

        if (result instanceof Result.Success) {
            status.postValue(new CreateUserResult(user)); //User created
        } else {
            status.postValue(new CreateUserResult(R.string.usercreation_failed)); //Error trying to create user
        }
    }

    public void createUserDataChanged(String firstName, String lastName,
                                 String username, String password,
                                 String repeatPassword, int phone,
                                 boolean checkbox) {
        if (!isNameValid(firstName)){
            createUserFormState.setValue(new CreateUserFormState(R.string.must_be_filled, null, null, null, null, null, null));
        } else if (!isNameValid(lastName)){
            createUserFormState.setValue(new CreateUserFormState(null, R.string.must_be_filled, null, null, null, null, null));
        } else if (!isUserNameValid(username)) {
            createUserFormState.setValue(new CreateUserFormState(null, null, R.string.invalid_username, null, null, null, null));
        } else if (!isPasswordValid(password)) {
            createUserFormState.setValue(new CreateUserFormState(null, null, null, R.string.invalid_password, null, null, null));
        } else if (!isRepeatPasswordValid(password, repeatPassword)){
            createUserFormState.setValue(new CreateUserFormState(null, null, null, null, R.string.invalid_password_confirmation, null, null));
        } else if (!isPhoneValid(phone)){
            createUserFormState.setValue(new CreateUserFormState(null, null, null, null, null, R.string.invalid_phone, null));
        } else if (!checkbox) {
            createUserFormState.setValue(new CreateUserFormState(null, null, null, null, null, null, R.string.invalid_checkbox));
        } else {
            createUserFormState.setValue(new CreateUserFormState(true));
        }
    }

    private boolean isNameValid(String name) {
        if (name == null){
            return false;
        }
        return !name.trim().isEmpty();
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        }
        return false;
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    private boolean isRepeatPasswordValid (String password, String repeatPassword) {
        return password.equals(repeatPassword);
    }

    private boolean isPhoneValid(int phone){
        return String.valueOf(Math.abs(phone)).length() == 8;
    }

}
