package com.example.dragernesdal.ui.login;

import com.example.dragernesdal.data.login.LoginRepository;
import com.example.dragernesdal.data.login.PasswordHandler;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoginViewModelTest {
    LoginViewModel vm = new LoginViewModel(new LoginRepository(new PasswordHandler()));

    @Test
    public void login() {
        vm.login("test@gmail.com", "pass");
        String name = vm.getLoginResult().getValue().getSuccess().getDisplayName();
        assertEquals("name", name);

    }
}