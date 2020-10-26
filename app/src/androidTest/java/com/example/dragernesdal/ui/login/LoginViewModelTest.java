package com.example.dragernesdal.ui.login;

import com.example.dragernesdal.data.user.UserRepository;
import com.example.dragernesdal.data.user.PasswordHandler;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoginViewModelTest {
    LoginViewModel vm = new LoginViewModel(new UserRepository(new PasswordHandler()));

    @Test
    public void login() {
        vm.login("test@gmail.com", "pass");
        String name = vm.getLoginResult().getValue().getSuccess().getDisplayName();
        assertEquals("name", name);

    }
}