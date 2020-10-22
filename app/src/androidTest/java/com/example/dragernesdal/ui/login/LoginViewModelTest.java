package com.example.dragernesdal.ui.login;

import com.example.dragernesdal.data.LoginRepository;
import com.example.dragernesdal.data.PasswordHandler;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoginViewModelTest {
    LoginViewModel vm = new LoginViewModel(new LoginRepository(new PasswordHandler()));

    @Test
    public void login() {
        vm.login("test@gmail.com", "testP4ssWord");


    }
}