package com.rbyte.dragernesdal.ui.login;

import com.rbyte.dragernesdal.data.user.UserRepository;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoginViewModelTest {
    LoginViewModel vm = new LoginViewModel(UserRepository.getInstance());

    @Test
    public void login() {
        vm.login("test@gmail.com", "password", false);
        String name = vm.getLoginResult().getValue().getSuccess().getDisplayName();
        assertEquals("name", name);

    }
}